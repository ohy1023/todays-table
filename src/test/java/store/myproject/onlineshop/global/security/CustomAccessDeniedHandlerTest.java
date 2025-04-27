package store.myproject.onlineshop.global.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import store.myproject.onlineshop.domain.ErrorResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.ResultCode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(MockitoExtension.class)
class CustomAccessDeniedHandlerTest {

    @InjectMocks
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Test
    @DisplayName("AccessDenied 발생 시 FORBIDDEN_ACCESS 반환")
    void handle_access_denied() throws IOException, ServletException {
        // given
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AccessDeniedException accessDeniedException = mock(AccessDeniedException.class);

        // when
        customAccessDeniedHandler.handle(request, response, accessDeniedException);

        // then
        ObjectMapper objectMapper = new ObjectMapper();
        Response<ErrorResponse> result = objectMapper.readValue(
                response.getContentAsString(),
                new TypeReference<Response<ErrorResponse>>() {}
        );

        assertThat(response.getStatus()).isEqualTo(ErrorCode.FORBIDDEN_ACCESS.getHttpStatus().value());
        assertThat(response.getContentType()).isEqualTo("application/json;charset=utf-8");
        assertThat(result.getResultCode()).isEqualTo(ResultCode.ERROR);
        assertThat(result.getResult().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ACCESS.name());
        assertThat(result.getResult().getMessage()).isEqualTo(ErrorCode.FORBIDDEN_ACCESS.getMessage());
    }
}
