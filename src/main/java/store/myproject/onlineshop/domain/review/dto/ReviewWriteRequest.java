package store.myproject.onlineshop.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.review.Review;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewWriteRequest {

    private Long reviewParentId;

    @NotBlank
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
