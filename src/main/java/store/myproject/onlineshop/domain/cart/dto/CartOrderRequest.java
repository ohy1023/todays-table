package store.myproject.onlineshop.domain.cart.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartOrderRequest {
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
                .recipientStreet(this.getRecipientStreet())
                .recipientDetail(this.getRecipientDetail())
                .build();
    }

}
