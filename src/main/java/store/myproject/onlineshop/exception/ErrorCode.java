package store.myproject.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common (Authentication)
    DATABASE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Database Error"),
    INVALID_PERMISSION(HttpStatus.UNAUTHORIZED, "Unauthorized access"),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "invalid password"),
    INVALID_REQUEST(HttpStatus.UNAUTHORIZED, "invalid quest"),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "Token invalid"),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "Token expired"),
    TOKEN_NOT_FOUND(HttpStatus.UNAUTHORIZED, "Token not found"),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "Access forbidden"),
    CONFLICT(HttpStatus.CONFLICT, "Request is Conflict"),

    // User
    EMAIL_NOT_FOUND(HttpStatus.NOT_FOUND, "email not found"),
    DUPLICATE_NICKNAME(HttpStatus.CONFLICT, "Nick name conflict"),

    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "email conflict"),
    USERNAME_NOT_FOUND(HttpStatus.NOT_FOUND, "Username Not Found"),
    DUPLICATED_ENROLL(HttpStatus.CONFLICT, "User is Duplicate"),
    INVALID_MAIL(HttpStatus.UNAUTHORIZED, "invalid email"),
    NOT_FOUND(HttpStatus.NOT_FOUND, "Role Not Found"),


    ;
    private HttpStatus httpStatus;
    private String message;
}
