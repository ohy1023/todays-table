package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.dto.common.MessageCode;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.dto.cart.CartAddRequest;
import store.myproject.onlineshop.domain.cart.CartItem;
import store.myproject.onlineshop.dto.cart.CartItemResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.CartFixture;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.global.utils.MessageUtil;
import store.myproject.onlineshop.domain.cart.CartRepository;
import store.myproject.onlineshop.domain.cart.CartItemRepository;
import store.myproject.onlineshop.domain.customer.CustomerRepository;
import store.myproject.onlineshop.domain.item.ItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private MessageUtil messageUtil;

    Customer customer = CustomerFixture.createCustomer();
    Brand brand = BrandFixture.createBrand();
    Item item = ItemFixture.createItem(brand);
    Cart cart = CartFixture.createCart(customer);

    @Test
    @DisplayName("장바구니에 상품 추가 성공")
    void add_item_to_cart_success() {

        // given
        UUID itemUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        CartAddRequest request = CartFixture.createAddRequest(itemUuid, 10L);

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(itemRepository.findByUuid(request.getItemUuid())).willReturn(Optional.of(item));
        given(cartItemRepository.findByCartAndItem(cart, item)).willReturn(Optional.empty());
        given(messageUtil.get(MessageCode.CART_ITEM_ADDED)).willReturn("장바구니 상품 추가 성공");

        // when
        MessageResponse response = cartService.addItemToCart(request, customer.getEmail());

        //then
        assertThat(response.getMessage()).isEqualTo("장바구니 상품 추가 성공");
        then(cartItemRepository).should().save(any(CartItem.class));
    }

    @Test
    @DisplayName("기존 항목 수량 증가")
    void add_item_to_cart_existing_item_quantity_increased() {

        // given
        CartAddRequest request = new CartAddRequest(item.getUuid(), 2L);
        CartItem existingCartItem = CartItem.createCartItem(item, 3L, cart);

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.of(item));
        given(cartItemRepository.findByCartAndItem(cart, item)).willReturn(Optional.of(existingCartItem));
        given(messageUtil.get(MessageCode.CART_ITEM_ADDED)).willReturn("장바구니 상품 추가 성공");

        // when
        MessageResponse response = cartService.addItemToCart(request, customer.getEmail());

        // then
        then(cartItemRepository).should(never()).save(any());
        assertThat(response.getMessage()).isEqualTo("장바구니 상품 추가 성공");
        assertThat(existingCartItem.getCartItemCnt()).isEqualTo(5L); // 3 + 2
    }

    @Test
    @DisplayName("재고 부족 시 예외 발생")
    void add_item_to_cart_not_enough_stock() {
        // given
        CartAddRequest request = new CartAddRequest(item.getUuid(), 100L);

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> cartService.addItemToCart(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.NOT_ENOUGH_STOCK.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 고객 예외")
    void add_item_to_cart_customer_not_found() {
        // given
        CartAddRequest request = new CartAddRequest(item.getUuid(), 1L);
        given(customerRepository.findByEmail(any())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.addItemToCart(request, "not@exist.com"))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CUSTOMER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("존재하지 않는 상품 예외")
    void add_item_to_cart_item_not_found() {
        // given

        CartAddRequest request = new CartAddRequest(item.getUuid(), 1L);

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.addItemToCart(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("장바구니 비우기 성공")
    void clear_cart_success() {
        // given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(messageUtil.get(MessageCode.CART_CLEARED)).willReturn("장바구니 비움");

        // when
        MessageResponse response = cartService.clearCart(customer.getEmail());

        // then
        then(cartItemRepository).should().deleteByCart(cart);
        assertThat(response.getMessage()).isEqualTo("장바구니 비움");
    }

    @Test
    @DisplayName("장바구니가 없으면 예외 발생")
    void clear_cart_fail_if_cart_not_found() {
        // given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.clearCart(customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("장바구니");
    }

    @Test
    @DisplayName("장바구니 품목 전체 조회 성공")
    void get_cart_items_success() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        Page<CartItemResponse> fakePage = new PageImpl<>(List.of());

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(cartItemRepository.findByCartPage(cart, pageable)).willReturn(fakePage);

        // when
        Page<CartItemResponse> result = cartService.getCartItems(customer.getEmail(), pageable);

        // then
        then(cartItemRepository).should().findByCartPage(cart, pageable);
        assertThat(result.getContent()).isEmpty();
    }

    @Test
    @DisplayName("장바구니 항목 삭제 성공")
    void delete_item_from_cart_success() {
        // given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.of(item));
        given(messageUtil.get(MessageCode.CART_ITEM_DELETED)).willReturn("삭제 성공");

        // when
        MessageResponse response = cartService.deleteItemFromCart(item.getUuid(), customer.getEmail());

        // then
        then(cartItemRepository).should().deleteCartItem(cart, item);
        assertThat(response.getMessage()).isEqualTo("삭제 성공");
    }

    @Test
    @DisplayName("삭제할 상품이 없으면 예외 발생")
    void delete_item_from_cart_fail_if_item_not_found() {
        // given
        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> cartService.deleteItemFromCart(item.getUuid(), customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.ITEM_NOT_FOUND.getMessage());
    }
}