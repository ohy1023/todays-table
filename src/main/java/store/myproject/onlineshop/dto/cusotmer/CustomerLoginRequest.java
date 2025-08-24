package store.myproject.onlineshop.dto.cusotmer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 요청 DTO")
public class CustomerLoginRequest {

    @Email
    @Schema(description = "사용자 이메일", example = "zvyg1023@naver.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "비밀번호", example = "1Q2w3e4r!!", requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}
