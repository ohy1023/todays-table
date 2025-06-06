package store.myproject.onlineshop.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
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
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.DeliveryStatus;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.OrderStatus;
import store.myproject.onlineshop.domain.order.dto.*;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.*;
import store.myproject.onlineshop.global.utils.MessageUtil;
import store.myproject.onlineshop.repository.cart.CartRepository;
import store.myproject.onlineshop.repository.cartitem.CartItemRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.repository.order.OrderRepository;
import store.myproject.onlineshop.repository.orderitem.OrderItemRepository;

import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private MessageUtil messageUtil;

    @Mock
    private IamportClient iamportClient;

    @Mock
    private AsyncCustomerService asyncCustomerService;

    @BeforeEach
    void setUp() throws Exception {
        Field field = OrderService.class.getDeclaredField("iamportClient");
        field.setAccessible(true);
        field.set(orderService, iamportClient);
    }


    @Test
    @DisplayName("UUID 기반 Order 조회 성공")
    void get_order_by_uuid_success() {
        // given
        Customer customer = CustomerFixture.createCustomerEntity();
        Delivery delivery = DeliveryFixture.createDelivery();
        Brand brand = BrandFixture.createBrandEntity();
        Item item = ItemFixture.createItemEntity(brand);
        BigDecimal discountedPrice = customer.getMemberShip().applyDiscount(item.getPrice());
        OrderItem orderItem = OrderItem.createOrderItem(item, discountedPrice, 1L);
        Order order = Order.createOrder(customer, delivery, orderItem);

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(orderRepository.findMyOrder(order.getMerchantUid(), customer)).willReturn(Optional.of(order));


        // when
        OrderInfo result = orderService.getOrderByUuid(order.getMerchantUid(), customer.getEmail());

        // then
        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("UUID 기반 Order 조회 실패 - 주문 없음")
    void get_order_by_uuid_fail_not_found_customer() {
        // given
        UUID wrongUuid = UUID.randomUUID();

        Customer customer = CustomerFixture.createCustomerEntity();

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrderByUuid(wrongUuid, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CUSTOMER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("UUID 기반 Order 조회 실패 - 주문 없음")
    void get_order_by_uuid_fail_not_found_order() {
        // given
        UUID merchantUid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Customer customer = CustomerFixture.createCustomerEntity();

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(orderRepository.findMyOrder(merchantUid, customer)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.getOrderByUuid(merchantUid, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    void get_my_orders_success() {
        // given
        Customer customer = CustomerFixture.createCustomerEntity();
        Delivery delivery = DeliveryFixture.createDelivery();
        Brand brand = BrandFixture.createBrandEntity();
        Item item = ItemFixture.createItemEntity(brand);
        OrderSearchCond searchCond = new OrderSearchCond(); // 검색 조건이 비어 있을 경우 기본 테스트
        Pageable pageable = PageRequest.of(0, 10);

        BigDecimal discountedPrice = customer.getMemberShip().applyDiscount(item.getPrice());
        OrderItem orderItem = OrderItem.createOrderItem(item, discountedPrice, 1L);
        Order order = Order.createOrder(customer, delivery, orderItem);

        Page<Order> orderPage = new PageImpl<>(List.of(order));

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(orderRepository.search(searchCond, customer, pageable)).willReturn(orderPage);

        // when
        Page<OrderInfo> result = orderService.getMyOrders(searchCond, customer.getEmail(), pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    @DisplayName("단건 주문 성공")
    void place_single_order_success() {
        // given
        String email = "test@example.com";
        UUID brandUuid = UUID.randomUUID();
        UUID itemUuid = UUID.randomUUID();
        Long itemId = 1L;
        Long itemCnt = 2L;

        MemberShip BRONZE = MemberShip.builder()
                .level(Level.BRONZE)
                .discountRate(BigDecimal.valueOf(0.1)) // 10% 할인
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .monthlyPurchaseAmount(BigDecimal.ZERO)
                .memberShip(BRONZE)
                .build();

        Brand brand = Brand.builder()
                .uuid(brandUuid)
                .name("브랜드명")
                .build();

        Item item = Item.builder()
                .uuid(itemUuid)
                .itemName("상품명")
                .stock(100L)
                .price(BigDecimal.valueOf(10000))
                .brand(brand)
                .build();

        OrderInfoRequest request = OrderInfoRequest.builder()
                .itemUuid(itemUuid)
                .itemCnt(itemCnt)
                .recipientName("홍길동")
                .recipientTel("010-1234-5678")
                .recipientCity("서울시")
                .recipientStreet("강남대로")
                .recipientDetail("123호")
                .recipientZipcode("06236")
                .build();

        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(itemRepository.findIdByUuid(itemUuid)).willReturn(Optional.of(itemId));
        given(itemRepository.findPessimisticLockById(itemId)).willReturn(Optional.of(item));
        given(orderRepository.save(any(Order.class)))
                .willAnswer(invocation -> invocation.getArgument(0)); // 저장된 Order 그대로 리턴


        BigDecimal totalPrice = item.getPrice()
                .multiply(BigDecimal.valueOf(itemCnt))
                .multiply(BigDecimal.ONE.subtract(BRONZE.getDiscountRate()));


        // when
        OrderInfo result = orderService.placeSingleOrder(request, email);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getItemName()).isEqualTo(item.getItemName());
        assertThat(result.getBrandName()).isEqualTo(item.getBrand().getName());
        assertThat(result.getTotalPrice()).isEqualTo(totalPrice);

    }

    @Test
    @DisplayName("단건 주문 실패 - 고객 없음")
    void place_single_order_fail_customer_not_found() {
        // given
        String email = "notfound@example.com";
        UUID itemUuid = UUID.randomUUID();
        OrderInfoRequest request = OrderInfoRequest.builder()
                .itemUuid(itemUuid)
                .itemCnt(1L)
                .recipientName("홍길동")
                .recipientTel("010-1234-5678")
                .recipientCity("서울시")
                .recipientStreet("강남대로")
                .recipientDetail("123호")
                .recipientZipcode("06236")
                .build();

        given(customerRepository.findByEmail(email)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.placeSingleOrder(request, email))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.CUSTOMER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("단건 주문 실패 - 상품 없음")
    void place_single_order_fail_item_not_found() {
        // given
        String email = "test@example.com";
        UUID itemUuid = UUID.randomUUID();
        OrderInfoRequest request = OrderInfoRequest.builder()
                .itemUuid(itemUuid)
                .itemCnt(1L)
                .recipientName("홍길동")
                .recipientTel("010-1234-5678")
                .recipientCity("서울시")
                .recipientStreet("강남대로")
                .recipientDetail("123호")
                .recipientZipcode("06236")
                .build();

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .monthlyPurchaseAmount(BigDecimal.ZERO)
                .memberShip(MemberShip.builder()
                        .level(Level.BRONZE)
                        .discountRate(BigDecimal.valueOf(0.1))
                        .build())
                .build();

        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(itemRepository.findIdByUuid(itemUuid)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.placeSingleOrder(request, email))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("배송지 변경 성공")
    void update_delivery_info_success() {
        // given
        String email = "test@example.com";

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .memberShip(MemberShip.builder()
                        .level(Level.BRONZE)
                        .discountRate(BigDecimal.ZERO)
                        .build())
                .build();

        Delivery oldDelivery = Delivery.builder()
                .recipientName("기존이름")
                .recipientTel("010-0000-0000")
                .address(
                        Address.builder()
                                .city("서울시")
                                .street("서초대로")
                                .detail("101호")
                                .zipcode("12345")
                                .build()
                )
                .status(DeliveryStatus.READY)
                .build();

        Order order = Order.builder()
                .id(1L)
                .merchantUid(UUID.randomUUID())
                .customer(customer)
                .delivery(oldDelivery)
                .orderStatus(OrderStatus.ORDER)
                .build();

        DeliveryUpdateRequest request = new DeliveryUpdateRequest(
                "새이름", "010-1111-1111", "부산시", "해운대로", "202호", "54321"
        );

        given(orderRepository.findByMerchantUid(order.getMerchantUid())).willReturn(Optional.of(order));
        given(messageUtil.get(MessageCode.ORDER_DELIVERY_MODIFIED)).willReturn("배송지 변경 완료");

        // when
        MessageResponse result = orderService.updateDeliveryAddress(order.getMerchantUid(), request);

        // then
        assertThat(result.getMessage()).isEqualTo("배송지 변경 완료");
        assertThat(order.getDelivery().getRecipientName()).isEqualTo("새이름");
        assertThat(order.getDelivery().getRecipientTel()).isEqualTo("010-1111-1111");
    }

    @Test
    @DisplayName("배송지 변경 실패 - 주문 없음")
    void update_delivery_info_fail_order_not_found() {
        // given
        String email = "test@example.com";
        UUID merchantUid = UUID.randomUUID();

        Customer customer = Customer.builder().email(email).build();

        DeliveryUpdateRequest request = new DeliveryUpdateRequest(
                "이름", "010-0000-0000", "도시", "도로", "상세", "우편번호"
        );

        given(orderRepository.findByMerchantUid(merchantUid)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.updateDeliveryAddress(merchantUid, request))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
    }


    @Test
    @DisplayName("주문 취소 성공")
    void cancel_order_success() throws Exception {
        // given
        String impUid = "imp_123456789";
        BigDecimal price = BigDecimal.valueOf(20000);

        Customer customer = CustomerFixture.createCustomerEntity();
        Brand brand = BrandFixture.createBrandEntity();
        Item item = ItemFixture.createItemEntity(brand);
        OrderItem orderItem = OrderItem.createOrderItem(item, price, 2L);
        Order order = Order.createOrder(customer, DeliveryFixture.createDelivery(), orderItem);
        order.setImpUid(impUid);

        CancelItemRequest request = OrderFixture.createCancelItemRequest(item.getUuid());

        given(orderRepository.findByMerchantUid(order.getMerchantUid())).willReturn(Optional.of(order));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.of(item));
        given(orderItemRepository.findByOrderAndItem(order, item)).willReturn(Optional.of(orderItem));
        given(messageUtil.get(MessageCode.ORDER_CANCEL)).willReturn("주문 취소");

        // 실제 Payment 객체 생성
        Payment payment = new Payment();
        Field impUidField = Payment.class.getDeclaredField("imp_uid");
        impUidField.setAccessible(true);
        impUidField.set(payment, impUid);

        // IamportResponse 리플렉션으로 주입
        IamportResponse<Payment> response = new IamportResponse<>();
        Field responseField = IamportResponse.class.getDeclaredField("response");
        responseField.setAccessible(true);
        responseField.set(response, payment);

        given(iamportClient.paymentByImpUid(impUid)).willReturn(response);

        // when
        MessageResponse result = orderService.cancelOrder(order.getMerchantUid(), request);

        // then
        assertThat(order.getOrderStatus()).isEqualTo(OrderStatus.CANCEL);
        assertThat(result.getMessage()).isEqualTo("주문 취소");
    }

    @Test
    @DisplayName("주문 취소 실패 - 존재하지 않는 주문 아이템 ID")
    void cancel_order_fail_order_item_not_found() {
        // given
        String impUid = "imp_123456789";
        BigDecimal price = BigDecimal.valueOf(20000);

        Customer customer = CustomerFixture.createCustomerEntity();
        Brand brand = BrandFixture.createBrandEntity();
        Item item = ItemFixture.createItemEntity(brand);
        OrderItem orderItem = OrderItem.createOrderItem(item, price, 2L);
        Order order = Order.createOrder(customer, DeliveryFixture.createDelivery(), orderItem);
        order.setImpUid(impUid);

        CancelItemRequest request = OrderFixture.createCancelItemRequest(item.getUuid());

        given(orderRepository.findByMerchantUid(order.getMerchantUid())).willReturn(Optional.of(order));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.of(item));
        given(orderItemRepository.findByOrderAndItem(order, item)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(order.getMerchantUid(), request))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.ORDER_ITEM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("주문 취소 실패 - Iamport 통신 예외 발생")
    void cancel_order_fail_iamport_exception() throws Exception {
        // given
        String impUid = "imp_123456789";
        BigDecimal price = BigDecimal.valueOf(20000);

        Customer customer = CustomerFixture.createCustomerEntity();
        Brand brand = BrandFixture.createBrandEntity();
        Item item = ItemFixture.createItemEntity(brand);
        OrderItem orderItem = OrderItem.createOrderItem(item, price, 2L);
        Order order = Order.createOrder(customer, DeliveryFixture.createDelivery(), orderItem);
        order.setImpUid(impUid);

        CancelItemRequest request = OrderFixture.createCancelItemRequest(item.getUuid());

        given(orderRepository.findByMerchantUid(order.getMerchantUid())).willReturn(Optional.of(order));
        given(itemRepository.findByUuid(item.getUuid())).willReturn(Optional.of(item));
        given(orderItemRepository.findByOrderAndItem(order, item)).willReturn(Optional.of(orderItem));
        given(iamportClient.paymentByImpUid(impUid)).willThrow(new java.io.IOException("통신 오류"));

        // when & then
        assertThatThrownBy(() -> orderService.cancelOrder(order.getMerchantUid(), request))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("통신 오류");
    }


    @Test
    @DisplayName("결제 사전 검증 성공")
    void validate_pre_payment_success() throws Exception {
        // given
        String merchantUid = "merchant-123";
        BigDecimal totalPrice = BigDecimal.valueOf(50000);

        PreparationRequest request = new PreparationRequest(merchantUid, totalPrice);
        Prepare mockPrepare = new Prepare(); // 성공 응답 객체 (내용 필요 시 필드 설정 가능)
        IamportResponse<Prepare> iamportResponse = new IamportResponse<>();

        // 리플렉션을 통해 내부 필드 주입
        Field field = IamportResponse.class.getDeclaredField("response");
        field.setAccessible(true);
        field.set(iamportResponse, mockPrepare);

        // 정상 응답 설정
        Field codeField = IamportResponse.class.getDeclaredField("code");
        codeField.setAccessible(true);
        codeField.set(iamportResponse, 0);

        given(iamportClient.postPrepare(any(PrepareData.class)))
                .willReturn(iamportResponse);

        // when
        PreparationResponse result = orderService.validatePrePayment(request);

        // then
        assertThat(result.getMerchantUid()).isEqualTo(merchantUid);
    }

    @Test
    @DisplayName("결제 사전 검증 실패 - Iamport 오류")
    void validate_pre_payment_fail_iamport_error() throws Exception {
        // given
        String merchantUid = "merchant-123";
        BigDecimal totalPrice = BigDecimal.valueOf(50000);

        PreparationRequest request = new PreparationRequest(merchantUid, totalPrice);

        IamportResponse<Prepare> iamportResponse = new IamportResponse<>();

        // 오류 코드, 메시지 설정
        Field codeField = IamportResponse.class.getDeclaredField("code");
        codeField.setAccessible(true);
        codeField.set(iamportResponse, 1);

        Field messageField = IamportResponse.class.getDeclaredField("message");
        messageField.setAccessible(true);
        messageField.set(iamportResponse, "Invalid authentication");

        given(iamportClient.postPrepare(any(PrepareData.class)))
                .willReturn(iamportResponse);

        // when & then
        assertThatThrownBy(() -> orderService.validatePrePayment(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining("Invalid authentication");
    }

    @Test
    @DisplayName("결제 사후 검증 성공")
    void verify_post_payment_success() throws Exception {
        // given
        UUID merchantUid = UUID.randomUUID();
        String impUid = "imp_123456789";
        String email = "test@example.com";
        Long brandId = 1L;
        Long itemId = 1L;
        BigDecimal price = BigDecimal.valueOf(20000);

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .memberShip(MemberShip.builder()
                        .level(Level.BRONZE)
                        .discountRate(BigDecimal.ZERO)
                        .build())
                .monthlyPurchaseAmount(BigDecimal.ZERO)
                .build();

        Brand brand = Brand.builder()
                .id(brandId)
                .name("브랜드명")
                .build();

        Item item = Item.builder()
                .id(itemId)
                .itemName("상품명")
                .stock(100L)
                .price(BigDecimal.valueOf(10000))
                .brand(brand)
                .build();

        OrderItem orderItem = OrderItem.createOrderItem(item, price, 2L);
        Order order = Order.createOrder(customer, DeliveryFixture.createDelivery(), orderItem);

        // 실제 결제된 금액 = 기대 금액
        Payment payment = new Payment();
        Field amountField = Payment.class.getDeclaredField("amount");
        amountField.setAccessible(true);
        amountField.set(payment, orderItem.getTotalPrice());

        Field impUidField = Payment.class.getDeclaredField("imp_uid");
        impUidField.setAccessible(true);
        impUidField.set(payment, impUid);

        IamportResponse<Payment> iamportResponse = new IamportResponse<>();
        Field responseField = IamportResponse.class.getDeclaredField("response");
        responseField.setAccessible(true);
        responseField.set(iamportResponse, payment);

        given(orderRepository.countByMerchantUid(merchantUid)).willReturn(1L);
        given(orderRepository.findByMerchantUid(merchantUid)).willReturn(Optional.of(order));
        given(iamportClient.paymentByImpUid(impUid)).willReturn(iamportResponse);
        given(messageUtil.get(MessageCode.ORDER_POST_VERIFICATION)).willReturn("결제 검증 완료");

        PostVerificationRequest request = new PostVerificationRequest(merchantUid, impUid);

        // when
        MessageResponse result = orderService.verifyPostPayment(request);

        // then
        Assertions.assertThat(result.getMessage()).isEqualTo("결제 검증 완료");
        Assertions.assertThat(order.getImpUid()).isEqualTo(impUid);
    }


    @Test
    @DisplayName("장바구니에 담긴 상품으로 주문 생성 성공")
    void order_from_cart_success() {
        // given
        String email = "test@example.com";
        Long orderId = 1L;
        Long brandId = 1L;

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .memberShip(MemberShip.builder()
                        .level(Level.BRONZE)
                        .baseline(BigDecimal.ZERO)
                        .discountRate(BigDecimal.ZERO)
                        .build())
                .monthlyPurchaseAmount(BigDecimal.ZERO)
                .build();

        Brand brand = Brand.builder()
                .id(brandId)
                .name("브랜드명")
                .build();

        Item item1 = Item.builder()
                .id(1L)
                .itemName("사과")
                .stock(100L)
                .price(BigDecimal.valueOf(10000))
                .brand(brand)
                .build();

        Item item2 = Item.builder()
                .id(2L)
                .itemName("바나나")
                .price(BigDecimal.valueOf(2000))
                .stock(5L)
                .brand(brand)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .customer(customer)
                .build();

        CartItem cartItem1 = CartItem.createCartItem(item1, 2L, cart);
        CartItem cartItem2 = CartItem.createCartItem(item2, 1L, cart);

        cart.addCartItem(cartItem1);
        cart.addCartItem(cartItem2);

        Delivery delivery = Delivery.builder()
                .recipientName("홍길동")
                .recipientTel("010-1234-5678")
                .address(
                        Address.builder()
                                .city("서울시")
                                .street("송파구 가락동")
                                .detail("101동 202호")
                                .zipcode("12345")
                                .build()
                )
                .build();

        Order savedOrder = Order.builder()
                .id(orderId)
                .customer(customer)
                .delivery(delivery)
                .orderStatus(OrderStatus.ORDER)
                .build();

        given(customerRepository.findByEmail(email)).willReturn(Optional.of(customer));
        given(itemRepository.findIdByUuid(cartItem1.getItem().getUuid())).willReturn(Optional.of(item1.getId()));
        given(itemRepository.findPessimisticLockById(item1.getId())).willReturn(Optional.of(item1));
        given(cartRepository.findByCustomer(customer)).willReturn(Optional.of(cart));
        given(orderRepository.save(any(Order.class))).willReturn(savedOrder);
        doNothing().when(cartItemRepository).deleteCartItem(any(Cart.class), any(Item.class));

        CartOrderRequest request = CartOrderRequest.builder()
                .recipientName("홍길동")
                .recipientTel("010-1234-5678")
                .recipientCity("서울시")
                .recipientStreet("송파구 가락동")
                .recipientDetail("101동 202호")
                .recipientZipcode("12345")
                .build();

        // when
        List<OrderInfo> result = orderService.placeCartOrder(request, email);

        // then
        assertThat(result).hasSize(2);
    }


}
