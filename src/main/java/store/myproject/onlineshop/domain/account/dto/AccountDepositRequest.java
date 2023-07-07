package store.myproject.onlineshop.domain.account.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountDepositRequest {

    @NotNull
    private Long depositPrice;
}
