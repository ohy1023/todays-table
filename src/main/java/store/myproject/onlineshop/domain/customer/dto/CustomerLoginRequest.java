package store.myproject.onlineshop.domain.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginRequest {

    @Email
    private String email;

    @NotBlank
    private String password;
}
