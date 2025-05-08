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
    @Schema(description = "Access Token", example = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Inp2eWcxMDIzQG5hdmVyLmNvbSIsImlhdCI6MTc0NjY5MDA5MywiZXhwIjoxNzQ2NzAwODkzfQ.iZlPQwUQZrS_9xIzCnuz3XdlgJ5sTdARGLNDiJm-1mY", requiredMode = Schema.RequiredMode.REQUIRED)
    private String accessToken;

    @NotBlank
    @Schema(description = "Refresh Token",example = "eyJhbGciOiJIUzI1NiJ9.eyJlbWFpbCI6Inp2eWcxMDIzQG5hdmVyLmNvbSIsImlhdCI6MTc0NjY5MDA5MywiZXhwIjoxNzQ3ODk5NjkzfQ.h575wijXHBzcq9KslmKtxHr0yaYC346bYKd-jONLETc", requiredMode = Schema.RequiredMode.REQUIRED)
    private String refreshToken;
}
