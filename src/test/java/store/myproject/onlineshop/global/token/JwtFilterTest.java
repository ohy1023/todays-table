package store.myproject.onlineshop.global.token;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.service.CustomerService;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;
    @Mock
    private CustomerService customerService;
    @Mock
    private JwtUtils jwtUtils;

    private final String ACCESS_TOKEN_HEADER = "Authorization";
    private final String REFRESH_TOKEN_HEADER = "Authorization-refresh";

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("AccessToken 없는 경우")
    void no_access_token() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("AccessToken 유효하지 않은 경우")
    void invalid_access_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer invalidAccessToken");
        request.setCookies(
                new Cookie(REFRESH_TOKEN_HEADER, "validRefreshToken")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        given(jwtUtils.isInvalid("invalidAccessToken")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_ACCESS_TOKEN.getMessage());
    }

    @Test
    @DisplayName("RefreshToken 없는 경우")
    void no_refresh_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer validAccessToken");
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("AccessToken 만료 + RefreshToken 만료된 경우 예외 발생")
    void expired_access_and_refresh_token() throws Exception {
        // given
        String expiredAccessToken = "expiredAccessToken";
        String expiredRefreshToken = "expiredRefreshToken";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + expiredAccessToken);
        request.setCookies(new Cookie(REFRESH_TOKEN_HEADER, expiredRefreshToken));
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // accessToken은 유효하지만 만료
        given(jwtUtils.isInvalid(expiredAccessToken)).willReturn(false);
        given(jwtUtils.isExpired(expiredAccessToken)).willReturn(true);
        given(jwtUtils.getEmail(expiredAccessToken)).willReturn("test@example.com");

        // refreshToken도 만료됨
        given(jwtUtils.isExpired(expiredRefreshToken)).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getMessage());
    }


    @Test
    @DisplayName("AccessToken 만료 + RefreshToken 유효 → accessToken 재발급 및 응답 반환")
    void expired_access_token_but_valid_refresh_token() throws Exception {
        // given
        String expiredAccessToken = "expiredAccessToken";
        String validRefreshToken = "validRefreshToken";
        String email = "user@example.com";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + expiredAccessToken);
        request.setCookies(new Cookie(REFRESH_TOKEN_HEADER, validRefreshToken));

        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        given(jwtUtils.isInvalid(expiredAccessToken)).willReturn(false);
        given(jwtUtils.isExpired(expiredAccessToken)).willReturn(true);
        given(jwtUtils.getEmail(expiredAccessToken)).willReturn(email);
        given(jwtUtils.isExpired(validRefreshToken)).willReturn(false);
        given(jwtUtils.createAccessToken(email)).willReturn(newAccessToken);
        given(jwtUtils.createRefreshToken(email)).willReturn(newRefreshToken);

        // when
        jwtFilter.doFilterInternal(request, response, chain);

        // then
        String body = response.getContentAsString();
        assertThat(body).contains("\"accessToken\":\"" + newAccessToken + "\"");
        assertThat(body).doesNotContain("refreshToken");

        // refreshToken은 쿠키로 내려왔는지 확인
        Cookie[] cookies = response.getCookies();
        Optional<Cookie> refreshTokenCookieOpt = Arrays.stream(cookies)
                .filter(cookie -> cookie.getName().equals(REFRESH_TOKEN_HEADER))
                .findFirst();


        assertThat(response.getContentType()).isEqualTo("application/json;charset=UTF-8");
        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(refreshTokenCookieOpt).isPresent();
        Cookie refreshTokenCookie = refreshTokenCookieOpt.get();
        assertThat(refreshTokenCookie.getValue()).isEqualTo(newRefreshToken);
        assertThat(refreshTokenCookie.isHttpOnly()).isTrue();
        assertThat(refreshTokenCookie.getSecure()).isTrue();

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

}
