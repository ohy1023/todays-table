package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateRequest;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;

import java.util.List;
import java.util.Locale;

public class RecipeFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static RecipeCreateRequest createRecipeCreateRequest() {
        return RecipeCreateRequest.builder()
                .recipeTitle(faker.food().dish())
                .recipeContent(faker.lorem().paragraph())
                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분")
                .recipeServings(faker.number().digit() + "인분")
                .itemIdList(List.of(1L, 2L, 3L))
                .build();
    }

    public static RecipeUpdateRequest createRecipeUpdateRequest() {
        return RecipeUpdateRequest.builder()
                .recipeTitle(faker.food().dish())
                .recipeContent(faker.lorem().paragraph())
                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분")
                .recipeServings(faker.number().digit() + "인분")
                .itemIdList(List.of(4L, 5L, 6L))
                .build();
    }

    public static RecipeDto createRecipeDto() {
        return RecipeDto.builder()
                .recipeTitle(faker.food().dish())
                .recipeContent(faker.lorem().paragraph())
                .recipeCookingTime("30분")
                .recipeServings("2인분")
                .recipeWriter(faker.name().fullName())
                .recipeView(faker.number().numberBetween(100, 10000))
                .reviewCnt((long) faker.number().numberBetween(0, 50))
                .likeCnt((long) faker.number().numberBetween(0, 100))
                .itemNameList(List.of("고기", "채소", "소스"))
                .recipeImageList(List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg"))
                .build();
    }

    public static SimpleRecipeDto createSimpleRecipeDto() {
        return SimpleRecipeDto.builder()
                .recipeId(1L)
                .title(faker.food().dish())
                .thumbnail("https://example.com/thumb.jpg")
                .writer(faker.name().fullName())
                .recipeCookingTime("20분")
                .recipeServings("1인분")
                .recipeView(faker.number().numberBetween(50, 500))
                .reviewCnt(10L)
                .likeCnt(25L)
                .build();
    }
}
