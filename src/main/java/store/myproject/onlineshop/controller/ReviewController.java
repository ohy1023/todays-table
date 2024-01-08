package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateResponse;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteResponse;
import store.myproject.onlineshop.service.RecipeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class ReviewController {

    private final RecipeService recipeService;

    @Operation(summary = "댓글 작성")
    @PostMapping("/{id}/reviews")
    public Response<ReviewWriteResponse> writeReview(@PathVariable Long id, ReviewWriteRequest request, Authentication authentication) {
        String email = authentication.getName();
        return Response.success(recipeService.writeReview(email, id, request));
    }

    @Operation(summary = "댓글 수정")
    @PostMapping("/{id}/reviews/{reviewId}")
    public Response<ReviewUpdateResponse> writeReview(@PathVariable Long id, @PathVariable Long reviewId, ReviewUpdateRequest request, Authentication authentication) {
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
