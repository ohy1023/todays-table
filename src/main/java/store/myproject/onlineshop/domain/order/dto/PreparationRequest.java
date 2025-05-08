package store.myproject.onlineshop.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "결제 준비 요청 DTO")
public class PreparationRequest {

    @NotBlank(message = "merchantUid는 필수입니다.")
    @Schema(description = "결제 고유 주문 번호 (주문 번호)", example = "13dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
    private String merchantUid;

    @NotNull(message = "totalPrice는 필수입니다.")
    @Schema(description = "결제 금액", example = "32000", required = true)
    private BigDecimal totalPrice;
}
