package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateRequest;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.service.RecipeService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 단건 조회")
    @GetMapping("/{recipeId}")
    public Response<RecipeDto> viewOneRecipe(@PathVariable Long recipeId) {
        return Response.success(recipeService.getRecipe(recipeId));
    }

    @Operation(summary = "레시피 전체 조회")
    @GetMapping
    public Response<Page<SimpleRecipeDto>> viewAllRecipes(Pageable pageable) {
        return Response.success(recipeService.getAllRecipe(pageable));
    }

    @Operation(summary = "레시피 작성")
    @PostMapping
    public Response<MessageResponse> writeRecipe(@Valid @RequestBody RecipeCreateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.createRecipe(request, email));
    }

    @Operation(summary = "레시피 수정")
    @PutMapping(value = "/{recipeId}")
    public Response<MessageResponse> writeRecipe(@PathVariable Long recipeId, @Valid @RequestBody RecipeUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.updateRecipe(recipeId, request, email));
    }

    @Operation(summary = "레시피 삭제")
    @DeleteMapping("/{recipeId}")
    public Response<MessageResponse> deleteRecipe(@PathVariable Long recipeId, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.deleteRecipe(recipeId, email));
    }

    @PostMapping("/image")
    public Response<MessageResponse> uploadImage(@RequestPart("recipeStepImage") MultipartFile file) {
        return Response.success(recipeService.uploadImage(file));
    }
}
