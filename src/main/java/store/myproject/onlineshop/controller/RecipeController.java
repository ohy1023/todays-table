package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipemeta.dto.RecipeMetaDto;
import store.myproject.onlineshop.service.RecipeService;

import java.net.URI;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 단건 조회", description = "특정 레시피의 상세 정보를 조회합니다.")
    @GetMapping("/{recipeUuid}")
    public ResponseEntity<Response<RecipeDto>> viewOneRecipe(
            @Parameter(description = "조회할 레시피 UUID", required = true)
            @PathVariable UUID recipeUuid) {
        RecipeDto recipeDetail = recipeService.getRecipeDetail(recipeUuid);
        recipeService.increaseRecipeViewCount(recipeUuid);
        return ResponseEntity.ok(Response.success(recipeDetail));
    }

    @Operation(summary = "레시피 통계 조회", description = "특정 레시피의 통계 정보를 조회합니다.")
    @GetMapping("/{recipeUuid}/meta")
    public ResponseEntity<Response<RecipeMetaDto>> viewOneRecipeMeta(
            @Parameter(description = "조회할 레시피 UUID", required = true)
            @PathVariable UUID recipeUuid) {
        return ResponseEntity.ok(Response.success(recipeService.getRecipeMeta(recipeUuid)));
    }

    @Operation(summary = "레시피 목록 조회", description = "커서 기반 페이징 방식으로 레시피 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<Response<RecipeCursorResponse>> viewAllRecipes(@ParameterObject RecipeListCond cond) {
        return ResponseEntity.ok(Response.success(recipeService.getRecipes(cond)));
    }

    @Operation(summary = "레시피 생성", description = "새로운 레시피를 작성합니다.")
    @PostMapping
    public ResponseEntity<Response<MessageResponse>> createRecipe(
            @Valid @RequestBody RecipeCreateRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = recipeService.createRecipe(request, email);
        String createdRecipeUuid = response.getUuid().toString();
        URI location = URI.create("/api/v1/recipes/" + createdRecipeUuid);
        return ResponseEntity.created(location).body(Response.success(response));
    }

    @Operation(summary = "레시피 수정", description = "기존 레시피를 수정합니다.")
    @PutMapping("/{recipeUuid}")
    public ResponseEntity<Response<MessageResponse>> updateRecipe(
            @PathVariable UUID recipeUuid,
            @Valid @RequestBody RecipeUpdateRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(Response.success(recipeService.updateRecipe(recipeUuid, request, email)));
    }

    @Operation(summary = "레시피 삭제", description = "특정 레시피를 삭제합니다.")
    @DeleteMapping("/{recipeUuid}")
    public ResponseEntity<Response<MessageResponse>> deleteRecipe(
            @PathVariable UUID recipeUuid,
            Authentication authentication) {
        String email = authentication.getName();
        recipeService.deleteRecipe(recipeUuid, email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "레시피 이미지 업로드", description = "이미지를 S3에 업로드하고 URL을 반환합니다.")
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Response<MessageResponse>> uploadImage(
            @RequestPart("recipeStepImage") MultipartFile file) {
        return ResponseEntity.ok(Response.success(recipeService.uploadImage(file)));
    }

    @GetMapping("/test/one")
    public Response<Page<SimpleRecipeDto>> testPage(Pageable pageable) {
        return Response.success(recipeService.testPage(pageable));
    }

    @GetMapping("/test/two")
    public Response<Slice<SimpleRecipeDto>> testSlice(Pageable pageable) {
        return Response.success(recipeService.testSlice(pageable));
    }


//    @GetMapping("/test/three")
//    public Response<RecipeCursorResponse> testCursor(@ModelAttribute RecipeCond cond) {
//        return Response.success(recipeService.testCursor(cond));
//    }

    @GetMapping("/test/four")
    public Response<Page<SimpleRecipeDto>> testCountPer(Pageable pageable) {
        return Response.success(recipeService.testCountPer(pageable));
    }

    @GetMapping("/test/five")
    public Response<Page<SimpleRecipeDto>> testCount(Pageable pageable) {
        return Response.success(recipeService.testCount(pageable));
    }
}
