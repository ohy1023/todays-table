package store.myproject.onlineshop.domain.delivery.dto;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfoRequest {
    private String recipientName;

    private String recipientTel;

    private String recipientCity;

    private String recipientStreet;

    private String recipientDetail;

    private String recipientZipcode;
}
