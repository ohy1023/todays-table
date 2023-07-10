package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cart.repository.CartRepository;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.cartitem.repository.CartItemRepository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.exception.AppException;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final CustomerRepository customerRepository;

    private final ItemRepository itemRepository;

    public MessageResponse addCart(CartAddRequest request, Authentication authentication) {

        String email = authentication.getName();

        Customer findCustomer = customerRepository.findByEmail(email).get();

        Cart cart = cartRepository.findByCustomer(findCustomer)
                .orElseGet(() -> cartRepository.save(Cart.createCart(findCustomer)));

        Item findItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

        CartItem cartItem = CartItem.createCartItem(findItem, request.getItemCnt(), cart);

        cartItemRepository.save(cartItem);

        return new MessageResponse("해당 아이템이 장바구니에 추가되었습니다.");
    }
}
