package store.myproject.onlineshop.domain.alert.dto;

import lombok.*;
import store.myproject.onlineshop.domain.alert.AlertType;
import store.myproject.onlineshop.domain.customer.Customer;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AlertRequestDto {

    private Customer receiver;

    private AlertType alertType;

    private String content;

    private String url;
}
