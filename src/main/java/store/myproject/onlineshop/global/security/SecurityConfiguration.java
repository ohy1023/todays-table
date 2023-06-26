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
import store.myproject.onlineshop.global.token.JwtExceptionFilter;
import store.myproject.onlineshop.global.token.JwtFilter;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.repository.CustomerRepository;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomerRepository customerRepository;
    private final JwtUtils jwtUtils;

    private static final String[] SWAGGER_AUTH = {
            "/api-docs/swagger-config/**",
            "/swagger-ui.html/**",
            "/swagger-ui/**",
            "/api-docs/**",
            "/swagger-resources/**",
            "/webjars/**",
    };

    private static final String[] GET_AUTH_ADMIN = {
    };

    private static final String[] POST_AUTH_ADMIN = {
            "/api/v1/brands",
    };

    private static final String[] PATCH_AUTH_ADMIN = {
            "/api/v1/brands/{id}",
    };

    private static final String[] DELETE_AUTH_ADMIN = {
            "/api/v1/brands/{id}",
    };

    private static final String[] GET_AUTH_USER = {
//            "/api/v1/customers"
    };

    private static final String[] POST_AUTH_USER = {
            "/api/v1/customers/reissue",
            "/api/v1/customers/logout",

    };
    private static final String[] PATCH_AUTH_USER = {
            "/api/v1/customers",
    };
    private static final String[] DELETE_AUTH_USER = {
            "api/v1/customers",
    };

    private static final String[] PERMIT_ALL = {
            "/api/v1/customers/join",
            "/api/v1/customers/login",
            "/api/v1/customers/email",
            "/api/v1/customers/nickName",
            "/api/v1/brands/search",
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
                        .requestMatchers(SWAGGER_AUTH).permitAll()
                        .requestMatchers(PERMIT_ALL).permitAll()
                        .requestMatchers(HttpMethod.GET, GET_AUTH_ADMIN).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, POST_AUTH_ADMIN).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, PATCH_AUTH_ADMIN).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, DELETE_AUTH_ADMIN).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, GET_AUTH_USER).authenticated()
                        .requestMatchers(HttpMethod.POST, POST_AUTH_USER).authenticated()
                        .requestMatchers(HttpMethod.PUT, PATCH_AUTH_USER).authenticated()
                        .requestMatchers(HttpMethod.DELETE, DELETE_AUTH_USER).authenticated()
                        .anyRequest().permitAll()
                )
                .exceptionHandling().accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                .addFilterBefore(new JwtFilter(customerRepository, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtFilter.class)
                .build();
    }

}
