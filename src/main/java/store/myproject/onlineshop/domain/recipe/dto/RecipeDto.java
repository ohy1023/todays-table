package store.myproject.onlineshop.domain.recipe.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeDto {

    private String recipeTitle;
    private String recipeContent;
    private String recipeCookingTime;
    private String recipeServings;
    private String recipeWriter;
    private int recipeView;
    private Long reviewCnt; // 댓글 수
    private Long likeCnt; // 좋아요  수
    private List<String> itemNameList;
    private List<String> recipeImageList;

}
