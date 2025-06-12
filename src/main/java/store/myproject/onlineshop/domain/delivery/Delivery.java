package store.myproject.onlineshop.domain.delivery;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @Column(name = "recipient_name")
    private String recipientName;

    @Column(name = "recipient_tel")
    private String recipientTel;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_status")
    private DeliveryStatus status; // 배송 상태 (READY, SHIPPING, DELIVERING, COMP, CANCEL)

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