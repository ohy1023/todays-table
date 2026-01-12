package store.myproject.onlineshop.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "결제 준비 요청 DTO")
public class PreparationRequest {

    @NotNull(message = "totalPrice는 필수입니다.")
    @Schema(description = "결제 금액", example = "32000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal totalPrice;
}
