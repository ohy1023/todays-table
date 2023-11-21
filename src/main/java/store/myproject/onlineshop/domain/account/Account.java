package store.myproject.onlineshop.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;
import store.myproject.onlineshop.domain.account.dto.AccountDto;
import store.myproject.onlineshop.exception.AppException;

import java.math.BigDecimal;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account {
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "account_number")
    private String accountNumber;

    private String depositor;

    @Column(name = "my_assets")
    private BigDecimal myAssets;

    public void plusMyAssets(BigDecimal money) {
        this.myAssets = this.myAssets.add(money);
    }

    public void minusMyAssets(BigDecimal money) {
        if (myAssets.compareTo(money) < 0) {
            throw new AppException(NOT_ENOUGH_MONEY, NOT_ENOUGH_MONEY.getMessage());
        }
        this.myAssets = this.myAssets.subtract(money);
    }

    public AccountDto toAccountDto() {
        return AccountDto.builder()
                .bankName(this.bankName)
                .accountNumber(this.accountNumber)
                .depositor(this.depositor)
                .myAssets(this.myAssets)
                .build();
    }
}