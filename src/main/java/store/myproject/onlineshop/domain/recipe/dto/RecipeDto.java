package store.myproject.onlineshop.domain.recipe.dto;

import lombok.*;
import store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeDto {
    private UUID recipeUuid;
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

    public RecipeDto(UUID recipeUuid, String recipeTitle, String recipeDescription, String recipeCookingTime, String recipeServings, String recipeWriter, String thumbnailUrl, Long recipeView, Long reviewCnt, Long likeCnt) {
        this.recipeUuid = recipeUuid;
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeServings = recipeServings;
        this.recipeWriter = recipeWriter;
        this.thumbnailUrl = thumbnailUrl;
        this.recipeView = recipeView;
        this.reviewCnt = reviewCnt;
        this.likeCnt = likeCnt;
    }
}
