package store.myproject.onlineshop.domain.cartitem.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class CartItemResponse {

    private Long itemId;

    private String itemName;

    private String thumbnail;

    private BigDecimal price;

    private Long stock;

    private Long itemCnt;

    @QueryProjection
    public CartItemResponse(Long itemId, String itemName, String thumbnail, BigDecimal price, Long stock, Long itemCnt) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.thumbnail = thumbnail;
        this.price = price;
        this.stock = stock;
        this.itemCnt = itemCnt;
    }
}
