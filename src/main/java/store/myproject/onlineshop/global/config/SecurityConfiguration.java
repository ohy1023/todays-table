package store.myproject.onlineshop.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.myproject.onlineshop.global.securityhandler.CustomAccessDeniedHandler;
import store.myproject.onlineshop.global.securityhandler.CustomAuthenticationEntryPoint;
import store.myproject.onlineshop.global.token.JwtFilter;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.service.CustomerService;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    private static final String[] SWAGGER_AUTH = {
            "/swagger-ui.html/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/v3/api-docs/**"
    };

    private static final String[] STATIC_RESOURCES = {
            "/",
            "/favicon.ico",
            "/css/**",
            "/js/**",
            "/images/**",
            "/webjars/**"
    };


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors().and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authorize -> authorize

                        // 정적 자원 허용
                        .requestMatchers(STATIC_RESOURCES).permitAll()

                        // Swagger 허용
                        .requestMatchers(SWAGGER_AUTH).permitAll()

                        // 비회원 접근 허용
                        .requestMatchers(
                                "/api/v1/customers/join",
                                "/api/v1/customers/login",
                                "/api/v1/customers/email",
                                "/api/v1/customers/nickname",
                                "/api/v1/corporations/join",
                                "/api/v1/corporations/login",
                                "/api/v1/brands/*",
                                "/api/v1/memberships",
                                "/api/v1/memberships/*",
                                "/api/v1/items/*",       // 품목 단건 조회
                                "/api/v1/recipes",
                                "/api/v1/recipes/*/reviews",
                                "/api/v1/recipes/*/reviews/*/replies",
                                "/api/v1/items/*/recipes",
                                "/api/v1/items/test/*",
                                "/api/v1/recipes/test/*"
                        ).permitAll()

                        // ADMIN 전용
                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/brands",
                                "/api/v1/memberships",
                                "/api/v1/recipes",
                                "/api/v1/items"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/brands/*",
                                "/api/v1/memberships/*",
                                "/api/v1/recipes/*/reviews/*",
                                "/api/v1/items/*"
                        ).hasRole("ADMIN")

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/brands/*",
                                "/api/v1/memberships/*",
                                "/api/v1/recipes/*/reviews/*",
                                "/api/v1/items/*"
                        ).hasRole("ADMIN")

                        // USER 이상 접근 허용
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/accounts",
                                "/api/v1/orders/*",
                                "/api/v1/orders",
                                "/api/v1/recipes/*",
                                "/api/v1/recipes/meta/*",
                                "/api/v1/carts",
                                "/api/v1/items",
                                "/api/v1/customers"
                        ).authenticated()

                        .requestMatchers(HttpMethod.POST,
                                "/api/v1/customers/reissue",
                                "/api/v1/customers/logout",
                                "/api/v1/accounts",
                                "/api/v1/recipes",
                                "/api/v1/recipes/image",
                                "/api/v1/recipes/*/reviews",
                                "/api/v1/orders",
                                "/api/v1/orders/cart",
                                "/api/v1/orders/preparation",
                                "/api/v1/orders/verification",
                                "/api/v1/orders/rollback",
                                "/api/v1/carts",
                                "/api/v1/carts/*",
                                "/api/v1/recipes/*/likes" // 좋아요 누르기
                        ).authenticated()

                        .requestMatchers(HttpMethod.PUT,
                                "/api/v1/customers",
                                "/api/v1/customers/password",
                                "/api/v1/customers/temp-password",
                                "/api/v1/accounts",
                                "/api/v1/recipes/*/reviews/*",
                                "/api/v1/orders/*"
                        ).authenticated()

                        .requestMatchers(HttpMethod.DELETE,
                                "/api/v1/customers",
                                "/api/v1/accounts",
                                "/api/v1/recipes/*",
                                "/api/v1/recipes/*/reviews/*",
                                "/api/v1/orders/*",
                                "/api/v1/carts",
                                "/api/v1/carts/*"
                        ).authenticated()

                        // 나머지 모두 차단
                        .anyRequest().denyAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .addFilterBefore(new JwtFilter(customerService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
