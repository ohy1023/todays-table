package store.myproject.onlineshop.domain.item.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
public class SimpleItemDto {
    private Long itemId;
    private String itemName;
    private BigDecimal price;
    private String thumbnail;
    private String brandName;

    @QueryProjection
    public SimpleItemDto(Long itemId, String itemName, BigDecimal price, String thumbnail, String brandName) {
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.thumbnail = thumbnail;
        this.brandName = brandName;
    }

}
