package store.myproject.onlineshop.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import store.myproject.onlineshop.dto.common.Response;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLTransientConnectionException;

@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionManager {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<?> handleRuntimeException(RuntimeException e) {
        return buildErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException e) {
        return buildErrorResponse(e.getErrorCode().name(), e.getMessage(), e.getErrorCode().getHttpStatus());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidation(MethodArgumentNotValidException e) {
        String errorCode = e.getBindingResult().getAllErrors().get(0).getCode();
        String message = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return buildErrorResponse(errorCode, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<?> handleMethodNotSupported(HttpRequestMethodNotSupportedException e) {
        return buildErrorResponse("METHOD_NOT_ALLOWED", e.getMessage(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<?> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException e) {
        return buildErrorResponse("UNSUPPORTED_MEDIA_TYPE", e.getMessage(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<?> handleNotReadable(HttpMessageNotReadableException e) {
        return buildErrorResponse("BAD_REQUEST", "요청 형식이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<?> handleMissingParameter(MissingServletRequestParameterException e) {
        return buildErrorResponse("BAD_REQUEST", "필수 요청 파라미터가 누락되었습니다: " + e.getParameterName(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingPathVariableException.class)
    public ResponseEntity<?> handleMissingPathVariable(MissingPathVariableException e) {
        return buildErrorResponse("INTERNAL_SERVER_ERROR", "URL 경로 변수가 누락되었습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<?> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return buildErrorResponse("BAD_REQUEST", "요청 파라미터 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolation(ConstraintViolationException e) {
        return buildErrorResponse("BAD_REQUEST", "요청 값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e) {
        return buildErrorResponse("BAD_REQUEST", e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDenied(AccessDeniedException e) {
        return buildErrorResponse("FORBIDDEN", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthentication(AuthenticationException e) {
        return buildErrorResponse("UNAUTHORIZED", "인증에 실패했습니다.", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(SQLTransientConnectionException.class)
    public ResponseEntity<?> handleSqlTransientConnection(SQLTransientConnectionException e) {
        return buildErrorResponse("DB_CONNECTION_POOL_EXHAUSTED", "현재 데이터베이스 접속이 원활하지 않습니다. 잠시 후 다시 시도해 주세요.", HttpStatus.SERVICE_UNAVAILABLE);
    }

    // 공통 에러 응답 생성 메서드
    private ResponseEntity<?> buildErrorResponse(String code, String message, HttpStatus status) {
        ErrorResponse error = ErrorResponse.of(code, message);
        return ResponseEntity.status(status).body(Response.error(error));
    }
}
