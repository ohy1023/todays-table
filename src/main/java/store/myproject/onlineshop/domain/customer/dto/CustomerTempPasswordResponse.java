package store.myproject.onlineshop.domain.customer.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTempPasswordResponse {

    private String email;

    private String tempPassword;

}