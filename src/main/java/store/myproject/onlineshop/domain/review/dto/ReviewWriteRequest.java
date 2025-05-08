package store.myproject.onlineshop.domain.review.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.review.Review;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "댓글 작성 요청 DTO")
public class ReviewWriteRequest {

    @Schema(description = "부모 댓글 ID (대댓글 작성 시 사용)", example = "0")
    private Long reviewParentId;

    @NotBlank
    @Schema(description = "댓글 내용", example = "이 레시피 정말 맛있어요!")
    private String reviewContent;

    public Review toEntity(Long reviewParentId, String reviewContent, Customer customer, Recipe recipe) {
        return Review.builder()
                .customer(customer)
                .recipe(recipe)
                .parentId(reviewParentId)
                .reviewContent(reviewContent)
                .build();
    }
}
