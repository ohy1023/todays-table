package store.myproject.onlineshop.domain.cartitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.CartItem;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    void deleteCartItemsByCart(Cart cart);
}
