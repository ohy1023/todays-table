package store.myproject.onlineshop.global.utils;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class CookieUtilsTest {

    @Test
    @DisplayName("AccessToken 쿠키를 추출할 수 있다")
    void extract_access_token_cookie() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("Authorization", "access-token-value"));

        // when
        Optional<String> result = CookieUtils.extractAccessToken(request);

        // then
        assertThat(result).isPresent().contains("access-token-value");
    }

    @Test
    @DisplayName("RefreshToken 쿠키를 추출할 수 있다")
    void extract_refresh_token_cookie() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(new Cookie("Authorization-refresh", "refresh-token-value"));

        // when
        Optional<String> result = CookieUtils.extractRefreshToken(request);

        // then
        assertThat(result).isPresent().contains("refresh-token-value");
    }

    @Test
    @DisplayName("AccessToken을 쿠키에 저장할 수 있다")
    void add_access_token_cookie() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        CookieUtils.addAccessTokenAtCookie(response, "access-token-value");

        // then
        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("Authorization=access-token-value");
        assertThat(setCookie).contains("Max-Age=10800"); // 3시간
    }

    @Test
    @DisplayName("RefreshToken을 쿠키에 저장할 수 있다")
    void add_refresh_token_cookie() {
        // given
        MockHttpServletResponse response = new MockHttpServletResponse();

        // when
        CookieUtils.addRefreshTokenAtCookie(response, "refresh-token-value");

        // then
        String setCookie = response.getHeader("Set-Cookie");
        assertThat(setCookie).contains("Authorization-refresh=refresh-token-value");
        assertThat(setCookie).contains("Max-Age=1209600"); // 14일
    }

    @Test
    @DisplayName("해당 키의 쿠키가 없으면 Optional.empty를 반환한다")
    void return_empty_when_cookie_not_found() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(); // 아무 쿠키도 없음

        // when
        Optional<Cookie> result = CookieUtils.getCookie(request, "unknown");

        // then
        assertThat(result).isEmpty();
    }
}
