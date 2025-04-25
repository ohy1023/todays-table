package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;

import java.util.List;
import java.util.Locale;

public class RecipeFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static Recipe createRecipe(Customer customer) {
        return Recipe.builder()
                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
                .recipeServings(faker.number().numberBetween(1, 6) + "인분") // "3인분" 형식
                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분") // "45분" 형식
                .customer(customer)
                .recipeViewCnt(0)
                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
                .build();
    }

    public static RecipeCreateRequest createRecipeCreateRequest() {
        return RecipeCreateRequest.builder()
                .recipeTitle(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분")
                .recipeServings(faker.number().digit() + "인분")
                .itemIdList(List.of(1L, 2L, 3L))
                .steps(List.of(RecipeStepRequest.builder()
                        .order(faker.number().numberBetween(0, 50))
                        .content(faker.lorem().paragraph())
                        .imageUrl("https://example.com/thumb.jpg")
                        .build()))
                .build();
    }

    public static RecipeUpdateRequest createRecipeUpdateRequest() {
        return RecipeUpdateRequest.builder()
                .recipeTitle(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분")
                .recipeServings(faker.number().digit() + "인분")
                .itemIdList(List.of(4L, 5L, 6L))
                .steps(List.of(RecipeStepRequest.builder()
                        .order(faker.number().numberBetween(0, 50))
                        .content(faker.lorem().paragraph())
                        .imageUrl("https://example.com/thumb.jpg")
                        .build()))
                .build();
    }

    public static RecipeDto createRecipeDto() {
        return RecipeDto.builder()
                .recipeTitle(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
                .recipeCookingTime("30분")
                .recipeServings("2인분")
                .recipeWriter(faker.name().fullName())
                .recipeView(faker.number().numberBetween(100, 10000))
                .reviewCnt((long) faker.number().numberBetween(0, 50))
                .likeCnt((long) faker.number().numberBetween(0, 100))
                .itemIdList(List.of(1L, 2L, 3L))
                .steps(List.of(RecipeStepDto.builder()
                                .stepOrder(faker.number().numberBetween(0, 50))
                                .content(faker.lorem().paragraph())
                                .imageUrl("https://example.com/thumb.jpg")
                        .build()))
                .build();
    }

    public static SimpleRecipeDto createSimpleRecipeDto() {
        return SimpleRecipeDto.builder()
                .recipeId(1L)
                .title(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
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
