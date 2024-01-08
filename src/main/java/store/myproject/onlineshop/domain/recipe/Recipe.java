package store.myproject.onlineshop.domain.recipe;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.review.Review;

import java.util.List;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Recipe extends BaseEntity {

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 제목
    private String recipeTitle;

    // 레시피 본문
    @Column(columnDefinition = "text")
    private String recipeContent;

    // 조회수
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int recipeViewCnt;

    // 조리시간
    private String recipeCookingTime;

    // 몇인분
    private int servings;

    // 댓글
    @Builder.Default
    @OneToMany(mappedBy = "recipe")
    private List<Review> reviewList;

    // 좋아요
    @Builder.Default
    @OneToMany(mappedBy = "recipe")
    private List<Like> likeList;

    // 레시피 재료
    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
    private List<RecipeItem> items;

    // 연관 관계 메서드
    public void addReview(Review review) {
        this.reviewList.add(review);
    }

    public void removeReview(Review review) {
        this.reviewList.remove(review);
    }

}
