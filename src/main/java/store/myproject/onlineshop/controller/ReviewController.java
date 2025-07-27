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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.review.dto.*;
import store.myproject.onlineshop.service.RecipeService;

import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class ReviewController {

    private final RecipeService recipeService;

    @Operation(
            summary = "댓글 조회",
            description = "특정 레시피에 대한 댓글 목록을 조회합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음")
    })
    @GetMapping("/{recipeUuid}/reviews")
    public Response<Page<ReviewResponse>> getReview(
            @Parameter(description = "조회할 레시피 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid,
            @ParameterObject @PageableDefault(sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {
        return Response.success(recipeService.getRecipeReviews(recipeUuid, pageable));
    }


    @Operation(summary = "대댓글 더보기", description = "특정 댓글에 대한 대댓글 목록을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "대댓글 조회 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @GetMapping("/{recipeUuid}/reviews/{reviewUuid}/replies")
    public Response<Page<ChildReviewResponse>> getChildReviews(
            @Parameter(description = "조회할 레시피 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid,
            @Parameter(description = "조회할 댓글 UUID", example = "88dd3e84-2b3a-11f0-9aef-59f7f88a8410", required = true)
            @PathVariable UUID reviewUuid,
            @ParameterObject @PageableDefault(sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return Response.success(recipeService.getChildReviews(recipeUuid, reviewUuid, pageable));
    }

    @Operation(summary = "댓글 작성", description = "특정 레시피에 대한 댓글을 작성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 작성 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/{recipeUuid}/reviews")
    public Response<MessageResponse> writeReview(
            @Parameter(description = "댓글을 작성할 레시피 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid,
            @Valid @RequestBody ReviewWriteRequest request,
            Authentication authentication
    ) {
        String email = authentication.getName();
        MessageResponse response = recipeService.createReview(email, recipeUuid, request);
        recipeService.increaseReviewCount(recipeUuid);
        return Response.success(response);
    }


    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PutMapping("/{recipeUuid}/reviews/{reviewUuid}")
    public Response<MessageResponse> modifyReview(
            @Parameter(description = "수정할 댓글 UUID", example = "88dd3e84-2b3a-11f0-9aef-59f7f88a8410", required = true)
            @PathVariable UUID recipeUuid, @PathVariable UUID reviewUuid, @Valid @RequestBody ReviewUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.updateReview(email, recipeUuid, reviewUuid, request));
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "댓글을 찾을 수 없음")
    })
    @DeleteMapping("/{recipeUuid}/reviews/{reviewUuid}")
    public Response<MessageResponse> deleteReview(
            @Parameter(description = "삭제할 댓글 있는 레시피 UUID", example = "13dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
            @PathVariable UUID recipeUuid,
            @Parameter(description = "삭제할 댓글 UUID", example = "88dd3e84-2b3a-11f0-9aef-59f7f88a8410", required = true)
            @PathVariable UUID reviewUuid, Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = recipeService.deleteReview(email, recipeUuid, reviewUuid);
        recipeService.decreaseReviewCount(recipeUuid);
        return Response.success(response);
    }
}
