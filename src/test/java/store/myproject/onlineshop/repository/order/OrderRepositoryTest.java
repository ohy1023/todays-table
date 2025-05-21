package store.myproject.onlineshop.repository.order;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.OrderStatus;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.fixture.*;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.delivery.DeliveryRepository;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.repository.membership.MemberShipRepository;
import store.myproject.onlineshop.repository.orderitem.OrderItemRepository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private MemberShipRepository memberShipRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private DeliveryRepository deliveryRepository;

    @Test
    @DisplayName("itemName 없이 검색 성공")
    void search_without_item_name_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // 검색 조건 생성
        OrderSearchCond condition = OrderSearchCond.builder()
                .brandName(brand.getName())
                .orderStatus(OrderStatus.READY)
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.search(condition, customer, pageRequest);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("customer 없이 검색 성공")
    void search_success_without_customer_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // 검색 조건 생성
        OrderSearchCond condition = OrderSearchCond.builder()
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.search(condition, null, pageRequest);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("itemName 조건이 있는 경우 검색 성공")
    void search_with_item_name_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // 검색 조건 (itemName만 설정)
        OrderSearchCond condition = OrderSearchCond.builder()
                .itemName(item.getItemName())
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.search(condition, customer, pageRequest);

        // then
        assertThat(result).hasSize(1);
    }


    @Test
    @DisplayName("brandName 없이 검색 성공")
    void search_without_brand_name_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // 검색 조건 (brandName만 null)
        OrderSearchCond condition = OrderSearchCond.builder()
                .itemName(item.getItemName())
                .orderStatus(OrderStatus.READY)
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.search(condition, customer, pageRequest);

        // then
        assertThat(result).hasSize(1);
    }


    @Test
    @DisplayName("orderStatus 없이 검색 성공")
    void search_without_order_status_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // 검색 조건 (orderStatus만 null)
        OrderSearchCond condition = OrderSearchCond.builder()
                .itemName(item.getItemName())
                .brandName(brand.getName())
                .build();

        PageRequest pageRequest = PageRequest.of(0, 10);

        // when
        Page<Order> result = orderRepository.search(condition, customer, pageRequest);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("자신의 주문을 조회")
    void find_my_order_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // when
        Optional<Order> result = orderRepository.findMyOrder(order.getMerchantUid(), customer);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(order.getId());
    }


    @Test
    @DisplayName("주문 조회")
    void find_by_merchant_uid_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // when
        Optional<Order> result = orderRepository.findByMerchantUid(order.getMerchantUid());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getMerchantUid()).isEqualTo(order.getMerchantUid());
    }

    @Test
    @DisplayName("존재하지 않는 주문 조회 시 빈 값")
    void find_by_merchant_uid_no_result() {
        // when
        Optional<Order> result = orderRepository.findByMerchantUid(UUID.randomUUID());

        // then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("주문 수 확인")
    void count_by_merchant_uid_success() {
        // given
        Customer customer = customerRepository.save(CustomerFixture.createCustomer());
        Brand brand = brandRepository.save(BrandFixture.createBrand());
        Item item = itemRepository.save(ItemFixture.createItem(brand));
        MemberShip memberShip = memberShipRepository.save(MemberShipFixture.createBronzeMembership());
        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

        Delivery delivery = deliveryRepository.save(DeliveryFixture.createDelivery());

        OrderItem orderItem = orderItemRepository.save(OrderItem.createOrderItem(customer, item, discountedPrice, 1L));
        Order order = orderRepository.save(Order.createOrder(customer, delivery, orderItem));
        orderItem.setOrder(order);

        // when
        Long count = orderRepository.countByMerchantUid(order.getMerchantUid());

        // then
        assertThat(count).isEqualTo(1);
    }

    @Test
    @DisplayName("존재하지 않는 MerchantUid에 대해 count는 0을 반환")
    void count_by_merchant_uid_zero() {
        // when
        Long count = orderRepository.countByMerchantUid(UUID.randomUUID());

        // then
        assertThat(count).isZero();
    }
}
