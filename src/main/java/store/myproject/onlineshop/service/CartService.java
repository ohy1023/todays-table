package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cart.repository.CartRepository;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.cartitem.repository.CartItemRepository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.exception.AppException;

import java.util.Optional;

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

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Cart myCart = cartRepository.findByCustomer(findCustomer)
                .orElseGet(() -> cartRepository.save(Cart.createCart(findCustomer)));

        Item findItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

        Optional<CartItem> cartItem = cartItemRepository.findByCartAndItem(myCart, findItem);

        if (cartItem.isEmpty()) {
            if (findItem.getStock() < request.getItemCnt()) {
                throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
            }
            CartItem createCartItem = CartItem.createCartItem(findItem, request.getItemCnt(), myCart);

            cartItemRepository.save(createCartItem);
        } else {
            Long cnt = cartItem.get().getCartItemCnt() + request.getItemCnt();

            if (findItem.getStock() < cnt) {
                throw new AppException(NOT_ENOUGH_STOCK, NOT_ENOUGH_STOCK.getMessage());
            }

            cartItem.get().plusItemCnt(request.getItemCnt());
        }

        return new MessageResponse("해당 아이템이 장바구니에 추가되었습니다.");
    }

    public MessageResponse deleteCarts(Authentication authentication) {
        String email = authentication.getName();

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Cart findCart = cartRepository.findByCustomer(findCustomer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND, CART_NOT_FOUND.getMessage()));

        cartItemRepository.deleteByCart(findCart);

        return new MessageResponse("장바구니를 비웠습니다.");
    }

    public Page<CartItemResponse> selectAllCartItem(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Cart myCart = cartRepository.findByCustomer(findCustomer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND, CART_NOT_FOUND.getMessage()));

        return cartItemRepository.findByCartPage(myCart, pageable);

    }
}
