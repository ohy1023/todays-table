package store.myproject.onlineshop.domain.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.context.ApplicationEventPublisher;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.alert.AlertType;
import store.myproject.onlineshop.domain.alert.dto.AlertRequestDto;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.DeliveryStatus;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static store.myproject.onlineshop.domain.order.OrderStatus.*;
import static store.myproject.onlineshop.exception.ErrorCode.ALREADY_ARRIVED;

@Entity
@Table(
        name = "Orders",
        indexes = {
                @Index(name = "idx_order_uuid", columnList = "order_uuid"),
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @Column(name = "order_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "order_date")
    private LocalDateTime orderDate; //주문시간

    @Column(name = "merchant_uid")
    private String merchantUid;

    @Column(name = "imp_uid")
    private String impUid;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; //배송정보

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList = new ArrayList<>();

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        orderItem.addOrder(this);
    }

    public void setImpUid(String impUid) {
        this.impUid = impUid;
    }


    public void cancel() {
        if (delivery.getStatus().equals(DeliveryStatus.COMP)) {
            throw new AppException(ALREADY_ARRIVED, ALREADY_ARRIVED.getMessage());
        }
        this.delivery.cancel();
        this.orderStatus = CANCEL;
        for (OrderItem orderItem : orderItemList) {
            orderItem.cancel();
        }
    }

    public static Order createOrder(String merchantUid, Customer customer, Delivery delivery, OrderItem orderItem) {

        Order order = Order.builder()
                .customer(customer)
                .delivery(delivery)
                .orderDate(LocalDateTime.now())
                .orderStatus(ORDER)
                .merchantUid(merchantUid)
                .build();

        order.setDelivery(delivery);

        order.addOrderItem(orderItem);

        return order;
    }

    public static Order createOrders(String merchantUid, Customer customer, Delivery delivery, List<OrderItem> orderItems) {

        Order order = Order.builder()
                .customer(customer)
                .delivery(delivery)
                .orderDate(LocalDateTime.now())
                .orderStatus(ORDER)
                .merchantUid(merchantUid)
                .build();

        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    public OrderInfo toOrderInfo() {

        BigDecimal totalPrice = new BigDecimal(0);

        for (OrderItem orderItem : orderItemList) {
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        String address = this.delivery.getAddress().getCity() + " " + this.delivery.getAddress().getStreet() + " " + this.delivery.getAddress().getDetail();

        return OrderInfo.builder()
                .uuid(UUIDGenerator.generateUUIDv7())
                .brandName(this.orderItemList.get(0).getItem().getBrand().getName())
                .itemName(this.orderItemList.get(0).getItem().getItemName())
                .orderDate(this.orderDate.format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")))
                .orderCustomerName(this.customer.getUserName())
                .orderCustomerTel(this.customer.getTel())
                .orderStatus(this.orderStatus.name())
                .deliveryStatus(this.delivery.getStatus().name())
                .recipientName(this.delivery.getRecipientName())
                .recipientTel(this.delivery.getRecipientTel())
                .recipientAddress(address)
                .zipcode(this.delivery.getAddress().getZipcode())
                .totalPrice(totalPrice)
                .build();
    }

    public void sendAlertOrderComplete(ApplicationEventPublisher eventPublisher, AlertType alertType) {
        AlertRequestDto alertRequestDto = AlertRequestDto.builder()
                .receiver(this.customer)
                .alertType(alertType)
                .content("주문이 완료되었습니다.")
                .url("/order/detail/" + this.id)
                .build();

        eventPublisher.publishEvent(alertRequestDto);
    }
}
