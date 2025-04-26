package store.myproject.onlineshop.repository.cartitem;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.repository.cart.CartRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.item.ItemRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class CartItemRepositoryTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Test
    @DisplayName("장바구니 조회 성공")
    void find_by_cart_and_item_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Cart cart = cartRepository.save(Cart.createCart(customer));
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        cartItemRepository.save(CartItem.createCartItem(item, 1L, cart));

        // when
        Optional<CartItem> result = cartItemRepository.findByCartAndItem(cart, item);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getCart()).isEqualTo(cart);
        assertThat(result.get().getItem()).isEqualTo(item);
        assertThat(result.get().getCartItemCnt()).isEqualTo(1L);
    }

    @Test
    @DisplayName("장바구니 비우기 성공")
    void delete_by_cart_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Cart cart = cartRepository.save(Cart.createCart(customer));
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        cartItemRepository.save(CartItem.createCartItem(item, 1L, cart));

        // when
        cartItemRepository.deleteByCart(cart);

        // then
        Optional<CartItem> result = cartItemRepository.findByCartAndItem(cart, item);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("장바구니에서 특정 상품 삭제 성공")
    void delete_by_item_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Cart cart = cartRepository.save(Cart.createCart(customer));
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        cartItemRepository.save(CartItem.createCartItem(item, 1L, cart));

        // when
        cartItemRepository.deleteByItem(item);

        // then
        Optional<CartItem> result = cartItemRepository.findByCartAndItem(cart, item);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("특정 장바구니에서 특정 상품 삭제 성공")
    void delete_cart_item_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Cart cart = cartRepository.save(Cart.createCart(customer));
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        cartItemRepository.save(CartItem.createCartItem(item, 1L, cart));

        // when
        cartItemRepository.deleteCartItem(cart, item);

        // then
        Optional<CartItem> result = cartItemRepository.findByCartAndItem(cart, item);
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("장바구니 아이템 페이지 조회 성공")
    void find_by_cart_page_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Cart cart = cartRepository.save(Cart.createCart(customer));
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item1 = itemRepository.save(ItemFixture.createItem(brand));
        Item item2 = itemRepository.save(ItemFixture.createItem(brand));
        cartItemRepository.save(CartItem.createCartItem(item1, 1L, cart));
        cartItemRepository.save(CartItem.createCartItem(item2, 1L, cart));

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<CartItemResponse> result = cartItemRepository.findByCartPage(cart, pageRequest);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent()).extracting("itemId")
                .containsExactlyInAnyOrder(item1.getId(), item2.getId());
    }
}
