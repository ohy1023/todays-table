package store.myproject.onlineshop.domain.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.account.Account;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountCreateRequest {

    @NotNull
    private String bankName;

    @NotNull
    private String accountNumber;

    @NotNull
    private String depositor;

    public Account toEntity() {
        return Account.builder()
                .bankName(this.bankName)
                .accountNumber(this.accountNumber)
                .depositor(this.depositor)
                .myAssets(new BigDecimal(0))
                .build();
    }

}
