package store.myproject.onlineshop.dto.membership;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShipCreateRequest {


    @NotNull
    private Level level;

    @NotNull
    private BigDecimal baseline;

    @NotNull
    private BigDecimal discountRate;

    public MemberShip toEntity() {
        return MemberShip.builder()
                .uuid(UUIDGenerator.generateUUIDv7())
                .level(this.level)
                .baseline(this.baseline)
                .discountRate(this.discountRate)
                .build();
    }

}
