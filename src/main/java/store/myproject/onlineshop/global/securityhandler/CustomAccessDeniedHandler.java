package store.myproject.onlineshop.global.securityhandler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import store.myproject.onlineshop.domain.ErrorResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.exception.ErrorCode;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorCode errorCode = ErrorCode.FORBIDDEN_ACCESS;
        ErrorResponse errorResponse = new ErrorResponse(errorCode.name(), errorCode.getMessage());
        Response<ErrorResponse> body = Response.error(errorResponse);

        response.setStatus(errorCode.getHttpStatus().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("utf-8");

        objectMapper.writeValue(response.getWriter(), body);
    }
}