//package store.myproject.onlineshop.repository.order;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.test.context.ActiveProfiles;
//import store.myproject.onlineshop.domain.brand.Brand;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.domain.delivery.Delivery;
//import store.myproject.onlineshop.domain.item.Item;
//import store.myproject.onlineshop.domain.membership.MemberShip;
//import store.myproject.onlineshop.domain.order.Order;
//import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;
//import store.myproject.onlineshop.domain.orderitem.OrderItem;
//import store.myproject.onlineshop.fixture.*;
//import store.myproject.onlineshop.global.config.TestConfig;
//import store.myproject.onlineshop.repository.brand.BrandRepository;
//import store.myproject.onlineshop.repository.customer.CustomerRepository;
//import store.myproject.onlineshop.repository.delivery.DeliveryRepository;
//import store.myproject.onlineshop.repository.item.ItemRepository;
//import store.myproject.onlineshop.repository.membership.MemberShipRepository;
//import store.myproject.onlineshop.repository.orderitem.OrderItemRepository;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(TestConfig.class)
//@ActiveProfiles("test")
//class OrderRepositoryTest {
//
//    @Autowired
//    private OrderRepository orderRepository;
//    @Autowired
//    private CustomerRepository customerRepository;
//    @Autowired
//    private ItemRepository itemRepository;
//    @Autowired
//    private BrandRepository brandRepository;
//    @Autowired
//    private MemberShipRepository memberShipRepository;
//    @Autowired
//    private OrderItemRepository orderItemRepository;
//    @Autowired
//    private DeliveryRepository deliveryRepository;
//
//    @Test
//    @DisplayName("나의 주문 목록 조회 성공")
//    void find_my_orders_success() {
//        // given
//        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
//        Brand brand = brandRepository.save(BrandFixture.createBrand());
//        Item item = itemRepository.save(ItemFixture.createItem(brand));
//        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
//        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());
//
//        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());
//
//        OrderItem orderItem = OrderItem.createOrderItem(item, discountedPrice, 1L);
//
//        Order order = Order.createOrder(UUID.randomUUID(), customer, delivery, orderItem);
//        orderItem.setOrder(order);
//
//        order.completePayment("imp_uid");
//
//        orderItemRepository.save(orderItem);
//        orderRepository.save(order);
//
//        // 검색 조건 생성
//        OrderSearchCond condition = OrderSearchCond.builder()
//                .merchantUid(null)
//                .size(10)
//                .fromDate(null)
//                .toDate(null)
//                .brandName(null)
//                .itemName(null)
//                .build();
//
//        // when
//        List<Order> result = orderRepository.findMyOrders(condition, customer);
//
//        // then
//        assertThat(result).hasSize(1);
//    }
//
//    @Test
//    @DisplayName("자신의 주문을 조회")
//    void find_my_order_success() {
//        // given
//        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
//        Brand brand = brandRepository.save(BrandFixture.createBrand());
//        Item item = itemRepository.save(ItemFixture.createItem(brand));
//        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
//        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());
//
//        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());
//
//        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(item, discountedPrice, 1L));
//        Order order = orderRepository.save(Order.createOrder(UUID.randomUUID(), customer, delivery, orderItem));
//        orderItem.setOrder(order);
//        order.completePayment("imp_uid");
//
//        // when
//        Optional<Order> result = orderRepository.findMyOrder(order.getMerchantUid(), customer);
//
//        // then
//        assertThat(result).isPresent();
//        assertThat(result.get().getId()).isEqualTo(order.getId());
//    }
//
//
//    @Test
//    @DisplayName("주문 조회")
//    void find_by_merchant_uid_success() {
//        // given
//        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
//        Brand brand = brandRepository.save(BrandFixture.createBrand());
//        Item item = itemRepository.save(ItemFixture.createItem(brand));
//        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
//        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());
//
//        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());
//
//        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(item, discountedPrice, 1L));
//        Order order = orderRepository.save(Order.createOrder(UUID.randomUUID(), customer, delivery, orderItem));
//        orderItem.setOrder(order);
//
//        // when
//        Optional<Order> result = orderRepository.findByMerchantUid(order.getMerchantUid());
//
//        // then
//        assertThat(result).isPresent();
//        assertThat(result.get().getMerchantUid()).isEqualTo(order.getMerchantUid());
//    }
//
//    @Test
//    @DisplayName("존재하지 않는 주문 조회 시 빈 값")
//    void find_by_merchant_uid_no_result() {
//        // when
//        Optional<Order> result = orderRepository.findByMerchantUid(UUID.randomUUID());
//
//        // then
//        assertThat(result).isEmpty();
//    }
//}
