package store.myproject.onlineshop.domain.order;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.item.Item;

import java.math.BigDecimal;

import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor
@SQLDelete(sql = "UPDATE order_item SET deleted_date = CURRENT_TIMESTAMP WHERE order_item_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(
        name = "order_item",
        indexes = {
                @Index(name = "idx_order_item_order_item", columnList = "orders_id, item_id")
        }
)
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item; //주문 상품

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "orders_id")
    private Order order; //주문

    @Column(name = "order_price")
    private BigDecimal orderPrice; //주문 가격

    @Column(name = "order_count")
    private Long count; //주문 수량

    public static OrderItem createOrderItem(Item item, BigDecimal orderPrice, Long count) {

        OrderItem orderItem = OrderItem.builder()
                .item(item)
                .orderPrice(orderPrice)
                .count(count)
                .build();

        item.decrease(count);

        return orderItem;

    }

    public void addOrder(Order order) {
        this.order = order;
    }

    public BigDecimal getTotalPrice() {
        return getOrderPrice().multiply(new BigDecimal(getCount()));
    }


}
