package store.myproject.onlineshop.domain.item.dto;

import lombok.*;
import store.myproject.onlineshop.domain.item.Item;

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


    public Item toEntity() {
        Item item = Item.builder()
                .itemName(this.itemName)
                .price(this.price)
                .stock(this.stock)
                .itemPhotoUrl(this.itemPhotoUrl)
                .build();

        return item;
    }
}
