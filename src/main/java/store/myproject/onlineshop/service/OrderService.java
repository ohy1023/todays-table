package store.myproject.onlineshop.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.repository.cart.CartRepository;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.repository.cartitem.CartItemRepository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.DeliveryStatus;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.*;
import store.myproject.onlineshop.repository.order.OrderRepository;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.repository.orderitem.OrderItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static store.myproject.onlineshop.domain.MessageCode.ORDER_POST_VERIFICATION;
import static store.myproject.onlineshop.domain.order.OrderStatus.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;
    private final MessageUtil messageUtil;
    private final AsyncCustomerService asyncCustomerService;

    @Value("${payment.rest.api.key}")
    private String apiKey;

    @Value("${payment.rest.api.secret}")
    private String apiSecret;

    private IamportClient iamportClient;

    @PostConstruct
    public void initializeIamportClient() {
        iamportClient = new IamportClient(apiKey, apiSecret);
    }

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public OrderInfo getOrderByUuid(UUID merchantUid, String email) {
        Customer customer = getCustomerByEmail(email);
        Order order = orderRepository.findMyOrder(merchantUid, customer)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));
        return order.toOrderInfo();
    }

    // 주문 목록 조회
    @Transactional(readOnly = true)
    public Page<OrderInfo> getMyOrders(OrderSearchCond searchCond, String email, Pageable pageable) {
        Customer customer = getCustomerByEmail(email);
        return orderRepository.search(searchCond, customer, pageable).map(Order::toOrderInfo);
    }

    // 단건 주문
    public OrderInfo placeSingleOrder(OrderInfoRequest request, String email) {
        Customer customer = getCustomerByEmail(email);
        MemberShip memberShip = customer.getMemberShip();
        Long itemId = itemRepository.findIdByUuid(request.getItemUuid())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
        Item item = itemRepository.findPessimisticLockById(itemId)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());
        log.info("할인된 가격 : {}", discountedPrice);

        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());
        delivery.createDeliveryStatus(DeliveryStatus.READY);

        OrderItem orderItem = OrderItem.createOrderItem(item, discountedPrice, request.getItemCnt());
        Order order = Order.createOrder(customer, delivery, orderItem);
        orderItem.setOrder(orderRepository.save(order));

        asyncCustomerService.addMonthlyPurchaseAmount(customer.getId(), discountedPrice);

        log.info("Total Price : {}", orderItem.getTotalPrice());
        return order.toOrderInfo();
    }

    // 배송지 수정
    public MessageResponse updateDeliveryAddress(UUID merchantUid, DeliveryUpdateRequest request) {

        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));

        Delivery delivery = order.getDelivery();

        if (delivery.getStatus() != DeliveryStatus.READY) {
            throw new AppException(DELIVERY_MODIFIED_FAIL);
        }

        order.getDelivery().setInfo(request);

        return new MessageResponse(order.getMerchantUid(), messageUtil.get(MessageCode.ORDER_DELIVERY_MODIFIED));
    }

    // 주문 취소
    public MessageResponse cancelOrder(UUID merchantUid, CancelItemRequest request) throws IamportResponseException, IOException {
        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));

        Item item = itemRepository.findByUuid(request.getItemUuid())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

        OrderItem orderItem = orderItemRepository.findByOrderAndItem(order, item)
                .orElseThrow(() -> new AppException(ORDER_ITEM_NOT_FOUND));

        order.cancel();
        cancelReservation(new CancelRequest(order.getImpUid(), orderItem.getTotalPrice()));

        return new MessageResponse(order.getMerchantUid(), messageUtil.get(MessageCode.ORDER_CANCEL));
    }

    // 장바구니 주문
    public List<OrderInfo> placeCartOrder(CartOrderRequest request, String email) {
        Customer customer = getCustomerByEmail(email);
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND));

        validateCartItems(cart.getCartItems());

        MemberShip memberShip = customer.getMemberShip();
        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());
        delivery.createDeliveryStatus(DeliveryStatus.READY);

        List<OrderItem> orderItems = createOrderItemsFromCart(cart.getCartItems(), memberShip);
        Order order = Order.createOrders(customer, delivery, orderItems);
        orderRepository.save(order);

        orderItems.forEach(orderItem -> {
            clearCartItem(cart, orderItem);
        });

        asyncCustomerService.addMonthlyPurchaseAmount(customer.getId(), order.getTotalPrice());

        return orderItems.stream().map(item -> order.toOrderInfo()).toList();
    }

    // 결제 사전 검증
    public PreparationResponse validatePrePayment(PreparationRequest request) throws IamportResponseException, IOException {
        PrepareData prepareData = new PrepareData(request.getMerchantUid(), request.getTotalPrice());
        IamportResponse<Prepare> response = iamportClient.postPrepare(prepareData);

        if (response.getCode() != 0) {
            throw new AppException(FAILED_PREPARE_VALID, response.getMessage());
        }
        return PreparationResponse.builder().merchantUid(request.getMerchantUid()).build();
    }

    // 결제 사후 검증
    public MessageResponse verifyPostPayment(PostVerificationRequest request) throws IamportResponseException, IOException {

        Payment payment = getPayment(request.getImpUid());

        // 주문 정보 검증 (merchant_uid로 조회)
        Order order = validateOrderByMerchantUid(request.getMerchantUid());

        // 주문 금액과 결제 금액 비교
        BigDecimal expectedAmount = calculateTotalAmount(order.getOrderItemList());
        BigDecimal actualAmount = payment.getAmount();

        // 금액 불일치 시 결제 취소 및 예외 발생
        if (expectedAmount.compareTo(actualAmount) != 0) {
            CancelData cancelData = createCancelData(payment, BigDecimal.ZERO);
            iamportClient.cancelPaymentByImpUid(cancelData);
            order.updateOrderStatus(CANCEL);
            throw new AppException(WRONG_PAYMENT_AMOUNT);
        }

        // 결제 정보가 일치하면 주문에 imp_uid 설정 및 주문 상태 변경
        order.setImpUid(request.getImpUid());
        order.updateOrderStatus(ORDER);

        // 성공 메시지 응답
        return new MessageResponse(messageUtil.get(ORDER_POST_VERIFICATION));
    }


    // === private utils ===

    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));
    }

    private Order validateOrderByMerchantUid(UUID merchantUid) {
        long count = orderRepository.countByMerchantUid(merchantUid);
        if (count >= 2) {
            throw new AppException(DUPLICATE_MERCHANT_UID);
        }
        return orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));
    }

    private Payment getPayment(String impUid) throws IOException {
        IamportResponse<Payment> paymentIamportResponse = null;
        try {
            // 포트원 서버에서 imp_uid로 결제 정보 조회
            paymentIamportResponse = iamportClient.paymentByImpUid(impUid);
        } catch (IamportResponseException e) {
            // 포트원 서버에서 imp_uid로 결제 정보 조회 실패 시 커스텀 예외 던지기
            throw new AppException(PAYMENT_NOT_FOUND);
        }

        return paymentIamportResponse.getResponse();
    }

    private BigDecimal calculateTotalAmount(List<OrderItem> items) {
        return items.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void cancelReservation(CancelRequest request) throws IamportResponseException, IOException {
        Payment payment = getPayment(request.getImpUid());
        CancelData cancelData = createCancelData(payment, request.getRefundAmount());
        iamportClient.cancelPaymentByImpUid(cancelData);
    }

    private CancelData createCancelData(Payment payment, BigDecimal refundAmount) {
        if (refundAmount.compareTo(BigDecimal.ZERO) == 0) {
            return new CancelData(payment.getImpUid(), true);
        }
        return new CancelData(payment.getImpUid(), true, refundAmount);
    }

    private void validateCartItems(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new AppException(CART_ITEM_NOT_EXIST_IN_CART);
        }
        if (cartItems.stream().noneMatch(CartItem::isChecked)) {
            throw new AppException(CHECK_NOT_EXIST_IN_CART);
        }
    }

    private List<OrderItem> createOrderItemsFromCart(List<CartItem> cartItems, MemberShip memberShip) {
        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            if (cartItem.isChecked()) {
                Long itemId = itemRepository.findIdByUuid(cartItem.getItem().getUuid())
                        .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
                Item item = itemRepository.findPessimisticLockById(itemId)
                        .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
                BigDecimal discountedPrice = memberShip.applyDiscount(cartItem.getItem().getPrice());
                orderItemList.add(OrderItem.createOrderItem(item, discountedPrice, cartItem.getCartItemCnt()));
            }
        }
        return orderItemList;
    }

    private void clearCartItem(Cart cart, OrderItem orderItem) {
        cartItemRepository.deleteCartItem(cart, orderItem.getItem());
    }
}
