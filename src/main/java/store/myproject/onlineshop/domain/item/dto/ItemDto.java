package store.myproject.onlineshop.domain.item.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDto {

    private UUID uuid;

    private String itemName;

    private BigDecimal price;

    private Long stock;

    private List<String> imageList;

    private String brandName;

    @QueryProjection
    public ItemDto(UUID uuid, String itemName, BigDecimal price, Long stock, String brandName) {
        this.uuid = uuid;
        this.itemName = itemName;
        this.price = price;
        this.stock = stock;
        this.brandName = brandName;
    }
}
