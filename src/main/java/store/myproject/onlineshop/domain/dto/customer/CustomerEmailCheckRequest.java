package store.myproject.onlineshop.domain.dto.customer;


import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEmailCheckRequest {

    @Email
    private String email;
}
