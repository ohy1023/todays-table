package store.myproject.onlineshop.domain.item.dto;

import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCreateRequest {

    private String itemName;

    private BigDecimal price;

    private Long stock;

    private String brandName;


    public Item toEntity(Brand brand) {
        return Item.builder()
                .itemName(this.itemName)
                .price(this.price)
                .stock(this.stock)
                .brand(brand)
                .build();
    }
}
