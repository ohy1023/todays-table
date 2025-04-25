package store.myproject.onlineshop.domain.recipe.dto;

import lombok.*;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeDto {

    private String recipeTitle;
    private String recipeDescription;
    private String recipeCookingTime;
    private String recipeServings;
    private String recipeWriter;
    private String thumbnailUrl;
    private int recipeView;
    private Long reviewCnt; // 댓글 수
    private Long likeCnt; // 좋아요  수
    private List<Long> itemIdList;
    private List<RecipeStepDto> steps;
}
