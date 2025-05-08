package store.myproject.onlineshop.domain.order.dto;

import lombok.*;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo {

    private UUID merchantUid;

    private String brandName;

    private String itemName;

    private String orderDate;

    private String orderStatus;

    private String orderCustomerName;

    private String orderCustomerTel;

    private String recipientName;

    private String recipientTel;

    private String recipientAddress;

    private String zipcode;

    private String deliveryStatus;

    private BigDecimal totalPrice;

}
