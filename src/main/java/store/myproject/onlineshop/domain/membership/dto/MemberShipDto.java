package store.myproject.onlineshop.domain.membership.dto;

import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShipDto {
    private UUID uuid;

    private Level level;

    private BigDecimal baseline;

    private BigDecimal discountRate;

}
