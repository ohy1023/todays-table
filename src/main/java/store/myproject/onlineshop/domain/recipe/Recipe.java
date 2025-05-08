package store.myproject.onlineshop.domain.recipe;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
import store.myproject.onlineshop.domain.review.Review;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE Recipe SET deleted_date = CURRENT_TIMESTAMP WHERE recipe_id = ?")
@Table(
        indexes = {
                @Index(name = "idx_recipe_deleted_created", columnList = "deleted_date, created_date")
        }
)
public class Recipe extends BaseEntity {

    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "recipe_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;

    // 제목
    private String recipeTitle;

    // 레시피 간단 설명
    @Column(columnDefinition = "text")
    private String recipeDescription;

    // 레시피 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // 조리시간
    private String recipeCookingTime;

    // 몇인분
    private String recipeServings;

    // 썸네일
    @Setter
    private String thumbnailUrl;

    // 통계 테이블
    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_meta_id")
    private RecipeMeta recipeMeta;

    // 댓글
    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviewList = new ArrayList<>();

    // 좋아요
    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likeList = new ArrayList<>();

    // 레시피 재료
    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeItem> itemList = new ArrayList<>();

    // 레시피 단계
    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("stepOrder ASC")
    private List<RecipeStep> stepList = new ArrayList<>();


    public void updateRecipe(RecipeUpdateRequest request) {
        this.recipeTitle = request.getRecipeTitle();
        this.recipeDescription = request.getRecipeDescription();
        this.recipeCookingTime = request.getRecipeCookingTime();
        this.recipeServings = request.getRecipeServings();
    }

    public void addItem(RecipeItem recipeItem) {
        this.itemList.add(recipeItem);
        recipeItem.setRecipe(this);  // 연관관계 주인 쪽 세팅
    }

    public void addItems(List<RecipeItem> recipeItems) {
        for (RecipeItem recipeItem : recipeItems) {
            addItem(recipeItem);
        }
    }

    public void addStep(RecipeStep step) {
        stepList.add(step);
        step.setRecipe(this);
    }

    public void addSteps(List<RecipeStep> steps) {
        for (RecipeStep step : steps) {
            addStep(step);
        }
    }
}
