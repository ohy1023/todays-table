package store.myproject.onlineshop.domain.customer.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountCreateResponse {

    private String bankName;

    private String accountNumber;

    private String depositor;
}
