package store.myproject.onlineshop.domain.order.dto;

import lombok.*;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfo {

    private String orderDate;

    private String orderStatus;

    private String orderCustomerName;

    private String orderCustomerTel;

    private String recipientName;

    private String recipientTel;

    private String recipientAddress;

    private String zipcode;

    private String deliveryStatus;

    private List<OrderItem> purchasedItem;

    private Long totalPrice;



}
