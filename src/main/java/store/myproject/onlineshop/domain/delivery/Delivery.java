package store.myproject.onlineshop.domain.delivery;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;

import static jakarta.persistence.FetchType.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE Delivery SET deleted_date = CURRENT_TIMESTAMP WHERE id = ?")
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_tel")
    private String recipientTel;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //ENUM [CANCEL(취소), READY(준비), COMP(배송)]

    public void setInfo(DeliveryUpdateRequest request) {
        this.recipientName = request.getRecipientName();
        this.recipientTel = request.getRecipientTel();
        this.address = Address.builder()
                .city(request.getCity())
                .street(request.getStreet())
                .detail(request.getDetail())
                .zipcode(request.getZipcode())
                .build();
    }

    public void createDeliveryStatus(DeliveryStatus status) {
        this.status = status;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public void cancel() {
        this.status = DeliveryStatus.CANCEL;
    }

    public static Delivery createWithInfo(DeliveryInfoRequest request) {
        return Delivery.builder()
                .recipientName(request.getRecipientName())
                .recipientTel(request.getRecipientTel())
                .address(Address.builder()
                        .city(request.getRecipientCity())
                        .zipcode(request.getRecipientZipcode())
                        .detail(request.getRecipientDetail())
                        .street(request.getRecipientStreet())
                        .build())
                .build();

    }

}