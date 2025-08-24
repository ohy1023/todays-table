//package store.myproject.onlineshop.repository.cart;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import store.myproject.onlineshop.domain.cart.Cart;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.fixture.CustomerFixture;
//import store.myproject.onlineshop.global.config.TestConfig;
//import store.myproject.onlineshop.domain.customer.CustomerRepository;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(TestConfig.class)
//@ActiveProfiles("test")
//class CartRepositoryTest {
//
//    @Autowired
//    CartRepository cartRepository;
//
//    @Autowired
//    CustomerRepository customerRepository;
//
//    @Test
//    @DisplayName("Customer로 Cart 조회 성공")
//    void find_cart_by_customer_success() {
//        // given
//        Customer customer = CustomerFixture.createCustomer();
//        customerRepository.save(customer);
//
//        Cart cart = Cart.createCart(customer);
//        cartRepository.save(cart);
//
//        // when
//        Optional<Cart> result = cartRepository.findByCustomer(customer);
//
//        // then
//        assertThat(result).isPresent();
//        assertThat(result.get().getCustomer()).isEqualTo(customer);
//    }
//
//    @Test
//    @DisplayName("Customer로 Cart 조회 실패 - 존재하지 않음")
//    void find_cart_by_customer_fail_not_found() {
//        // given
//        Customer customer = CustomerFixture.createCustomer();
//
//        // when
//        Optional<Cart> result = cartRepository.findByCustomer(customer);
//
//        // then
//        assertThat(result).isEmpty();
//    }
//}
