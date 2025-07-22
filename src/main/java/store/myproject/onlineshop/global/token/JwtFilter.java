package store.myproject.onlineshop.global.token;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.Map;
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

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = authHeader.substring(7);

        if (jwtUtils.isInvalid(accessToken)) {
            throw new AppException(INVALID_ACCESS_TOKEN);
        }

        boolean isAccessTokenExpired = jwtUtils.isExpired(accessToken);
        String email = jwtUtils.getEmail(accessToken);

        Optional<String> refreshTokenAtCookie = CookieUtils.extractRefreshToken(request);

        if (refreshTokenAtCookie.isEmpty()) {
            throw new AppException(REFRESH_TOKEN_NOT_FOUND);
        }

        String refreshToken = refreshTokenAtCookie.get();
        log.info("refreshToken : {}", refreshToken);

        if (isAccessTokenExpired) {
            if (jwtUtils.isExpired(refreshToken)) {
                throw new AppException(EXPIRED_REFRESH_TOKEN);
            }

            String newAccessToken = jwtUtils.createAccessToken(email);
            String newRefreshToken = jwtUtils.createRefreshToken(email);

            log.info("newAccessToken : {}", newAccessToken);
            log.info("newRefreshToken : {}", newRefreshToken);

            CookieUtils.addRefreshTokenAtCookie(response, newRefreshToken);

            Map<String, String> tokenMap = Map.of("accessToken", newAccessToken);

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.writeValue(response.getWriter(), tokenMap);
            return;
        }

        Customer customer = customerService.findCustomerByEmail(email);

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        customer.getEmail(),
                        null,
                        List.of(new SimpleGrantedAuthority(customer.getCustomerRole().name()))
                );
        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

        filterChain.doFilter(request, response);
    }

}
