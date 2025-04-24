package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;

import java.math.BigDecimal;
import java.util.List;

public class CartFixture {

    private static final Faker faker = new Faker();

    public static CartAddRequest createAddRequest() {
        return new CartAddRequest(1L, 100L);
    }

    public static List<CartItemResponse> createCartItemResponses() {
        return List.of(
                new CartItemResponse(1L, faker.commerce().productName(), new BigDecimal(15000), 10000L, 1000L)
        );
    }
}
