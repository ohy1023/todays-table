package store.myproject.onlineshop.domain.dto.account;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.entity.Account;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountUpdateRequest {
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
                .build();
    }
}
