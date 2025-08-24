package store.myproject.onlineshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {
    private Long count;
    private BigDecimal orderPrice;
    private String itemName;
    private UUID itemUuid;
    private String thumbnail;
    private UUID brandUuid;
    private String brandName;

}