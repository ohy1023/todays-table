package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.review.dto.ReviewUpdateRequest;
import store.myproject.onlineshop.domain.review.dto.ReviewWriteRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class ReviewFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static ReviewWriteRequest createReviewWriteRequest() {
        return ReviewWriteRequest.builder()
                .reviewParentId(null)
                .reviewContent(faker.lorem().sentence())
                .build();
    }

    public static ReviewUpdateRequest createReviewUpdateRequest() {
        return ReviewUpdateRequest.builder()
                .reviewContent(faker.lorem().sentence())
                .build();
    }
}
