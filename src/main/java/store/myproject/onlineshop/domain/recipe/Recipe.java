package store.myproject.onlineshop.domain.recipe;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;
import store.myproject.onlineshop.domain.review.Review;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


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

    // 레시피 간단 설명
    @Column(columnDefinition = "text")
    private String recipeDescription;

    // 레시피 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    // 조회수
    @Column(columnDefinition = "integer default 0", nullable = false)
    private int recipeViewCnt;

    // 조리시간
    private String recipeCookingTime;

    // 몇인분
    private String recipeServings;

    // 썸네일
    @Setter
    private String thumbnailUrl;

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
    @Setter
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "recipe_id")
    @OrderBy("stepOrder ASC")
    private List<RecipeStep> stepList = new ArrayList<>();


    public void updateRecipe(RecipeUpdateRequest request) {
        this.recipeTitle = request.getRecipeTitle();
        this.recipeDescription = request.getRecipeDescription();
        this.recipeCookingTime = request.getRecipeCookingTime();
        this.recipeServings = request.getRecipeServings();
    }

    public RecipeDto toDto() {
        return RecipeDto.builder()
                .recipeTitle(this.getRecipeTitle())
                .recipeDescription(this.getRecipeDescription())
                .recipeCookingTime(this.getRecipeCookingTime())
                .recipeServings(this.getRecipeServings())
                .recipeWriter(this.getCustomer().getNickName())
                .recipeView(this.getRecipeViewCnt())
                .thumbnailUrl(this.getThumbnailUrl())
                .likeCnt((long) this.getLikeList().size())
                .reviewCnt((long) this.getReviewList().size())
                .itemIdList(this.getItemList().stream()
                        .map(RecipeItem::getItem)
                        .map(Item::getId)
                        .toList())
                .steps(this.getStepList().stream()
                        .map(step -> RecipeStepDto.builder()
                                .stepOrder(step.getStepOrder())
                                .content(step.getContent())
                                .imageUrl(step.getImageUrl())
                                .build())
                        .toList())
                .build();
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

    public void addViewCnt() {
        this.recipeViewCnt++;
    }
}
