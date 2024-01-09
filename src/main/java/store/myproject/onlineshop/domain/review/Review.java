package store.myproject.onlineshop.domain.review;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateResponse;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteResponse;

import static jakarta.persistence.FetchType.*;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE review SET deleted_date = CURRENT_TIMESTAMP WHERE review_id = ?")
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "parent_id")
    private Long parentId;

    // 리뷰 내용
    private String reviewContent;

    // 리뷰를 작성한 고객
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    // 리뷰 내용 수정
    public void updateReview(ReviewUpdateRequest reviewUpdateRequest) {
        this.reviewContent = reviewUpdateRequest.getReviewContent();
    }


    // 리뷰를 응답 객체로 변환
    public ReviewWriteResponse toWriteResponse() {
        return ReviewWriteResponse.builder()
                .reviewType(this.getReviewType())        // 리뷰의 유형 (댓글 또는 답글)
                .reviewContent(this.getReviewContent())  // 리뷰 내용
                .email(this.customer.getEmail())          // 리뷰를 작성한 고객의 이메일
                .recipeId(this.recipe.getId())           // 관련된 레시피의 ID
                .build();
    }

    public ReviewUpdateResponse toUpdateResponse() {
        return ReviewUpdateResponse.builder()
                .reviewType(this.getReviewType())        // 리뷰의 유형 (댓글 또는 답글)
                .reviewContent(this.getReviewContent())  // 리뷰 내용
                .email(this.customer.getEmail())          // 리뷰를 작성한 고객의 이메일
                .recipeId(this.recipe.getId())           // 관련된 레시피의 ID
                .build();
    }

    // 리뷰의 유형을 결정 (댓글 또는 답글)
    private String getReviewType() {
        return parentId == 0 ? "댓글" : "대댓글";
    }

    // 연관 관계 메서드
    public void addReviewToRecipe(Recipe recipe) {
        this.recipe = recipe;
        recipe.getReviewList().add(this);  // 연관된 Recipe에도 Review를 추가
    }

    public void removeReviewToRecipe() {
        if (recipe != null) {
            recipe.getReviewList().remove(this);  // 연관된 Recipe에서도 Review를 제거
            this.recipe = null;
        }
    }


}
