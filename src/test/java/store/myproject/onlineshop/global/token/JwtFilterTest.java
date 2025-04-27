package store.myproject.onlineshop.global.token;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.service.CustomerService;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
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

    @ParameterizedTest
    @ValueSource(strings = {"/", "/api/v1/customers/login", "/api/v1/customers/join", "/swagger-ui/index.html", "/api-docs/v1"})
    @DisplayName("특정 url 인증 없이 통과")
    void excluded_url_pass_success(String uri) throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        jwtFilter.doFilterInternal(request, response, chain);

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }


    @Test
    @DisplayName("AccessToken 없는 경우")
    void no_access_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(ACCESS_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("AccessToken 유효하지 않은 경우")
    void invalid_access_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(ACCESS_TOKEN_HEADER, "invalidAccessToken"),
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
    @DisplayName("AccessToken 만료된 경우")
    void expired_access_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(ACCESS_TOKEN_HEADER, "expiredAccessToken"),
                new Cookie(REFRESH_TOKEN_HEADER, "validRefreshToken")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        given(jwtUtils.isExpired("expiredAccessToken")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(EXPIRED_ACCESS_TOKEN.getMessage());
    }

    @Test
    @DisplayName("RefreshToken 없는 경우")
    void no_refresh_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(ACCESS_TOKEN_HEADER, "validAccessToken")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("RefreshToken 만료된 경우")
    void expired_refresh_token() {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(ACCESS_TOKEN_HEADER, "validAccessToken"),
                new Cookie(REFRESH_TOKEN_HEADER, "expiredRefreshToken")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        given(jwtUtils.isInvalid("validAccessToken")).willReturn(false);
        given(jwtUtils.isExpired("validAccessToken")).willReturn(false);
        given(jwtUtils.isExpired("expiredRefreshToken")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> jwtFilter.doFilterInternal(request, response, chain))
                .isInstanceOf(AppException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("AccessToken, RefreshToken 재발급 성공")
    void valid_tokens_success() throws ServletException, IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setCookies(
                new Cookie(ACCESS_TOKEN_HEADER, "validAccessToken"),
                new Cookie(REFRESH_TOKEN_HEADER, "validRefreshToken")
        );
        MockHttpServletResponse response = new MockHttpServletResponse();
        MockFilterChain chain = new MockFilterChain();

        Customer customer = CustomerFixture.createCustomer();
        given(jwtUtils.isInvalid("validAccessToken")).willReturn(false);
        given(jwtUtils.isExpired("validAccessToken")).willReturn(false);
        given(jwtUtils.isExpired("validRefreshToken")).willReturn(false);
        given(jwtUtils.getEmail("validAccessToken")).willReturn(customer.getEmail());

        given(customerService.findCustomerByEmail(customer.getEmail())).willReturn(customer);

        given(jwtUtils.createAccessToken(customer.getEmail())).willReturn("newAccessToken");
        given(jwtUtils.createRefreshToken(customer.getEmail())).willReturn("newRefreshToken");

        // when
        jwtFilter.doFilterInternal(request, response, chain);

        // then
        then(customerService).should().findCustomerByEmail(customer.getEmail());
        assertThat(response.getCookies()).extracting(Cookie::getName)
                .contains(ACCESS_TOKEN_HEADER, REFRESH_TOKEN_HEADER);
    }
}
