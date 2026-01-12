package store.myproject.onlineshop.fixture;

import net.datafaker.Faker;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;
import store.myproject.onlineshop.dto.recipe.*;
import store.myproject.onlineshop.dto.recipestep.RecipeStepDto;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class RecipeFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static Recipe createRecipeEntity(Customer customer) {
        RecipeMeta recipeMeta = RecipeMeta.builder()
                .reviewCnt(0L)
                .viewCnt(0L)
                .likeCnt(0L)
                .build();

        return Recipe.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
                .recipeServings(faker.number().numberBetween(1, 6)) // "3인분" 형식
                .recipeCookingTime(faker.number().numberBetween(10, 120) ) // "45분" 형식
                .customer(customer)
                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
                .recipeMeta(recipeMeta)
                .build();
    }

    public static Recipe createRecipeEntityWithId(Long id, Customer customer) {
        RecipeMeta recipeMeta = RecipeMeta.builder()
                .reviewCnt(0L)
                .viewCnt(0L)
                .likeCnt(0L)
                .build();

        return Recipe.builder()
                .id(id)
                .uuid(UUID.randomUUID())
                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
                .recipeServings(faker.number().numberBetween(1, 6)) // "3인분" 형식
                .recipeCookingTime(faker.number().numberBetween(10, 120)) // "45분" 형식
                .customer(customer)
                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
                .recipeMeta(recipeMeta)
                .build();
    }

    public static Recipe createRecipe(Customer customer) {
        RecipeMeta recipeMeta = RecipeMeta.builder()
                .reviewCnt(0L)
                .viewCnt(0L)
                .likeCnt(0L)
                .build();

        return Recipe.builder()
                .uuid(UUID.randomUUID())
                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
                .recipeServings(faker.number().numberBetween(1, 6)) // "3인분" 형식
                .recipeCookingTime(faker.number().numberBetween(10, 120)) // "45분" 형식
                .customer(customer)
                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
                .recipeMeta(recipeMeta)
                .build();
    }

    public static RecipeCreateRequest createRecipeCreateRequest() {
        return RecipeCreateRequest.builder()
                .recipeTitle(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
                .recipeCookingTime(faker.number().numberBetween(10, 120))
                .recipeServings(faker.number().numberBetween(1, 6))
                .thumbnailUrl(faker.internet().image())
                .itemUuidList(List.of(UUID.fromString(faker.internet().uuid()), UUID.fromString(faker.internet().uuid()), UUID.fromString(faker.internet().uuid())))
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
                .thumbnailUrl(faker.internet().image())
                .recipeCookingTime(faker.number().numberBetween(10, 120))
                .recipeServings(faker.number().numberBetween(1, 6))
                .itemUuidList(List.of(UUID.fromString(faker.internet().uuid()), UUID.fromString(faker.internet().uuid()), UUID.fromString(faker.internet().uuid())))
                .steps(List.of(RecipeStepRequest.builder()
                        .order(faker.number().numberBetween(0, 50))
                        .content(faker.lorem().paragraph())
                        .imageUrl("https://example.com/thumb.jpg")
                        .build()))
                .build();
    }

    public static RecipeDto createRecipeDto(UUID uuid) {
        return RecipeDto.builder()
                .recipeUuid(uuid)
                .recipeTitle(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
                .recipeCookingTime(30)
                .recipeServings(2)
                .recipeWriter(faker.name().fullName())
                .steps(List.of(RecipeStepDto.builder()
                        .stepOrder(faker.number().numberBetween(0, 50))
                        .content(faker.lorem().paragraph())
                        .imageUrl(faker.internet().image())
                        .build()))
                .build();
    }

    public static SimpleRecipeDto createSimpleRecipeDto() {
        return SimpleRecipeDto.builder()
                .recipeUuid(UUID.fromString(faker.internet().uuid()))
                .title(faker.food().dish())
                .recipeDescription(faker.lorem().paragraph())
                .thumbnail(faker.internet().image())
                .writer(faker.name().fullName())
                .recipeCookingTime(20)
                .recipeServings(1)
                .reviewCnt(10L)
                .likeCnt(25L)
                .build();
    }
}
