package store.myproject.onlineshop.domain.order.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreparationRequest {

    private String merchantUid;

    private BigDecimal totalPrice;
}
