package store.myproject.onlineshop.domain.customer.dto;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEmailCheckRequest {

    @Email
    private String email;
}
