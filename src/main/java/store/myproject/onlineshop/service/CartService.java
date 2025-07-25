package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.repository.cart.CartRepository;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.repository.cartitem.CartItemRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;
import java.util.UUID;

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
    private final MessageUtil messageUtil;

    /**
     * 장바구니에 품목 추가
     */
    public MessageResponse addItemToCart(CartAddRequest request, String email) {
        Customer customer = findCustomerByEmail(email);
        Cart cart = findOrCreateCartByCustomer(customer);
        Item item = itemRepository.findByUuid(request.getItemUuid())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

        cartItemRepository.findByCartAndItem(cart, item)
                .ifPresentOrElse(
                        existingItem -> updateItemCount(existingItem, request.getItemCnt(), item),
                        () -> addNewItemToCart(cart, item, request.getItemCnt())
                );

        return new MessageResponse(messageUtil.get(MessageCode.CART_ITEM_ADDED));
    }

    /**
     * 장바구니 비우기 (모든 품목 삭제)
     */
    public MessageResponse clearCart(String email) {
        Customer customer = findCustomerByEmail(email);
        Cart cart = findCartByCustomer(customer);

        cartItemRepository.deleteByCart(cart);
        return new MessageResponse(messageUtil.get(MessageCode.CART_CLEARED));
    }

    /**
     * 장바구니 품목 전체 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<CartItemResponse> getCartItems(String email, Pageable pageable) {
        Customer customer = findCustomerByEmail(email);
        Cart cart = findCartByCustomer(customer);

        return cartItemRepository.findByCartPage(cart, pageable);
    }

    /**
     * 장바구니에서 특정 품목 삭제
     */
    public MessageResponse deleteItemFromCart(UUID uuid, String email) {
        Customer customer = findCustomerByEmail(email);// 고객 존재 여부 확인

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND));


        Item item = itemRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

        cartItemRepository.deleteCartItem(cart, item);

        return new MessageResponse(messageUtil.get(MessageCode.CART_ITEM_DELETED));
    }

    public void insertInitialCartItems(String email) {

        Customer customer = findCustomerByEmail(email);

        Cart cart = findCartByCustomer(customer);

        cartItemRepository.deleteByCart(cart);

        List<Item> items = itemRepository.findTop3ByOrderByIdAsc(PageRequest.of(0, 3));

        for (Item item : items) {
            Long count = 1L;
            CartItem newCartItem = CartItem.createCartItem(item, count, cart);
            cartItemRepository.save(newCartItem);
        }

    }

    // === private utils ===

    /**
     * 이메일로 고객 조회 (없으면 예외 발생)
     */
    private Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));
    }

    /**
     * 고객의 장바구니 조회, 없으면 새로 생성
     */
    private Cart findOrCreateCartByCustomer(Customer customer) {
        return cartRepository.findByCustomer(customer)
                .orElseGet(() -> cartRepository.save(Cart.createCart(customer)));
    }

    /**
     * 고객의 장바구니 조회 (없으면 예외 발생)
     */
    private Cart findCartByCustomer(Customer customer) {
        return cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND));
    }

    /**
     * 기존 장바구니 품목 수량 증가 (재고 초과 여부 검증 포함)
     */
    private void updateItemCount(CartItem cartItem, Long additionalCount, Item item) {
        long newCount = cartItem.getCartItemCnt() + additionalCount;
        validateStockEnough(item, newCount);
        cartItem.plusItemCnt(additionalCount);
    }

    /**
     * 장바구니에 새 품목 추가
     */
    private void addNewItemToCart(Cart cart, Item item, Long count) {
        validateStockEnough(item, count);
        CartItem newCartItem = CartItem.createCartItem(item, count, cart);
        cartItemRepository.save(newCartItem);
    }

    /**
     * 재고 부족 여부 검증
     */
    private void validateStockEnough(Item item, long requiredCount) {
        if (item.getStock() < requiredCount) {
            throw new AppException(NOT_ENOUGH_STOCK);
        }
    }

}
