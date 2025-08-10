package store.myproject.onlineshop.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 정보 DTO")
public class OrderInfo {

    @Schema(description = "주문 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID merchantUid;

    private List<OrderItemResponse> orderItemList;

    @Schema(description = "주문 일자", example = "2025-05-08")
    private String orderDate;

    @Schema(description = "주문 상태", example = "배송 중")
    private String orderStatus;

    @Schema(description = "주문자 이름", example = "홍길동")
    private String orderCustomerName;

    @Schema(description = "주문자 연락처", example = "010-1234-5678")
    private String orderCustomerTel;

    @Schema(description = "수취인 이름", example = "김철수")
    private String recipientName;

    @Schema(description = "수취인 연락처", example = "010-9876-5432")
    private String recipientTel;

    @Schema(description = "수취인 주소", example = "서울특별시 강남구 테헤란로 123")
    private String recipientAddress;

    @Schema(description = "우편번호", example = "12345")
    private String zipcode;

    @Schema(description = "배송 상태", example = "배송 완료")
    private String deliveryStatus;

    @Schema(description = "총 가격", example = "50000")
    private BigDecimal totalPrice;

    public static OrderInfo from(Order order) {

        Delivery delivery = order.getDelivery();

        String address = delivery.getAddress().getCity() + " " + delivery.getAddress().getStreet() + " " + delivery.getAddress().getDetail();

        List<OrderItem> orderItemList = order.getOrderItemList();

        List<OrderItemResponse> orderItemResponses = orderItemList.stream().map(
                        orderItem -> OrderItemResponse.builder()
                                .itemUuid(orderItem.getItem().getUuid())
                                .orderPrice(orderItem.getOrderPrice())
                                .itemName(orderItem.getItem().getItemName())
                                .thumbnail(orderItem.getItem().getThumbnail())
                                .count(orderItem.getCount())
                                .brandUuid(orderItem.getItem().getBrand().getUuid())
                                .brandName(orderItem.getItem().getBrand().getBrandName())
                                .build()
                )
                .toList();

        Customer customer = order.getCustomer();

        return OrderInfo.builder()
                .merchantUid(order.getMerchantUid())
                .orderItemList(orderItemResponses)
                .totalPrice(order.getTotalPrice())
                .orderDate(
                        Optional.ofNullable(order.getCreatedDate())
                                .map(d -> d.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")))
                                .orElse("날짜 없음")
                )
                .orderCustomerName(customer.getUserName())
                .orderCustomerTel(customer.getTel())
                .orderStatus(order.getOrderStatus().name())
                .deliveryStatus(delivery.getStatus().name())
                .recipientName(delivery.getRecipientName())
                .recipientTel(delivery.getRecipientTel())
                .recipientAddress(address)
                .zipcode(delivery.getAddress().getZipcode())
                .build();
    }

}
