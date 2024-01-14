package store.myproject.onlineshop.domain.recipe;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateResponse;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
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

    // 레시피 본문
    @Column(columnDefinition = "text")
    private String recipeContent;

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

    // 레시피 사진의 URL
    @Builder.Default
    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ImageFile> imageFileList = new ArrayList<>();

    public void updateRecipe(RecipeUpdateRequest request) {
        this.recipeTitle = request.getRecipeTitle();
        this.recipeContent = request.getRecipeContent();
        this.recipeCookingTime = request.getRecipeCookingTime();
        this.recipeServings = request.getRecipeServings();
    }


    public RecipeCreateResponse fromEntity(Recipe recipe) {
        return RecipeCreateResponse.builder()
                .recipeTitle(recipe.getRecipeTitle())
                .recipeContent(recipe.getRecipeContent())
                .recipeCookingTime(recipe.getRecipeCookingTime())
                .recipeServings(recipe.getRecipeServings())
                .recipeWriter(recipe.getCustomer().getNickName())
                .recipeView(recipe.getRecipeViewCnt())
                .itemNameList(recipe.getItemList().stream()
                        .map(RecipeItem::getItem)
                        .map(Item::getItemName)
                        .collect(Collectors.toList()))
                .recipeImageList(recipe.getImageFileList().stream()
                        .map(ImageFile::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    public RecipeDto toDto() {
        return RecipeDto.builder()
                .recipeTitle(this.getRecipeTitle())
                .recipeContent(this.getRecipeContent())
                .recipeCookingTime(this.getRecipeCookingTime())
                .recipeServings(this.getRecipeServings())
                .recipeWriter(this.getCustomer().getNickName())
                .recipeView(this.getRecipeViewCnt())
                .itemNameList(this.getItemList().stream()
                        .map(RecipeItem::getItem)
                        .map(Item::getItemName)
                        .collect(Collectors.toList()))
                .recipeImageList(this.getImageFileList().stream()
                        .map(ImageFile::getImageUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    public void updateView() {
        this.recipeViewCnt++;
    }

}
