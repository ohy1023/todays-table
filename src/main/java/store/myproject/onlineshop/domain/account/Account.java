package store.myproject.onlineshop.domain.account;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.*;
import store.myproject.onlineshop.domain.account.dto.AccountDto;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;

import java.math.BigDecimal;

import static store.myproject.onlineshop.domain.customer.CustomerRole.ROLE_CUSTOMER;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@Embeddable
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Account{

    private String bankName;

    private String accountNumber;

    private String depositor;

    private BigDecimal myAssets;

    public void plusMyAssets(BigDecimal money) {
        this.myAssets = this.myAssets.add(money);
    }

    public void minusMyAssets(BigDecimal money) {
        if (myAssets.compareTo(money) < 0) {
            throw new AppException(NOT_ENOUGH_MONEY, NOT_ENOUGH_MONEY.getMessage());
        }
        this.myAssets =this.myAssets.subtract(money);
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