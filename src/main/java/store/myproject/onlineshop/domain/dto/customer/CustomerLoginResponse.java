package store.myproject.onlineshop.domain.dto.customer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerLoginResponse {

    private String accessToken;
    private String refreshToken;

}
