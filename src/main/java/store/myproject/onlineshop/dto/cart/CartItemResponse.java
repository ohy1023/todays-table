package store.myproject.onlineshop.dto.cart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private UUID itemUuid;

    private String itemName;

    private String thumbnail;

    private BigDecimal itemPrice;

    private Long itemCnt;
}
