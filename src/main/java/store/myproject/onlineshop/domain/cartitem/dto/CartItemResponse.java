package store.myproject.onlineshop.domain.cartitem.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class CartItemResponse {

    private UUID itemUuid;

    private String itemName;

    private String thumbnail;

    private BigDecimal price;

    private Long itemCnt;

    @QueryProjection
    public CartItemResponse(UUID itemUuid, String itemName, String thumbnail, BigDecimal price, Long itemCnt) {
        this.itemUuid = itemUuid;
        this.itemName = itemName;
        this.thumbnail = thumbnail;
        this.price = price;
        this.itemCnt = itemCnt;
    }
}
