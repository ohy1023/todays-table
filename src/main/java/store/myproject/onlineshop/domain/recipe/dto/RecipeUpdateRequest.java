package store.myproject.onlineshop.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeUpdateRequest {


    @NotBlank
    private String recipeTitle;

    @NotBlank
    private String recipeDescription;

    @NotBlank
    private String recipeCookingTime;

    @NotBlank
    private String recipeServings;

    private String thumbnailUrl;

    @NotEmpty
    private List<Long> itemIdList;

    @NotEmpty
    private List<RecipeStepRequest> steps;

    public Recipe toEntity(Customer customer) {
        return Recipe.builder()
                .recipeTitle(recipeTitle)
                .recipeDescription(recipeDescription)
                .customer(customer)
                .recipeCookingTime(recipeCookingTime)
                .recipeServings(recipeServings)
                .build();
    }

}
