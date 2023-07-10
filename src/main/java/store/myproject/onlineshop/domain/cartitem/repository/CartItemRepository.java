package store.myproject.onlineshop.domain.cartitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.cartitem.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
}
