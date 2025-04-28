package store.myproject.onlineshop.domain.recipeitem;

import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeItemDto {
    private Long itemId;

    private String itemName;

    private BigDecimal price;

    private Long stock;

    private String itemImage;

    private String brandName;
}
