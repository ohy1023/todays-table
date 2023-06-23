package store.myproject.onlineshop.domain.dto.customer;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTokenRequest {
    private String accessToken;
    private String refreshToken;
}
