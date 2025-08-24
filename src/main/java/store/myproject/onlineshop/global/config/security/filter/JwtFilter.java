package store.myproject.onlineshop.global.config.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.CookieUtils;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.service.CustomerService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static store.myproject.onlineshop.exception.ErrorCode.*;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final CustomerService customerService;

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        Optional<String> accessTokenAtCookie = CookieUtils.extractAccessToken(request);
        Optional<String> refreshTokenAtCookie = CookieUtils.extractRefreshToken(request);

        if (accessTokenAtCookie.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = accessTokenAtCookie.get();

        if (jwtUtils.isInvalid(accessToken)) {
            throw new AppException(INVALID_ACCESS_TOKEN);
        }

        if (jwtUtils.isExpired(accessToken)) {
            throw new AppException(EXPIRED_ACCESS_TOKEN);
        }

        String info = jwtUtils.getEmail(accessToken);

        // refresh Token 존재 여부 확인
        if (refreshTokenAtCookie.isEmpty()) {
            throw new AppException(REFRESH_TOKEN_NOT_FOUND);
        }

        String refreshToken = refreshTokenAtCookie.get();

        // access Token 만료된 경우 -> refresh Token 검증
        if (jwtUtils.isExpired(refreshToken)) {
            // refresh Token 만료된 경우
            throw new AppException(EXPIRED_REFRESH_TOKEN);
        }

        // refresh Token 유효한 경우 -> access Token / refresh Token 재발급
        String newAccessToken = jwtUtils.createAccessToken(info);
        String newRefreshToken = jwtUtils.createRefreshToken(info);


        // 발급된 accessToken을 response cookie 에 저장
        CookieUtils.addAccessTokenAtCookie(response, newAccessToken);
        // 발급된 refreshToken을 response cookie 에 저장
        CookieUtils.addRefreshTokenAtCookie(response, newRefreshToken);

        Customer customer = customerService.findCustomerByEmail(info);

        // 유효성 검증 통과한 경우
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(customer.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(customer.getCustomerRole().name())));

        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));


        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

}
