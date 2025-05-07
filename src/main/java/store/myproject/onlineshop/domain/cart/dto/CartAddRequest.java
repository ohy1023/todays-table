package store.myproject.onlineshop.domain.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequest {

    @NotNull
    private UUID itemUuid;

    @NotNull
    private Long itemCnt;
}
