package store.myproject.onlineshop.domain.recipe.dto;

import lombok.*;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.recipeitem.RecipeItemDto;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeDto {
    private Long recipeId;
    private String recipeTitle;
    private String recipeDescription;
    private String recipeCookingTime;
    private String recipeServings;
    private String recipeWriter;
    private String thumbnailUrl;
    private Long recipeView;
    private Long reviewCnt; // 댓글 수
    private Long likeCnt; // 좋아요  수
    private List<RecipeItemDto> items;
    private List<RecipeStepDto> steps;
}
