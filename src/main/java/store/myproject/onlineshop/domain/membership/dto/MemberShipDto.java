package store.myproject.onlineshop.domain.membership.dto;

import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShipDto {

    private Level level;

    private BigDecimal baseline;

    private BigDecimal discountRate;

}
