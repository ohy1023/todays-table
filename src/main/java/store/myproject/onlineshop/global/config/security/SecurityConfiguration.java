package store.myproject.onlineshop.global.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import store.myproject.onlineshop.global.config.security.securityhandler.CustomAccessDeniedHandler;
import store.myproject.onlineshop.global.config.security.securityhandler.CustomAuthenticationEntryPoint;
import store.myproject.onlineshop.global.config.security.filter.JwtFilter;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.service.CustomerService;

import java.util.Arrays;
import java.util.List;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final CustomerService customerService;
    private final JwtUtils jwtUtils;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .httpBasic().disable()
                .csrf().disable()
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(this::configureAuthorization)
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(new CustomAccessDeniedHandler())
                        .authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                )
                .addFilterBefore(new JwtFilter(customerService, jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    private void configureAuthorization(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        // Swagger 문서 접근 허용
        configureSwaggerAccess(authorize);

        // 각 도메인별 권한 설정
        configureBrandAccess(authorize);
        configureCartAccess(authorize);
        configureCustomerAccess(authorize);
        configureItemAccess(authorize);
        configureLikeAccess(authorize);
        configureMembershipAccess(authorize);
        configureRecipeAccess(authorize);
        configureReviewAccess(authorize);
        configureOrderAccess(authorize);

        // 나머지 모든 요청 차단
        authorize.anyRequest().denyAll();
    }

    private void configureSwaggerAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] swaggerPaths = {
                "/swagger-ui.html/**", "/swagger-ui/**", "/api-docs/**",
                "/swagger-resources/**", "/v3/api-docs/**"
        };
        authorize.requestMatchers(swaggerPaths).permitAll();
    }

    private void configureBrandAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] brandGetPaths = {"/api/v1/brands", "/api/v1/brands/*"};
        String[] brandPostPaths = {"/api/v1/brands"};
        String[] brandPutPaths = {"/api/v1/brands/*"};
        String[] brandDeletePaths = {"/api/v1/brands/*"};

        authorize.requestMatchers(HttpMethod.GET, brandGetPaths).permitAll();
        authorize.requestMatchers(HttpMethod.POST, brandPostPaths).hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.PUT, brandPutPaths).hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, brandDeletePaths).hasRole("ADMIN");
    }

    private void configureCartAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] cartPostPublicPaths = {"/api/v1/carts/init-cart-items"};
        String[] cartGetPaths = {"/api/v1/carts"};
        String[] cartPostPaths = {"/api/v1/carts"};
        String[] cartDeletePaths = {"/api/v1/carts", "/api/v1/carts/*"};

        authorize.requestMatchers(HttpMethod.POST, cartPostPublicPaths).permitAll();
        authorize.requestMatchers(HttpMethod.GET, cartGetPaths).authenticated();
        authorize.requestMatchers(HttpMethod.POST, cartPostPaths).authenticated();
        authorize.requestMatchers(HttpMethod.DELETE, cartDeletePaths).authenticated();
    }

    private void configureCustomerAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] customerPostPublicPaths = {
                "/api/v1/customers/join", "/api/v1/customers/login", "/api/v1/customers/reissue",
                "/api/v1/customers/email", "/api/v1/customers/nickname"
        };
        String[] customerGetPaths = {"/api/v1/customers"};
        String[] customerPostAuthPaths = {
                "/api/v1/customers/logout", "/api/v1/customers/temp-password", "/api/v1/customers/password"
        };
        String[] customerPutPaths = {"/api/v1/customers"};
        String[] customerDeletePaths = {"/api/v1/customers"};

        authorize.requestMatchers(HttpMethod.POST, customerPostPublicPaths).permitAll();
        authorize.requestMatchers(HttpMethod.GET, customerGetPaths).authenticated();
        authorize.requestMatchers(HttpMethod.POST, customerPostAuthPaths).authenticated();
        authorize.requestMatchers(HttpMethod.PUT, customerPutPaths).authenticated();
        authorize.requestMatchers(HttpMethod.DELETE, customerDeletePaths).authenticated();
    }

    private void configureItemAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] itemGetPaths = {"/api/v1/items/*", "/api/v1/items", "/api/v1/items/*/recipes"};
        String[] itemPostPaths = {"/api/v1/items"};
        String[] itemPutPaths = {"/api/v1/items/*"};
        String[] itemDeletePaths = {"/api/v1/items/*"};

        authorize.requestMatchers(HttpMethod.GET, itemGetPaths).permitAll();
        authorize.requestMatchers(HttpMethod.POST, itemPostPaths).hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.PUT, itemPutPaths).hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, itemDeletePaths).hasRole("ADMIN");
    }

    private void configureLikeAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] likePostPaths = {"/api/v1/recipes/*/likes"};

        authorize.requestMatchers(HttpMethod.POST, likePostPaths).authenticated();
    }

    private void configureMembershipAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] membershipGetPaths = {"/api/v1/memberships/*", "/api/v1/memberships"};
        String[] membershipPutPaths = {"/api/v1/memberships/*"};
        String[] membershipDeletePaths = {"/api/v1/memberships/*"};

        authorize.requestMatchers(HttpMethod.GET, membershipGetPaths).permitAll();
        authorize.requestMatchers(HttpMethod.PUT, membershipPutPaths).hasRole("ADMIN");
        authorize.requestMatchers(HttpMethod.DELETE, membershipDeletePaths).hasRole("ADMIN");
    }

    private void configureRecipeAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] recipeGetPaths = {
                "/api/v1/recipes/*", "/api/v1/recipes/*/meta", "/api/v1/recipes", "/api/v1/recipes/test/*"
        };
        String[] recipePostPaths = {"/api/v1/recipes", "/api/v1/recipes/image"};
        String[] recipePutPaths = {"/api/v1/recipes/*"};
        String[] recipeDeletePaths = {"/api/v1/recipes/*"};

        authorize.requestMatchers(HttpMethod.GET, recipeGetPaths).permitAll();
        authorize.requestMatchers(HttpMethod.POST, recipePostPaths).authenticated();
        authorize.requestMatchers(HttpMethod.PUT, recipePutPaths).authenticated();
        authorize.requestMatchers(HttpMethod.DELETE, recipeDeletePaths).authenticated();
    }

    private void configureReviewAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] reviewGetPaths = {
                "/api/v1/recipes/*/reviews", "/api/v1/recipes/*/reviews/*/replies"
        };
        String[] reviewPostPaths = {"/api/v1/recipes/*/reviews"};
        String[] reviewPutPaths = {"/api/v1/recipes/*/reviews"};
        String[] reviewDeletePaths = {"/api/v1/recipes/*/reviews"};

        authorize.requestMatchers(HttpMethod.GET, reviewGetPaths).permitAll();
        authorize.requestMatchers(HttpMethod.POST, reviewPostPaths).authenticated();
        authorize.requestMatchers(HttpMethod.PUT, reviewPutPaths).authenticated();
        authorize.requestMatchers(HttpMethod.DELETE, reviewDeletePaths).authenticated();
    }

    private void configureOrderAccess(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry authorize) {
        String[] orderGetPaths = {"/api/v1/orders/*", "/api/v1/orders"};
        String[] orderPostPaths = {
                "/api/v1/orders", "/api/v1/orders/cart", "/api/v1/orders/rollback",
                "/api/v1/orders/preparation", "/api/v1/orders/verification"
        };
        String[] orderPutPaths = {"/api/v1/orders/*"};
        String[] orderDeletePaths = {"/api/v1/orders/*"};

        authorize.requestMatchers(HttpMethod.GET, orderGetPaths).authenticated();
        authorize.requestMatchers(HttpMethod.POST, orderPostPaths).authenticated();
        authorize.requestMatchers(HttpMethod.PUT, orderPutPaths).authenticated();
        authorize.requestMatchers(HttpMethod.DELETE, orderDeletePaths).authenticated();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOriginPatterns(List.of("*"));

        // 허용할 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        // 허용할 헤더
        configuration.setAllowedHeaders(List.of("*"));

        // 자격 증명 허용 (JWT 토큰 등)
        configuration.setAllowCredentials(true);

        // Preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}