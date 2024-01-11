package store.myproject.onlineshop.domain.item.dto;

import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.*;
import org.springframework.util.StringUtils;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;

import static store.myproject.onlineshop.domain.brand.QBrand.brand;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemUpdateRequest {
    private String itemName;

    private Long price;

    private Long stock;

    private String brandName;

}
