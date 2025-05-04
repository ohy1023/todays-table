//package store.myproject.onlineshop.fixture;
//
//import com.github.javafaker.Faker;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.domain.recipe.Recipe;
//import store.myproject.onlineshop.domain.review.Review;
//import store.myproject.onlineshop.domain.review.dto.ReviewUpdateRequest;
//import store.myproject.onlineshop.domain.review.dto.ReviewWriteRequest;
//
//import java.util.Locale;
//
//public class ReviewFixture {
//
//    private static final Faker faker = new Faker(Locale.KOREA);
//
//
//    public static Review createParentReviewEntity(Recipe recipe, Customer customer) {
//        return Review.builder()
//                .id(1L)
//                .parentId(0L)
//                .recipe(recipe)
//                .customer(customer)
//                .reviewContent(faker.lorem().sentence())
//                .build();
//    }
//
//    public static Review createParentReview(Recipe recipe, Customer customer) {
//        return Review.builder()
//                .parentId(0L)
//                .recipe(recipe)
//                .customer(customer)
//                .reviewContent(faker.lorem().sentence())
//                .build();
//    }
//
//    public static Review createChildReviewEntity(Recipe recipe, Customer customer, Review review) {
//        return Review.builder()
//                .id(2L)
//                .parentId(review.getId())
//                .recipe(recipe)
//                .customer(customer)
//                .reviewContent(faker.lorem().sentence())
//                .build();
//    }
//
//    public static Review createChildReview(Recipe recipe, Customer customer, Review review) {
//        return Review.builder()
//                .parentId(review.getId())
//                .recipe(recipe)
//                .customer(customer)
//                .reviewContent(faker.lorem().sentence())
//                .build();
//    }
//
//    public static ReviewWriteRequest createReviewWriteRequest() {
//        return ReviewWriteRequest.builder()
//                .reviewParentId(null)
//                .reviewContent(faker.lorem().sentence())
//                .build();
//    }
//
//    public static ReviewUpdateRequest createReviewUpdateRequest() {
//        return ReviewUpdateRequest.builder()
//                .reviewContent(faker.lorem().sentence())
//                .build();
//    }
//}
