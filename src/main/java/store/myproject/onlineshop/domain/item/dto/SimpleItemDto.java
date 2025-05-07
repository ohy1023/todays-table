package store.myproject.onlineshop.domain.item.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
public class SimpleItemDto {
    private UUID uuid;
    private String itemName;
    private BigDecimal price;
    private String thumbnail;
    private String brandName;

    @QueryProjection
    public SimpleItemDto(UUID uuid, String itemName, BigDecimal price, String thumbnail, String brandName) {
        this.uuid = uuid;
        this.itemName = itemName;
        this.price = price;
        this.thumbnail = thumbnail;
        this.brandName = brandName;
    }

}
