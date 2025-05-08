package store.myproject.onlineshop.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

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
    private List<UUID> itemUuidList;

    @NotEmpty
    private List<RecipeStepRequest> steps;

}
