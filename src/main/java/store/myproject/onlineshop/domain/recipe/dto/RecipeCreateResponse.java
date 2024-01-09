package store.myproject.onlineshop.domain.recipe.dto;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeCreateResponse {

    private String recipeTitle;

    private String recipeContent;

    private String recipeWriter;

    private String recipeCookingTime;

    private String recipeServings;

    private int recipeView;

    private List<String> itemNameList;
}
