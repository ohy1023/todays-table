package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateRequest;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.service.RecipeService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 단건 조회")
    @GetMapping("/{uuid}")
    public Response<RecipeDto> viewOneRecipe(@PathVariable UUID uuid) {
        return Response.success(recipeService.getRecipeDetail(uuid));
    }

    @Operation(summary = "레시피 페이징 조회")
    @GetMapping
    public Response<Slice<SimpleRecipeDto>> viewAllRecipes(Pageable pageable) {
        return Response.success(recipeService.getRecipes(pageable));
    }

    @Operation(summary = "레시피 작성")
    @PostMapping
    public Response<MessageResponse> writeRecipe(@Valid @RequestBody RecipeCreateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.createRecipe(request, email));
    }

    @Operation(summary = "레시피 수정")
    @PutMapping(value = "/{uuid}")
    public Response<MessageResponse> writeRecipe(@PathVariable UUID uuid, @Valid @RequestBody RecipeUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.updateRecipe(uuid, request, email));
    }

    @Operation(summary = "레시피 삭제")
    @DeleteMapping("/{uuid}")
    public Response<MessageResponse> deleteRecipe(@PathVariable UUID uuid, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.deleteRecipe(uuid, email));
    }

    @Operation(summary = "이미지 업로드 -> s3 저장 -> URL 반환")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<MessageResponse> uploadImage(@RequestPart("recipeStepImage") MultipartFile file) {
        return Response.success(recipeService.uploadImage(file));
    }
}
