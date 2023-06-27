package store.myproject.onlineshop.domain.membership.dto;

import lombok.*;
import store.myproject.onlineshop.domain.customer.Level;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberShipDto {

    private Level level;

    private Long baseline;

    private Float discountRate;

}
