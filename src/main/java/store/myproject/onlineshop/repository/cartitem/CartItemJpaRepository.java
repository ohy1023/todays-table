package store.myproject.onlineshop.repository.cartitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.item.Item;

import java.util.Optional;

@Repository
public interface CartItemJpaRepository extends JpaRepository<CartItem, Long> {

    @Modifying
    @Query("delete from CartItem ci where ci.cart = :cart")
    void deleteByCart(@Param("cart") Cart cart);

    void deleteByItem(Item item);

    Optional<CartItem> findByCartAndItem(Cart cart, Item item);

    @Modifying
    @Query("delete from CartItem ci where ci.cart = :cart and ci.item =:item ")
    void deleteCartItem(@Param("cart") Cart cart, @Param("item") Item item);
}
