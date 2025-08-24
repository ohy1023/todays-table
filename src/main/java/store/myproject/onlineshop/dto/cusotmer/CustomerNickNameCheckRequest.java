package store.myproject.onlineshop.dto.cusotmer;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "닉네임 중복 검사 요청 DTO")
public class CustomerNickNameCheckRequest {

    @Schema(description = "닉네임", example = "콩거", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "닉네임이 Null 또는 공백일 수 없습니다.")
    private String nickName;
}
