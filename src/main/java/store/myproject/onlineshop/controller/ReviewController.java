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

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class ReviewController {

    private final RecipeService recipeService;

    @Operation(summary = "댓글 조회")
    @GetMapping("/{recipeUuid}/reviews")
    public Response<Page<ReviewResponse>> getReview(@PathVariable UUID recipeUuid, Pageable pageable) {
        return Response.success(recipeService.getRecipeReviews(recipeUuid, pageable));
    }

    @Operation(summary = "대댓글 더보기")
    @GetMapping("/{recipeUuid}/reviews/{reviewUuid}/replies")
    public Response<Page<ChildReviewResponse>> getChildReviews(
            @PathVariable UUID recipeUuid,
            @PathVariable UUID reviewUuid,
            Pageable pageable
    ) {
        return Response.success(recipeService.getChildReviews(recipeUuid, reviewUuid, pageable));
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/{recipeUuid}/reviews")
    public Response<MessageResponse> writeReview(@PathVariable UUID recipeUuid, @Valid @RequestBody ReviewWriteRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.createReview(email, recipeUuid, request));
    }

    @Operation(summary = "댓글 수정")
    @PutMapping("/{recipeUuid}/reviews/{reviewUuid}")
    public Response<MessageResponse> modifyReview(@PathVariable UUID recipeUuid, @PathVariable UUID reviewUuid, @Valid @RequestBody ReviewUpdateRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.updateReview(email, recipeUuid, reviewUuid, request));
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/{recipeUuid}/reviews/{reviewUuid}")
    public Response<MessageResponse> deleteReview(@PathVariable UUID recipeUuid, @PathVariable UUID reviewUuid, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.deleteReview(email, recipeUuid, reviewUuid));
    }
}
