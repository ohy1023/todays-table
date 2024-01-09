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

    private int recipeServings;

    private List<String> itemNameList;
}
