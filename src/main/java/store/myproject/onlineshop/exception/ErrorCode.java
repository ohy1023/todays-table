package store.myproject.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    DUPLICATED_EMAIL(HttpStatus.CONFLICT, "사용자의 이메일이 중복됩니다");


    private HttpStatus httpStatus;
    private String message;
}
