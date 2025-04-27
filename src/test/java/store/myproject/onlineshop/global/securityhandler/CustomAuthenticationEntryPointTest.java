package store.myproject.onlineshop.global.securityhandler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.AuthenticationException;
import store.myproject.onlineshop.domain.ErrorResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.exception.ErrorCode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import store.myproject.onlineshop.fixture.ResultCode;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationEntryPointTest {

    @InjectMocks
    private CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Test
    @DisplayName("요청에 errorCode가 없는 경우 - 기본 ACCESS_TOKEN_NOT_FOUND 반환")
    void commence_default_error_code() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = mock(AuthenticationException.class);

        // when
        customAuthenticationEntryPoint.commence(request, response, authException);

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        Response<ErrorResponse> result = objectMapper.readValue(
                response.getContentAsString(),
                new TypeReference<Response<ErrorResponse>>() {}
        );

        assertThat(response.getStatus()).isEqualTo(ErrorCode.ACCESS_TOKEN_NOT_FOUND.getHttpStatus().value());
        assertThat(response.getContentType()).isEqualTo("application/json;charset=utf-8");
        assertThat(result.getResultCode()).isEqualTo(ResultCode.ERROR);
        assertThat(result.getResult().getErrorCode()).isEqualTo(ErrorCode.ACCESS_TOKEN_NOT_FOUND.name());
        assertThat(result.getResult().getMessage()).isEqualTo(ErrorCode.ACCESS_TOKEN_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("요청에 errorCode가 설정된 경우 - 해당 errorCode로 반환")
    void commence_custom_error_code() throws IOException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setAttribute("errorCode", ErrorCode.INVALID_ACCESS_TOKEN);  // 커스텀 에러코드 주입
        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthenticationException authException = mock(AuthenticationException.class);

        // when
        customAuthenticationEntryPoint.commence(request, response, authException);

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        Response<ErrorResponse> result = objectMapper.readValue(
                response.getContentAsString(),
                new TypeReference<Response<ErrorResponse>>() {}
        );

        assertThat(response.getStatus()).isEqualTo(ErrorCode.INVALID_ACCESS_TOKEN.getHttpStatus().value());
        assertThat(response.getContentType()).isEqualTo("application/json;charset=utf-8");
        assertThat(result.getResultCode()).isEqualTo(ResultCode.ERROR);
        assertThat(result.getResult().getErrorCode()).isEqualTo(ErrorCode.INVALID_ACCESS_TOKEN.name());
        assertThat(result.getResult().getMessage()).isEqualTo(ErrorCode.INVALID_ACCESS_TOKEN.getMessage());
    }
}
