package store.myproject.onlineshop.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import store.myproject.onlineshop.domain.ErrorResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.exception.ErrorCode;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException {
        ErrorCode errorCode = (ErrorCode) request.getAttribute("errorCode");

        if (errorCode == null) {
            errorCode = ErrorCode.ACCESS_TOKEN_NOT_FOUND;
        }

        ObjectMapper objectMapper = new ObjectMapper();

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        Response errorResponse = Response.error(new ErrorResponse(errorCode.toString(), errorCode.getMessage()));

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
