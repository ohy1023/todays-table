package store.myproject.onlineshop.domain.item.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDto {

    private String itemName;

    private Long price;

    private Long stock;

    private List<String> imageList;

    private String brandName;

    @QueryProjection
    public ItemDto(String itemName, Long price, Long stock, String brandName) {
        this.itemName = itemName;
        this.price = price;
        this.stock = stock;
        this.brandName = brandName;
    }
}
