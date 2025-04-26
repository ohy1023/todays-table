package store.myproject.onlineshop.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.myproject.onlineshop.domain.order.OrderStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCond {

    private String itemName;

    private String brandName;

    private OrderStatus orderStatus;
}
