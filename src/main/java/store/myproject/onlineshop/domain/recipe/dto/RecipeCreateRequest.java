package store.myproject.onlineshop.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeCreateRequest {


    @NotBlank
    private String recipeTitle;

    @NotBlank
    private String recipeDescription;

    @NotBlank
    private String recipeCookingTime;

    @NotBlank
    private String recipeServings;

    @NotEmpty
    private List<Long> itemIdList;

    @NotEmpty
    private List<RecipeStepRequest> steps;

    private String thumbnailUrl;

    public Recipe toEntity(Customer customer) {
        return Recipe.builder()
                .uuid(UUIDGenerator.generateUUIDv7())
                .recipeTitle(recipeTitle)
                .recipeDescription(recipeDescription)
                .customer(customer)
                .recipeCookingTime(recipeCookingTime)
                .recipeServings(recipeServings)
                .recipeMeta(RecipeMeta.builder()
                        .reviewCnt(0L)
                        .likeCnt(0L)
                        .viewCnt(0L)
                        .build())
                .build();
    }

}
