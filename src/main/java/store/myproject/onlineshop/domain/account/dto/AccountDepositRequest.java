package store.myproject.onlineshop.domain.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDepositRequest {

    @NotNull
    private BigDecimal depositPrice;
}
