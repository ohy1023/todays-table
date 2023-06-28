package store.myproject.onlineshop.domain.item.dto;

import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemDto {

    private String itemName;

    private int price;

    private int stock;

    private String itemPhotoUrl;

    private Brand brand;

}
