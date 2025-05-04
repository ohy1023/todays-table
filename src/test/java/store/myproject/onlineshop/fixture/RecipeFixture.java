//package store.myproject.onlineshop.fixture;
//
//import com.github.javafaker.Faker;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.domain.recipe.Recipe;
//import store.myproject.onlineshop.domain.recipe.dto.*;
//import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;
//import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;
//
//import java.util.List;
//import java.util.Locale;
//
//public class RecipeFixture {
//
//    private static final Faker faker = new Faker(Locale.KOREA);
//
//    public static Recipe createRecipeEntity(Customer customer) {
//        RecipeMeta recipeMeta = RecipeMeta.builder()
//                .reviewCnt(0L)
//                .viewCnt(0L)
//                .likeCnt(0L)
//                .build();
//
//        return Recipe.builder()
//                .id(1L)
//                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
//                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
//                .recipeServings(faker.number().numberBetween(1, 6) + "인분") // "3인분" 형식
//                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분") // "45분" 형식
//                .customer(customer)
//                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
//                .recipeMeta(recipeMeta)
//                .build();
//    }
//
//    public static Recipe createRecipeEntityWithId(Long id, Customer customer) {
//        RecipeMeta recipeMeta = RecipeMeta.builder()
//                .reviewCnt(0L)
//                .viewCnt(0L)
//                .likeCnt(0L)
//                .build();
//
//        return Recipe.builder()
//                .id(id)
//                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
//                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
//                .recipeServings(faker.number().numberBetween(1, 6) + "인분") // "3인분" 형식
//                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분") // "45분" 형식
//                .customer(customer)
//                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
//                .recipeMeta(recipeMeta)
//                .build();
//    }
//
//    public static Recipe createRecipe(Customer customer) {
//        RecipeMeta recipeMeta = RecipeMeta.builder()
//                .reviewCnt(0L)
//                .viewCnt(0L)
//                .likeCnt(0L)
//                .build();
//
//        return Recipe.builder()
//                .recipeTitle(faker.food().dish()) // 랜덤 음식 이름
//                .recipeDescription(faker.lorem().sentence()) // 랜덤 문장
//                .recipeServings(faker.number().numberBetween(1, 6) + "인분") // "3인분" 형식
//                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분") // "45분" 형식
//                .customer(customer)
//                .thumbnailUrl(faker.internet().image()) // 랜덤 이미지 URL
//                .recipeMeta(recipeMeta)
//                .build();
//    }
//
//    public static RecipeCreateRequest createRecipeCreateRequest() {
//        return RecipeCreateRequest.builder()
//                .recipeTitle(faker.food().dish())
//                .recipeDescription(faker.lorem().paragraph())
//                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분")
//                .recipeServings(faker.number().digit() + "인분")
//                .itemIdList(List.of(1L, 2L, 3L))
//                .steps(List.of(RecipeStepRequest.builder()
//                        .order(faker.number().numberBetween(0, 50))
//                        .content(faker.lorem().paragraph())
//                        .imageUrl("https://example.com/thumb.jpg")
//                        .build()))
//                .build();
//    }
//
//    public static RecipeUpdateRequest createRecipeUpdateRequest() {
//        return RecipeUpdateRequest.builder()
//                .recipeTitle(faker.food().dish())
//                .recipeDescription(faker.lorem().paragraph())
//                .recipeCookingTime(faker.number().numberBetween(10, 120) + "분")
//                .recipeServings(faker.number().digit() + "인분")
//                .itemIdList(List.of(4L, 5L, 6L))
//                .steps(List.of(RecipeStepRequest.builder()
//                        .order(faker.number().numberBetween(0, 50))
//                        .content(faker.lorem().paragraph())
//                        .imageUrl("https://example.com/thumb.jpg")
//                        .build()))
//                .build();
//    }
//
//    public static RecipeDto createRecipeDto() {
//        return RecipeDto.builder()
//                .recipeTitle(faker.food().dish())
//                .recipeDescription(faker.lorem().paragraph())
//                .recipeCookingTime("30분")
//                .recipeServings("2인분")
//                .recipeWriter(faker.name().fullName())
//                .reviewCnt((long) faker.number().numberBetween(0, 50))
//                .likeCnt((long) faker.number().numberBetween(0, 100))
//                .steps(List.of(RecipeStepDto.builder()
//                                .stepOrder(faker.number().numberBetween(0, 50))
//                                .content(faker.lorem().paragraph())
//                                .imageUrl("https://example.com/thumb.jpg")
//                        .build()))
//                .build();
//    }
//
//    public static SimpleRecipeDto createSimpleRecipeDto() {
//        return SimpleRecipeDto.builder()
//                .recipeId(1L)
//                .title(faker.food().dish())
//                .recipeDescription(faker.lorem().paragraph())
//                .thumbnail("https://example.com/thumb.jpg")
//                .writer(faker.name().fullName())
//                .recipeCookingTime("20분")
//                .recipeServings("1인분")
//                .reviewCnt(10L)
//                .likeCnt(25L)
//                .build();
//    }
//}
