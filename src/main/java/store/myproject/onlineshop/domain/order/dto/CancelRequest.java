package store.myproject.onlineshop.domain.order.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CancelRequest {

    private String impUid;

    private BigDecimal refundAmount;
}
