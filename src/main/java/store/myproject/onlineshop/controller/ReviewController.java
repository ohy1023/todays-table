package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.review.dto.*;
import store.myproject.onlineshop.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class ReviewController {

    private final RecipeService recipeService;

    @Operation(summary = "댓글 조회")
    @GetMapping("/{recipeId}/reviews")
    public Response<Page<ReviewResponse>> getReview(@PathVariable Long recipeId, Pageable pageable) {
        return Response.success(recipeService.getRecipeReviews(recipeId, pageable));
    }

    @Operation(summary = "대댓글 더보기")
    @GetMapping("/{recipeId}/reviews/{parentReviewId}/replies")
    public Response<Page<ChildReviewResponse>> getChildReviews(
            @PathVariable Long recipeId,
            @PathVariable Long parentReviewId,
            Pageable pageable
    ) {
        return Response.success(recipeService.getChildReviews(recipeId, parentReviewId, pageable));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/{id}/reviews")
    public Response<MessageResponse> writeReview(@PathVariable Long id, @Valid @RequestBody ReviewWriteRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.createReview(email, id, request));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{id}/reviews/{reviewId}")
    public Response<MessageResponse> modifyReview(@PathVariable Long id, @PathVariable Long reviewId, @Valid @RequestBody ReviewUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.updateReview(email, id, reviewId, request));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{id}/reviews/{reviewId}")
    public Response<MessageResponse> deleteReview(@PathVariable Long id, @PathVariable Long reviewId, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.deleteReview(email, id, reviewId));
    }
}
