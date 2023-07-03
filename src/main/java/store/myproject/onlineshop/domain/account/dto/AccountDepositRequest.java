package store.myproject.onlineshop.domain.account.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDepositRequest {

    private Long depositPrice;
}
