package store.myproject.onlineshop.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderFlatDto {
    private UUID merchantUid;
    private LocalDateTime createdDate;
    private String orderStatus;
    private BigDecimal totalPrice;
    private String deliveryStatus;

    private Long count;
    private BigDecimal orderPrice;
    private UUID itemUuid;
    private String itemName;
    private String thumbnail;
    private UUID brandUuid;
    private String brandName;
}
