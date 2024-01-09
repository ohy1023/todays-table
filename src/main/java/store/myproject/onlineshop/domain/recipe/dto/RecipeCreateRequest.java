package store.myproject.onlineshop.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeCreateRequest {


    @NotBlank
    private String recipeTitle;

    @NotBlank
    private String recipeContent;

    @NotBlank
    private String recipeCookingTime;

    @NotBlank
    private int recipeServings;

    private List<Long> itemIdList;


    public Recipe toEntity(Customer customer) {
        return Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeContent(recipeContent)
                .customer(customer)
                .recipeCookingTime(recipeCookingTime)
                .servings(recipeServings)
                .build();
    }

}
