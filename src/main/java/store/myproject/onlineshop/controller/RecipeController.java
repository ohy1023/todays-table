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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipemeta.dto.RecipeMetaDto;
import store.myproject.onlineshop.service.RecipeService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class RecipeController {

    private final RecipeService recipeService;

    @Operation(summary = "레시피 단건 조회", description = "특정 레시피의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 조회 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음")
    })
    @GetMapping("/{recipeUuid}")
    public Response<RecipeDto> viewOneRecipe(
            @Parameter(description = "조회할 레시피 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid) {
        RecipeDto recipeDetail = recipeService.getRecipeDetail(recipeUuid);
        recipeService.increaseRecipeViewCount(recipeUuid);

        return Response.success(recipeDetail);
    }

    @Operation(summary = "레시피 단건 통계 정보 조회", description = "특정 레시피의 통계 정보를 조회합니다.")
    @GetMapping("/meta/{recipeUuid}")
    public Response<RecipeMetaDto> viewOneRecipeMeta(
            @Parameter(description = "조회할 레시피 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid) {
        return Response.success(recipeService.getRecipeMeta(recipeUuid));
    }

    @Operation(summary = "레시피 페이징 조회", description = "레시피 목록을 페이징하여 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 목록 조회 성공")
    })
    @GetMapping
    public Response<Slice<SimpleRecipeDto>> viewAllRecipes(RecipeListCond cond, @ParameterObject Pageable pageable) {
        return Response.success(recipeService.getRecipes(cond, pageable));
    }

    @Operation(summary = "레시피 작성", description = "새로운 레시피를 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping
    public Response<MessageResponse> writeRecipe(@Valid @RequestBody RecipeCreateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.createRecipe(request, email));
    }

    @Operation(summary = "레시피 수정", description = "기존 레시피를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 수정 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PutMapping(value = "/{recipeUuid}")
    public Response<MessageResponse> writeRecipe(
            @Parameter(description = "수정할 레시피 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid,
            @Valid @RequestBody RecipeUpdateRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return Response.success(recipeService.updateRecipe(recipeUuid, request, email));
    }

    @Operation(summary = "레시피 삭제", description = "특정 레시피를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "레시피 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음")
    })
    @DeleteMapping("/{recipeUuid}")
    public Response<MessageResponse> deleteRecipe(
            @Parameter(description = "삭제할 레시피 UUID", example = "13dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
            @PathVariable UUID recipeUuid,
            Authentication authentication
    ) {
        String email = authentication.getName();
        return Response.success(recipeService.deleteRecipe(recipeUuid, email));
    }

    @Operation(summary = "이미지 업로드 -> s3 저장 -> URL 반환", description = "레시피 단계 이미지를 S3에 저장하고 URL을 반환합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 파일 형식")
    })
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<MessageResponse> uploadImage(@RequestPart("recipeStepImage") MultipartFile file) {
        return Response.success(recipeService.uploadImage(file));
    }

    @GetMapping("/test/one")
    public Response<Page<SimpleRecipeDto>> testPage(Pageable pageable) {
        return Response.success(recipeService.testPage(pageable));
    }

    @GetMapping("/test/two")
    public Response<Slice<SimpleRecipeDto>> testSlice(Pageable pageable) {
        return Response.success(recipeService.testSlice(pageable));
    }


    @GetMapping("/test/three")
    public Response<RecipeCursorResponse> testCursor(@ModelAttribute RecipeCond cond) {
        return Response.success(recipeService.testCursor(cond));
    }

    @GetMapping("/test/four")
    public Response<Page<SimpleRecipeDto>> testCountPer(Pageable pageable) {
        return Response.success(recipeService.testCountPer(pageable));
    }

    @GetMapping("/test/five")
    public Response<Page<SimpleRecipeDto>> testCount(Pageable pageable) {
        return Response.success(recipeService.testCount(pageable));
    }
}
