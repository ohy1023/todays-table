package store.myproject.onlineshop.domain.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.order.dto.MyOrderResponse;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.dto.OrderItemResponse;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static store.myproject.onlineshop.domain.order.OrderStatus.*;

@Entity
@Table(
        name = "Orders"
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
    @Column(name = "imp_uid", unique = true)
    private String impUid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "order_status")
    private OrderStatus orderStatus;

    @Setter
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery; //배송정보

    @Builder.Default
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> orderItemList = new ArrayList<>();

    public void addOrderItem(OrderItem orderItem) {
        orderItemList.add(orderItem);
        orderItem.addOrder(this);
    }

    public void completePayment(String impUid) {
        setImpUid(impUid);
        this.orderStatus = ORDER;
    }

    public void cancelPayment() {
        this.orderStatus = CANCEL;
    }

    public void rollbackPayment() {
        this.orderStatus = ROLLBACK;
    }

    public static Order createOrder(UUID merchantUid, Customer customer, Delivery delivery, OrderItem orderItem) {

        Order order = Order.builder()
                .merchantUid(merchantUid)
                .customer(customer)
                .delivery(delivery)
                .totalPrice(orderItem.getTotalPrice())
                .orderStatus(READY)
                .build();

        order.addOrderItem(orderItem);

        return order;
    }

    public static Order createOrders(UUID merchantUid, Customer customer, Delivery delivery, List<OrderItem> orderItems) {

        BigDecimal totalPrice = BigDecimal.ZERO;

        for (OrderItem orderItem : orderItems) {
            totalPrice = totalPrice.add(orderItem.getTotalPrice());
        }

        Order order = Order.builder()
                .customer(customer)
                .delivery(delivery)
                .totalPrice(totalPrice)
                .orderStatus(READY)
                .merchantUid(merchantUid)
                .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }

        return order;
    }
}
