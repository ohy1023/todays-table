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
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;

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
            "/api/v1/memberships",
            "/api/v1/recipes",
            "/api/v1/items", // 품목 생성
            "/api/v1/{id}/likes", // 좋아요 누르기
    };

    private static final String[] PUT_AUTH_ADMIN = {
            "/api/v1/brands/{id}",
            "/api/v1/memberships/{id}",
            "/api/v1/recipes/{id}/reviews/{recipeId}",
            "/api/v1/items/{itemId}", // 품목 수정
    };

    private static final String[] DELETE_AUTH_ADMIN = {
            "/api/v1/brands/{id}",
            "/api/v1/memberships/{id}",
            "/api/v1/recipes/{id}/reviews/{recipeId}",
            "/api/v1/items/{itemId}", // 품목 삭제
    };

    private static final String[] GET_AUTH_USER = {
            "/api/v1/accounts",
            "/api/v1/memberships",
            "/api/v1/memberships/{id}",
            "/api/v1/orders/{orderId}",
            "/api/v1/orders/search",
            "/api/v1/carts", // 장바구니 품목 조회
    };

    private static final String[] POST_AUTH_USER = {
            "/api/v1/customers/reissue",
            "/api/v1/customers/logout",
            "/api/v1/accounts",
            "/api/v1/recipes",
            "/api/v1/orders",
            "/api/v1/orders/cart",
            "/api/v1/carts", // 장바구니에 해당 품목 넣기
            "/api/v1/carts/{cartItemId}", // 장바구니에 구매할 품목 체크
            "/api/v1/{id}/likes", // 좋아요 누르기

    };
    private static final String[] PUT_AUTH_USER = {
            "/api/v1/customers",
            "/api/v1/accounts",
            "/api/v1/recipes/{id}/reviews/{recipeId}",
            "/api/v1/deliveries/{orderId}", // 배송지 변경

    };
    private static final String[] DELETE_AUTH_USER = {
            "/api/v1/customers", // 회원 탈퇴
            "/api/v1/accounts", // 계좌 삭제
            "/api/v1/recipes/{id}/reviews/{recipeId}", // 레시피 삭제
            "/api/v1/orders/{orderId}", // 주문 취소
            "/api/v1/carts", // 장바구니에 모든 품목 삭제
            "/api/v1/carts/{itemId}", // 장바구니에 해당 품목 삭제

    };


    private static final String[] PERMIT_ALL = {
            "/api/v1/customers/join",
            "/api/v1/customers/login",
            "/api/v1/corporations/join",
            "/api/v1/corporations/login",
            "/api/v1/customers/email",
            "/api/v1/customers/nickName",
            "/api/v1/brands/search",
            "/api/v1/memberships", // 멤버쉽 전체 조회
            "/api/v1/memberships/{id}", // 멤버쉽 단건 조회
            "/api/v1/items/{itemId}", // 품목 단건 조회
            "/api/v1/{id}/likes", // 좋아요 수 조회

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
                        .requestMatchers(HttpMethod.PUT, PUT_AUTH_ADMIN).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, DELETE_AUTH_ADMIN).hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, GET_AUTH_USER).authenticated()
                        .requestMatchers(HttpMethod.POST, POST_AUTH_USER).authenticated()
                        .requestMatchers(HttpMethod.PUT, PUT_AUTH_USER).authenticated()
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
