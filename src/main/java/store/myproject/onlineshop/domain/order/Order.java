package store.myproject.onlineshop.domain.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import org.springframework.context.ApplicationEventPublisher;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.DeliveryStatus;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static store.myproject.onlineshop.domain.order.OrderStatus.*;
import static store.myproject.onlineshop.exception.ErrorCode.ALREADY_ARRIVED;

@Entity
@Table(
        name = "Orders",
        indexes = {
                @Index(name = "idx_merchant_uid", columnList = "merchant_uid"),
                @Index(name = "idx_deleted_date", columnList = "deleted_date")
        }
)
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE Orders SET deleted_date = CURRENT_TIMESTAMP WHERE orders_id = ?")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_id")
    private Long id;

    @Column(name = "merchant_uid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID merchantUid;

    @Setter
    @Column(name = "imp_uid", unique = true, nullable = false)
    private String impUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

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

    public void updateOrderStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
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

    public static Order createOrder(Customer customer, Delivery delivery, OrderItem orderItem) {

        Order order = Order.builder()
                .customer(customer)
                .delivery(delivery)
                .totalPrice(orderItem.getTotalPrice())
                .orderStatus(READY)
                .merchantUid(UUIDGenerator.generateUUIDv7())
                .build();

        order.setDelivery(delivery);

        order.addOrderItem(orderItem);

        return order;
    }

    public static Order createOrders(Customer customer, Delivery delivery, List<OrderItem> orderItems) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        Order order = Order.builder()
                .customer(customer)
                .delivery(delivery)
                .totalPrice(totalPrice)
                .orderStatus(READY)
                .merchantUid(UUIDGenerator.generateUUIDv7())
                .build();

        order.setDelivery(delivery);

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }

    public OrderInfo toOrderInfo() {

        String address = this.delivery.getAddress().getCity() + " " + this.delivery.getAddress().getStreet() + " " + this.delivery.getAddress().getDetail();

        return OrderInfo.builder()
                .merchantUid(this.merchantUid)
                .brandName(this.orderItemList.get(0).getItem().getBrand().getName())
                .itemName(this.orderItemList.get(0).getItem().getItemName())
                .totalPrice(this.totalPrice)
                .orderDate(this.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")))
                .orderCustomerName(this.customer.getUserName())
                .orderCustomerTel(this.customer.getTel())
                .orderStatus(this.orderStatus.name())
                .deliveryStatus(this.delivery.getStatus().name())
                .recipientName(this.delivery.getRecipientName())
                .recipientTel(this.delivery.getRecipientTel())
                .recipientAddress(address)
                .zipcode(this.delivery.getAddress().getZipcode())
                .build();
    }

}
