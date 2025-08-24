package store.myproject.onlineshop.dto.recipe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeItemDto {
    private UUID itemUuid;

    private String itemName;

    private BigDecimal itemPrice;

    private String brandName;

    private String thumbnail;
}
