package store.myproject.onlineshop.global.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import store.myproject.onlineshop.global.token.JwtFilter;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomerRepository customerRepository;
    private final JwtUtils jwtUtils;

    private static final String[] SWAGGER_AUTH = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/webjars/**",
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
                                // Swagger 허용
                                .requestMatchers(SWAGGER_AUTH).permitAll()

                                // 비회원 접근 허용
                                .requestMatchers(
                                        "/api/v1/customers/join",
                                        "/api/v1/customers/login",
                                        "/api/v1/corporations/join",
                                        "/api/v1/corporations/login",
                                        "/api/v1/customers/email",
                                        "/api/v1/customers/nickName",
                                        "/api/v1/brands/*",
                                        "/api/v1/memberships",
                                        "/api/v1/memberships/*",
                                        "/api/v1/items/*",       // 품목 단건 조회
                                        "/api/v1/*/likes"       // 좋아요 수 조회 (GET 기준)
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
                                        "/api/v1/orders/search",
                                        "/api/v1/carts"
                                ).authenticated()

                                .requestMatchers(HttpMethod.POST,
                                        "/api/v1/customers/reissue",
                                        "/api/v1/customers/logout",
                                        "/api/v1/accounts",
                                        "/api/v1/recipes",
                                        "/api/v1/orders",
                                        "/api/v1/orders/cart",
                                        "/api/v1/carts",
                                        "/api/v1/carts/*",
                                        "/api/v1/*/likes" // 좋아요 누르기
                                ).authenticated()

                                .requestMatchers(HttpMethod.PUT,
                                        "/api/v1/customers",
                                        "/api/v1/accounts",
                                        "/api/v1/recipes/*/reviews/*",
                                        "/api/v1/deliveries/*"
                                ).authenticated()

                                .requestMatchers(HttpMethod.DELETE,
                                        "/api/v1/customers",
                                        "/api/v1/accounts",
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
                .addFilterBefore(new JwtFilter(customerRepository, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
