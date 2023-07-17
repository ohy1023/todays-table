package store.myproject.onlineshop.domain.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeliveryUpdateRequest {

    private String recipientName;

    private String recipientTel;

    private String city;

    private String street;

    private String detail;

    private String zipcode;
}
