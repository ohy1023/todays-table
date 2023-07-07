package store.myproject.onlineshop.domain.account.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountWithdrawRequest {

    private BigDecimal withdrawPrice;
}
