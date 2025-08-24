package store.myproject.onlineshop.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "결제 검증 요청 DTO")
public class PostVerificationRequest {

    @NotNull(message = "merchantUid는 필수입니다.")
    @Schema(description = "주문 고유 식별자", example = "13dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
    private UUID merchantUid;

    @NotNull(message = "impUid는 필수입니다.")
    @Schema(description = "포트원에서 제공한 결제 고유 번호", example = "imp_123456789012", required = true)
    private String impUid;
}
