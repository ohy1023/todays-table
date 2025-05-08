package store.myproject.onlineshop.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "응답 결과")
public class Response<T> {
    @Schema(description = "결과 코드", example = "200")
    private String resultCode;

    @Schema(description = "응답 데이터")
    private T result;

    public static <T> Response<T> error(T result) {
        return new Response<>("ERROR", result);
    }

    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }
}