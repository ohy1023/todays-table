package store.myproject.onlineshop.global.utils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import java.util.Optional;

@Slf4j
public class CookieUtils {

    private final static int ACCESS_TOKEN_MAX_AGE = 60 * 60 * 3; // (seconds) -> 3시간
    private final static int REFRESH_TOKEN_MAX_AGE = 60 * 60 * 24 * 14; // (seconds) -> 14일
    private final static String ACCESS_TOKEN_HEADER = "Authorization";
    private final static String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    public static Optional<Cookie> getCookie(HttpServletRequest request, String key) {

        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(key)) {
                    return Optional.of(cookie);
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<String> extractAccessToken(HttpServletRequest request) {

        return getCookie(request, ACCESS_TOKEN_HEADER).map(Cookie::getValue);
    }

    public static Optional<String> extractRefreshToken(HttpServletRequest request) {

        return getCookie(request, REFRESH_TOKEN_HEADER).map(Cookie::getValue);
    }


    public static void addAccessTokenAtCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie =
                ResponseCookie.from(ACCESS_TOKEN_HEADER, value)
                        .httpOnly(false)
                        .secure(false)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(ACCESS_TOKEN_MAX_AGE)
                        .build();

        // 헤더에 Set-Cookie 를 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }

    public static void addRefreshTokenAtCookie(HttpServletResponse response, String value) {
        ResponseCookie cookie =
                ResponseCookie.from(REFRESH_TOKEN_HEADER, value)
                        .httpOnly(false)
                        .secure(false)
                        .sameSite("Strict")
                        .path("/")
                        .maxAge(REFRESH_TOKEN_MAX_AGE)
                        .build();

        // 헤더에 Set-Cookie 를 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }

}
