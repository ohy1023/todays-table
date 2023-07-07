package store.myproject.onlineshop.domain.account;

import jakarta.persistence.Embeddable;
import jakarta.persistence.PrePersist;
import lombok.*;
import store.myproject.onlineshop.domain.account.dto.AccountDto;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;

import static store.myproject.onlineshop.domain.customer.CustomerRole.ROLE_CUSTOMER;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@Embeddable
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Account {

    private String bankName;

    private String accountNumber;

    private String depositor;

    private Long myAssets;

    public void plusMyAssets(Long money) {
        this.myAssets += money;
    }

    public void minusMyAssets(Long money) {
        if (myAssets < money) {
            throw new AppException(NOT_ENOUGH_MONEY, NOT_ENOUGH_MONEY.getMessage());
        }
        this.myAssets -= money;
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