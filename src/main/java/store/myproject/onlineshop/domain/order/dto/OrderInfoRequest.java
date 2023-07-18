package store.myproject.onlineshop.domain.order.dto;

import lombok.*;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderInfoRequest {

    private Long itemId;

    private Long itemCnt;

    private String recipientName;

    private String recipientTel;

    private String recipientCity;

    private String recipientStreet;

    private String recipientDetail;

    private String recipientZipcode;

    public DeliveryInfoRequest toDeliveryInfoRequest() {
        return DeliveryInfoRequest
                .builder()
                .recipientName(this.getRecipientName())
                .recipientTel(this.getRecipientTel())
                .recipientCity(this.getRecipientCity())
                .recipientZipcode(this.getRecipientZipcode())
                .recipientDetail(this.getRecipientDetail())
                .recipientStreet(this.getRecipientStreet())
                .build();
    }
}
