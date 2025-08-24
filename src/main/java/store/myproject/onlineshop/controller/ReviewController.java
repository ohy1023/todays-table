package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.dto.common.Response;
import store.myproject.onlineshop.dto.review.ChildReviewResponse;
import store.myproject.onlineshop.dto.review.ReviewResponse;
import store.myproject.onlineshop.dto.review.ReviewUpdateRequest;
import store.myproject.onlineshop.dto.review.ReviewWriteRequest;
import store.myproject.onlineshop.service.RecipeService;

import java.net.URI;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class ReviewController {

    private final RecipeService recipeService;

    @Operation(summary = "댓글 조회", description = "특정 레시피에 대한 댓글 목록을 조회합니다.")
    @GetMapping("/{recipeUuid}/reviews")
    public ResponseEntity<Response<Page<ReviewResponse>>> getReview(
            @Parameter(description = "조회할 레시피 UUID", required = true)
            @PathVariable UUID recipeUuid,
            @ParameterObject @PageableDefault(sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(Response.success(recipeService.getRecipeReviews(recipeUuid, pageable)));
    }

    @Operation(summary = "대댓글 더보기", description = "특정 댓글에 대한 대댓글 목록을 조회합니다.")
    @GetMapping("/{recipeUuid}/reviews/{reviewUuid}/replies")
    public ResponseEntity<Response<Page<ChildReviewResponse>>> getChildReviews(
            @Parameter(description = "조회할 레시피 UUID", required = true)
            @PathVariable UUID recipeUuid,
            @Parameter(description = "조회할 댓글 UUID", required = true)
            @PathVariable UUID reviewUuid,
            @ParameterObject @PageableDefault(sort = "createdDate", direction = Sort.Direction.ASC) Pageable pageable) {

        return ResponseEntity.ok(Response.success(recipeService.getChildReviews(recipeUuid, reviewUuid, pageable)));
    }

    @Operation(summary = "댓글 작성", description = "특정 레시피에 대한 댓글을 작성합니다.")
    @PostMapping("/{recipeUuid}/reviews")
    public ResponseEntity<Response<MessageResponse>> writeReview(
            @Parameter(description = "댓글을 작성할 레시피 UUID", required = true)
            @PathVariable UUID recipeUuid,
            @Valid @RequestBody ReviewWriteRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        MessageResponse response = recipeService.createReview(email, recipeUuid, request);
        recipeService.increaseReviewCount(recipeUuid);

        // 가정: 생성된 리뷰 UUID를 응답에 담고 있음
        String createdReviewUuid = response.getUuid().toString();

        return ResponseEntity
                .created(URI.create("/api/v1/recipes/" + recipeUuid.toString() + "/reviews/" + createdReviewUuid))
                .body(Response.success(response));
    }

    @Operation(summary = "댓글 수정", description = "특정 댓글을 수정합니다.")
    @PutMapping("/{recipeUuid}/reviews/{reviewUuid}")
    public ResponseEntity<Response<MessageResponse>> modifyReview(
            @Parameter(description = "레시피 UUID", required = true)
            @PathVariable UUID recipeUuid,
            @Parameter(description = "댓글 UUID", required = true)
            @PathVariable UUID reviewUuid,
            @Valid @RequestBody ReviewUpdateRequest request,
            Authentication authentication) {

        String email = authentication.getName();
        return ResponseEntity.ok(Response.success(
                recipeService.updateReview(email, recipeUuid, reviewUuid, request)
        ));
    }

    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @DeleteMapping("/{recipeUuid}/reviews/{reviewUuid}")
    public ResponseEntity<Response<MessageResponse>> deleteReview(
            @Parameter(description = "레시피 UUID", required = true)
            @PathVariable UUID recipeUuid,
            @Parameter(description = "댓글 UUID", required = true)
            @PathVariable UUID reviewUuid,
            Authentication authentication) {

        String email = authentication.getName();
        MessageResponse response = recipeService.deleteReview(email, recipeUuid, reviewUuid);
        recipeService.decreaseReviewCount(recipeUuid);

        return ResponseEntity.noContent().build();
    }
}
