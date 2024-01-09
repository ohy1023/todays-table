package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateRequest;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateResponse;
import store.myproject.onlineshop.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 작성")
    @PostMapping
    public Response<RecipeCreateResponse> writeReview(@Valid RecipeCreateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.writeRecipe(email, request));
    }

}
