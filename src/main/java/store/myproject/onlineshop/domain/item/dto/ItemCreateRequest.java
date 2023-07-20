package store.myproject.onlineshop.domain.item.dto;

import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.stock.Stock;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCreateRequest {

    private String itemName;

    private Long price;

    private Long stock;

    private String itemPhotoUrl;

    private String brandName;


    public Item toEntity(Long cnt) {
        Item item = Item.builder()
                .itemName(this.itemName)
                .price(this.price)
                .itemPhotoUrl(this.itemPhotoUrl)
                .build();

        Stock newStock = Stock.builder()
                .quantity(cnt)
                .build();

        item.setStock(newStock);

        return item;
    }
}
