package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.customer.Customer;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CartFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static CartAddRequest createAddRequest(UUID ItemUuid, Long itemCnt) {
        return new CartAddRequest(ItemUuid, itemCnt);
    }

    public static List<CartItemResponse> createCartItemResponses() {
        return List.of(
                new CartItemResponse(UUID.fromString(faker.internet().uuid()), faker.commerce().productName(), faker.internet().image(), new BigDecimal(15000), 10000L, 1000L)
        );
    }

    public static Cart createCart(Customer customer) {
        return Cart.builder()
                .customer(customer)
                .build();
    }
}
