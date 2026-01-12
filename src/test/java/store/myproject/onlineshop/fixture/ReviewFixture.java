package store.myproject.onlineshop.fixture;

import net.datafaker.Faker;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.review.Review;
import store.myproject.onlineshop.dto.review.ReviewUpdateRequest;
import store.myproject.onlineshop.dto.review.ReviewWriteRequest;

import java.util.Locale;
import java.util.UUID;

public class ReviewFixture {

    private static final Faker faker = new Faker(Locale.KOREA);


    public static Review createParentReviewEntity(Recipe recipe, Customer customer) {
        return Review.builder()
                .uuid(UUID.randomUUID())
                .id(1L)
                .parentId(null)
                .recipe(recipe)
                .customer(customer)
                .reviewContent(faker.lorem().sentence())
                .build();
    }

    public static Review createParentReview(Recipe recipe, Customer customer) {
        return Review.builder()
                .parentId(null)
                .uuid(UUID.randomUUID())
                .recipe(recipe)
                .customer(customer)
                .reviewContent(faker.lorem().sentence())
                .build();
    }

    public static Review createChildReviewEntity(Recipe recipe, Customer customer, Review review) {
        return Review.builder()
                .id(2L)
                .uuid(UUID.randomUUID())
                .parentId(review.getId())
                .recipe(recipe)
                .customer(customer)
                .reviewContent(faker.lorem().sentence())
                .build();
    }

    public static Review createChildReview(Recipe recipe, Customer customer, Review review) {
        return Review.builder()
                .uuid(UUID.randomUUID())
                .parentId(review.getId())
                .recipe(recipe)
                .customer(customer)
                .reviewContent(faker.lorem().sentence())
                .build();
    }

    public static ReviewWriteRequest createReviewWriteRequest() {
        return ReviewWriteRequest.builder()
                .reviewUuid(null)
                .reviewContent(faker.lorem().sentence())
                .build();
    }

    public static ReviewUpdateRequest createReviewUpdateRequest() {
        return ReviewUpdateRequest.builder()
                .reviewContent(faker.lorem().sentence())
                .build();
    }
}
