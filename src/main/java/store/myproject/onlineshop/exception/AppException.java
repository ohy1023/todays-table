package store.myproject.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AppException extends RuntimeException{

    private ErrorCode errorCode;
    private String message;

    @Override
    public String toString() {
        if (message == null) return errorCode.getMessage();
        return message;
    }
}

