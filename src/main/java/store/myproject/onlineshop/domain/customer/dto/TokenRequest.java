package store.myproject.onlineshop.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "토큰 요청 DTO")
public class TokenRequest {

    @NotBlank
    @Schema(description = "Access Token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessToken;

    @NotBlank
    @Schema(description = "Refresh Token", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
