package store.myproject.onlineshop.domain.customer.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTempPasswordResponse {

    private String email;
    private String tempPassword;

    public CustomerTempPasswordResponse(CustomerTempPasswordResponse customerTempPasswordResponse) {
        this.email = customerTempPasswordResponse.getEmail();
        this.tempPassword = customerTempPasswordResponse.getTempPassword();
    }
}