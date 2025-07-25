package store.myproject.onlineshop.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeListCond {

    // 몇인분
    private Integer servings;

    // 조리 시간 범위
    private Integer cookingTimeFrom;
    private Integer cookingTimeTo;
}
