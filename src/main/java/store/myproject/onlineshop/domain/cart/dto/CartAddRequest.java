package store.myproject.onlineshop.domain.cart.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartAddRequest {

    @NotNull
    private Long itemId;

    @NotNull
    private Long itemCnt;
}
