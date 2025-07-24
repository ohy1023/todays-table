package store.myproject.onlineshop.repository.cartitem;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.item.Item;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class CartItemRepository {

    private final CartItemJpaRepository cartItemJpaRepository;
    private final CartItemMapper cartItemMapper;

    public Optional<CartItem> findById(Long id) {
        return cartItemJpaRepository.findById(id);
    }

    public void save(final CartItem cartItem) {
        cartItemJpaRepository.save(cartItem);
    }

    public void deleteByCart(Cart cart) {
        cartItemJpaRepository.deleteByCart(cart);
    }

    public void deleteByItem(Item item) {
        cartItemJpaRepository.deleteByItem(item);
    }

    public Optional<CartItem> findByCartAndItem(Cart cart, Item item) {
        return cartItemJpaRepository.findByCartAndItem(cart, item);
    }

    public void deleteCartItem(Cart cart, Item item) {
        cartItemJpaRepository.deleteCartItem(cart,item);
    }

    public Page<CartItemResponse> findByCartPage(Cart cart, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            String orderBy = pageable.getSort().stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            PageHelper.orderBy(orderBy);
        }

        List<CartItemResponse> cartItemResponses = cartItemMapper.findByCartPage(cart.getId());

        PageInfo<CartItemResponse> pageInfo = new PageInfo<>(cartItemResponses);

        return new PageImpl<>(cartItemResponses, pageable, pageInfo.getTotal());
    }

}
