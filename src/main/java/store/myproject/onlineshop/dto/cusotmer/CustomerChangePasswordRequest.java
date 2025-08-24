package store.myproject.onlineshop.dto.cusotmer;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "비밀번호 변경 DTO")
public class CustomerChangePasswordRequest {

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "현재 비밀번호", example = "curPassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String currentPassword;

    @NotBlank
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "비밀번호는 최소 8자 이상, 대소문자, 숫자, 특수문자를 포함해야 합니다.")
    @Schema(description = "새 비밀번호", example = "newPassword", requiredMode = Schema.RequiredMode.REQUIRED)
    private String newPassword;
}
