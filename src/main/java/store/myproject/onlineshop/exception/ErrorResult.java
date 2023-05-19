package store.myproject.onlineshop.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResult extends RuntimeException {

    private ErrorCode errorCode;
    private String message;

}
