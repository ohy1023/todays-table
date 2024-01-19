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
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.domain.cart.repository.CartRepository;
import store.myproject.onlineshop.domain.cartitem.CartItem;
import store.myproject.onlineshop.domain.cartitem.repository.CartItemRepository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.DeliveryStatus;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.*;
import store.myproject.onlineshop.domain.order.repository.OrderRepository;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.domain.orderitem.repository.OrderItemRepository;
import store.myproject.onlineshop.exception.AppException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    @Value("${payment.rest.api.key}")
    private String apiKey;
    @Value("${payment.rest.api.secret}")
    private String apiSecret;

    private IamportClient iamportClient;

    @PostConstruct
    public void initializeIamportClient() {
        iamportClient = new IamportClient(apiKey, apiSecret);
    }

    @Transactional(readOnly = true)
    public OrderInfo findOrder(Long orderId, String email) {

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Order findOrder = orderRepository.findMyOrder(orderId, findCustomer)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));

        return findOrder.toOrderInfo();
    }

    @Transactional(readOnly = true)
    public Page<OrderInfo> searchMyOrders(OrderSearchCond orderSearchCond, String email, Pageable pageable) {

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        return orderRepository.search(orderSearchCond, findCustomer, pageable)
                .map(Order::toOrderInfo);

    }

    // 단건 주문
    public OrderInfo orderByOne(OrderInfoRequest request, String email) {

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        MemberShip memberShip = findCustomer.getMemberShip();

        Item findItem = itemRepository.findPessimisticLockById(request.getItemId())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());

        delivery.createDeliveryStatus(DeliveryStatus.READY);

        // 멤버쉽 할인
        BigDecimal price = memberShip.applyDiscount(findItem.getPrice());

        log.info("할인된 가격 : {}", price);

        // 주문 정보 생성
        OrderItem orderItem = OrderItem.createOrderItem(findCustomer, findItem, price, request.getItemCnt());

        // 주문 생성
        Order order = Order.createOrder(request.getMerchantUid(), findCustomer, delivery, orderItem);

        Order savedOrder = orderRepository.save(order);

        // 연관 관계 설정
        orderItem.setOrder(savedOrder);

        log.info("Total Price : {}", orderItem.getTotalPrice());

        return savedOrder.toOrderInfo();

    }


    // 주문 취소
    public MessageResponse cancelForOrder(Long orderItemId) throws IamportResponseException, IOException {

        OrderItem findOrderItem = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));

        Order order = findOrderItem.getOrder();

        order.cancel();

        BigDecimal refundAmount = findOrderItem.getTotalPrice();

        cancelReservation(new CancelRequest(order.getImpUid(), refundAmount));

        return new MessageResponse("주문이 취소되었습니다.");
    }

    // 장바구니 주문
    public List<OrderInfo> orderByCart(CartOrderRequest request, String email) {
        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Cart findCart = cartRepository.findByCustomer(findCustomer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND, CART_NOT_FOUND.getMessage()));

        // 장바구니에 품목 유무 & 체크 되어 있는지 체크
        validateCartItems(findCart.getCartItems());

        MemberShip memberShip = findCustomer.getMemberShip();

        // Delivery 정보 생성
        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());
        delivery.createDeliveryStatus(DeliveryStatus.READY);

        // 주문 정보 생성
        List<OrderItem> orderItemList = createOrderItems(findCart.getCartItems(), memberShip, findCustomer);

        Order order = Order.createOrders(request.getMerchantUid(), findCustomer, delivery, orderItemList);
        orderRepository.save(order);

        // 여러 개의 주문 정보를 담을 리스트 생성
        List<OrderInfo> orderInfoList = new ArrayList<>();

        for (OrderItem orderItem : orderItemList) {
            // 주문 후 장바구니 비우기
            clearCartItems(findCart, orderItem);

            // 주문 정보를 리스트에 추가
            orderInfoList.add(order.toOrderInfo());
        }

        // 여러 개의 주문 정보를 반환
        return orderInfoList;
    }

    // 사전 검증
    public PreparationResponse prepareValid(PreparationRequest request) throws IamportResponseException, IOException {
        PrepareData prepareData = new PrepareData(request.getMerchantUid(), request.getTotalPrice());
        IamportResponse<Prepare> iamportResponse = iamportClient.postPrepare(prepareData);

        log.info("결과 코드 : {}", iamportResponse.getCode());
        log.info("결과 메시지 : {}", iamportResponse.getMessage());

        if (iamportResponse.getCode() != 0) {
            throw new AppException(FAILED_PREPARE_VALID, iamportResponse.getMessage());
        }
        return PreparationResponse.builder().merchantUid(request.getMerchantUid()).build();
    }

    // 사후 검증
    public MessageResponse postVerification(PostVerificationRequest request) throws IamportResponseException, IOException {
        //DB에 merchant_uid가 중복되었는지 확인
        Order order = validOrder(request.getMerchantUid());

        //DB에 있는 금액과 사용자가 결제한 금액이 같은지 확인
        BigDecimal dbAmount = calcDbAmount(order.getOrderItemList()); // db에서 가져온 금액

        IamportResponse<Payment> iamResponse = iamportClient.paymentByImpUid(request.getImpUid());
        BigDecimal paidAmount = iamResponse.getResponse().getAmount(); // 사용자가 결제한 금액

        // 금액이 다르면 결제 취소
        if (paidAmount.compareTo(dbAmount) != 0) {
            IamportResponse<Payment> response = iamportClient.paymentByImpUid(request.getImpUid());
            CancelData cancelData = createCancelData(response, BigDecimal.ZERO);
            iamportClient.cancelPaymentByImpUid(cancelData);

            throw new AppException(WRONG_PAYMENT_AMOUNT, WRONG_PAYMENT_AMOUNT.getMessage());
        }

        order.setImpUid(request.getImpUid());

        return new MessageResponse("사후 검증 완료되었습니다.");
    }

    private Order validOrder(String merchantUid) {
        long count = orderRepository.countByMerchantUid(merchantUid);

        if (count >= 2) {
            // 두 개 이상 존재하는 경우에 대한 로직을 추가
            throw new AppException(DUPLICATE_MERCHANT_UID, DUPLICATE_MERCHANT_UID.getMessage());
        }

        return orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));
    }

    private BigDecimal calcDbAmount(List<OrderItem> orderItemList) {

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItemList) {
            totalPrice = totalPrice.add(orderItem.getTotalPrice()); // 값을 누적하기 위해 totalPrice를 업데이트
        }
        return totalPrice;
    }

    private MessageResponse cancelReservation(CancelRequest cancelReq) throws IamportResponseException, IOException {
        IamportResponse<Payment> response = iamportClient.paymentByImpUid(cancelReq.getImpUid());
        //cancelData 생성
        CancelData cancelData = createCancelData(response, cancelReq.getRefundAmount());
        //결제 취소
        iamportClient.cancelPaymentByImpUid(cancelData);

        return new MessageResponse("취소되었습니다.");
    }

    private CancelData createCancelData(IamportResponse<Payment> response, BigDecimal refundAmount) {
        if (refundAmount.compareTo(BigDecimal.ZERO) == 0) { //전액 환불일 경우
            return new CancelData(response.getResponse().getImpUid(), true);
        }
        //부분 환불일 경우 checksum을 입력해 준다.
        return new CancelData(response.getResponse().getImpUid(), true, refundAmount);
    }

    private void validateCartItems(List<CartItem> cartItems) {
        if (cartItems.isEmpty()) {
            throw new AppException(CART_ITEM_NOT_EXIST_IN_CART, CART_ITEM_NOT_EXIST_IN_CART.getMessage());
        }

        boolean checkedItemsExist = cartItems.stream()
                .anyMatch(CartItem::isChecked);

        if (!checkedItemsExist) {
            throw new AppException(CHECK_NOT_EXIST_IN_CART, CHECK_NOT_EXIST_IN_CART.getMessage());
        }
    }

    private List<OrderItem> createOrderItems(List<CartItem> cartItems, MemberShip memberShip, Customer customer) {
        List<OrderItem> orderItemList = new ArrayList<>();

        for (CartItem cartItem : cartItems) {
            if (cartItem.isChecked()) {
                Item findItem = itemRepository.findPessimisticLockById(cartItem.getItem().getId())
                        .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

                BigDecimal price = memberShip.applyDiscount(cartItem.getItem().getPrice());

                OrderItem orderItem = OrderItem.createOrderItem(customer, findItem, price, cartItem.getCartItemCnt());
                orderItemList.add(orderItem);

            }
        }

        return orderItemList;
    }

    private void clearCartItems(Cart cart, OrderItem orderItem) {
        cartItemRepository.deleteCartItem(cart, orderItem.getItem());
    }
}
