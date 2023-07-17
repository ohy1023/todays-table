package store.myproject.onlineshop.domain.order.dto;

import lombok.Data;
import store.myproject.onlineshop.domain.order.OrderStatus;

@Data
public class OrderSearchCond {

    private String itemName;

    private String brandName;

    private OrderStatus orderStatus;
}
