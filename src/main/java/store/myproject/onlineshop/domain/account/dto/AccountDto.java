package store.myproject.onlineshop.domain.account.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDto {

    private String bankName;

    private String accountNumber;

    private String depositor;

    private BigDecimal myAssets;
}
