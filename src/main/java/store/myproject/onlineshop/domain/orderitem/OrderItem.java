package store.myproject.onlineshop.domain.orderitem;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.order.Order;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //주문 상품

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Order order; //주문

    @Column(name = "order_price")
    private BigDecimal orderPrice; //주문 가격

    private Long count; //주문 수량

    public static OrderItem createOrderItem(Customer customer, Item item, BigDecimal orderPrice, Long count) {

        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .orderPrice(orderPrice)
                .count(count)
                .build();

        item.decrease(count);

        customer.purchase(orderItem.getTotalPrice());
        customer.addPurchaseAmount(orderItem.getTotalPrice());

        return orderItem;

    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        getItem().increase(this.count);
    }

    public BigDecimal getTotalPrice() {
        return getOrderPrice().multiply(new BigDecimal(getCount()));
    }


}
