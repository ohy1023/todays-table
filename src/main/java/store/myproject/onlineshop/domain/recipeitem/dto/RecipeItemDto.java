package store.myproject.onlineshop.domain.recipeitem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeItemDto {
    private Long itemId;

    private String itemName;

    private BigDecimal price;

    private String itemImage;

    private String brandName;
}
