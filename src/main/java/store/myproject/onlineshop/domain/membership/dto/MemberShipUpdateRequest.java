package store.myproject.onlineshop.domain.membership.dto;

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
    private Level level;

    @NotNull
    private BigDecimal baseline;

    @NotNull
    private Float discountRate;

    public MemberShip toEntity() {
        return MemberShip.builder()
                .level(this.level)
                .baseline(this.baseline)
                .discountRate(this.discountRate)
                .build();
    }

}
