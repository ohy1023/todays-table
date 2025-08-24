package store.myproject.onlineshop.domain.cart;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.dto.cart.CartItemResponse;

public interface CartItemCustomRepository {

    Page<CartItemResponse> findByCartPage(Cart cart, Pageable pageable);
}
