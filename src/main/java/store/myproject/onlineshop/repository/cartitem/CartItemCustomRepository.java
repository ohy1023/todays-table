package store.myproject.onlineshop.repository.cartitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;

public interface CartItemCustomRepository {

    Page<CartItemResponse> findByCartPage(Cart cart, Pageable pageable);
}
