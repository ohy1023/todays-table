package store.myproject.onlineshop.domain.orderitem;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.order.Order;

@Entity
@Getter
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE order_item SET deleted_date = CURRENT_TIMESTAMP WHERE order_item_id = ?")
public class OrderItem extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;
}
