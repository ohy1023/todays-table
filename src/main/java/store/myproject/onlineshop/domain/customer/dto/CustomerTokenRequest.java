package store.myproject.onlineshop.domain.customer.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerTokenRequest {
    private String accessToken;
    private String refreshToken;
}
