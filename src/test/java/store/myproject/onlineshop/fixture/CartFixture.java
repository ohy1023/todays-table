package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.customer.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

public class CartFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static CartAddRequest createAddRequest() {
        return new CartAddRequest(1L, 1L);
    }

    public static List<CartItemResponse> createCartItemResponses() {
        return List.of(
                new CartItemResponse(1L, faker.commerce().productName(), new BigDecimal(15000), 10000L, 1000L)
        );
    }

    public static Cart createCart(Customer customer) {
        return Cart.builder()
                .customer(customer)
                .build();
    }
}
