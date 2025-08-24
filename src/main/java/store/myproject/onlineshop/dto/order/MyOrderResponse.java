package store.myproject.onlineshop.dto.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MyOrderResponse {

    private UUID merchantUid;
    private LocalDateTime createdDate;
    private String orderStatus;
    private BigDecimal totalPrice;
    private String deliveryStatus;
    private List<OrderItemResponse> orderItems;

}
