package store.myproject.onlineshop.domain.review;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;
import store.myproject.onlineshop.domain.common.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.dto.review.ReviewUpdateRequest;

import java.util.UUID;

import static jakarta.persistence.FetchType.*;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE review SET deleted_date = CURRENT_TIMESTAMP WHERE review_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(
        name = "review",
        indexes = {
                @Index(name = "idx_review_uuid", columnList = "review_uuid"),
                @Index(name = "idx_deleted_date", columnList = "deleted_date")
        }
)
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "review_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID uuid;

    @Column(name = "parent_id")
    private Long parentId;

    // 리뷰 내용
    @Column(name = "review_content")
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
