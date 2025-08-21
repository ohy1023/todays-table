package store.myproject.onlineshop.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Schema(description = "오류 응답")
public class ErrorResponse {

    @Schema(description = "에러 코드", example = "ITEM_NOT_FOUND")
    private String errorCode;

    @Schema(description = "에러 메시지", example = "해당 품목을 찾을 수 없습니다.")
    private String message;

    public static ErrorResponse of(String errorCode, String message) {
        return new ErrorResponse(errorCode, message);
    }
}