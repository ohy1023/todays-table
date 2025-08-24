package store.myproject.onlineshop.dto.membership;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShipUpdateRequest {


    @NotNull
    @Schema(description = "멤버십 레벨 (BRONZE, SLIVER, GOLD, DIAMOND)", example = "GOLD", requiredMode = Schema.RequiredMode.REQUIRED)
    private Level level;

    @NotNull
    @Schema(description = "기준 금액", example = "100000", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal baseline;

    @NotNull
    @Schema(description = "할인율", example = "0.1", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal discountRate;

    public MemberShip toEntity() {
        return MemberShip.builder()
                .level(this.level)
                .baseline(this.baseline)
                .discountRate(this.discountRate)
                .build();
    }

}
