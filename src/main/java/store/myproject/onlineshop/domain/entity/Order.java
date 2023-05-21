package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.enums.DeliveryStatus;
import store.myproject.onlineshop.domain.enums.OrderStatus;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE order SET deleted_date = CURRENT_TIMESTAMP WHERE order_id = ?")
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Embedded
    private Address receiverAddress;

    private String receiverName;

    private String receiverTel;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus deliveryStatus;

    @OneToMany(mappedBy = "order")
    private List<OrderItem> orderItemList = new ArrayList<>();
}
