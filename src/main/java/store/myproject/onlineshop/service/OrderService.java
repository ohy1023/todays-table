package store.myproject.onlineshop.service;

import com.siot.IamportRestClient.IamportClient;
import com.siot.IamportRestClient.exception.IamportResponseException;
import com.siot.IamportRestClient.request.CancelData;
import com.siot.IamportRestClient.request.PrepareData;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import com.siot.IamportRestClient.response.Prepare;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.global.utils.UUIDGenerator;
import store.myproject.onlineshop.repository.cart.CartRepository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.repository.cartitem.CartItemRepository;
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
    private final IamportClient iamportClient;

    // 주문 단건 조회
    @Transactional(readOnly = true)
    public OrderInfo getOrderByUuid(UUID merchantUid, String email) {
        Customer customer = getCustomerByEmail(email);
        Order order = orderRepository.findMyOrder(merchantUid, customer)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));
        return order.toOrderInfo();
    }

    // 나의 주문 목록 조회
    @Transactional(readOnly = true)
    public MyOrderSliceResponse getMyOrders(OrderSearchCond cond, String email) {
        Customer customer = getCustomerByEmail(email);

        List<Order> myOrders = orderRepository.findMyOrders(cond, customer);
        boolean hasNext = myOrders.size() > cond.getSize();

        List<Order> content = hasNext
                ? myOrders.subList(0, cond.getSize())
                : myOrders;

        List<MyOrderResponse> responseList = content.stream()
                .map(Order::toMyOrderResponse)
                .toList();

        UUID nextCursor = hasNext
                ? content.get(content.size() - 1).getMerchantUid()
                : null;

        return MyOrderSliceResponse.builder()
                .content(responseList)
                .nextCursor(nextCursor)
                .build();
    }

    // 단건 주문
    public MessageResponse placeSingleOrder(OrderInfoRequest request, String email) {
        Customer customer = getCustomerByEmail(email);
        MemberShip memberShip = customer.getMemberShip();
        Long itemId = itemRepository.findIdByUuid(request.getItemUuid())
                .orElseThrow(() -> new AppException(ITEM_ID_NOT_FOUND));
        Item item = itemRepository.findPessimisticLockById(itemId)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

        BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());
        log.info("할인된 가격 : {}", discountedPrice);

        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());
        delivery.createDeliveryStatus(DeliveryStatus.READY);

        OrderItem orderItem = OrderItem.createOrderItem(item, discountedPrice, request.getItemCnt());
        Order order = Order.createOrder(request.getMerchantUid(), customer, delivery, orderItem);
        orderItem.setOrder(order);

        log.info("Total Price : {}", orderItem.getTotalPrice());

        Order savedOrder = orderRepository.save(order);
        return new MessageResponse(savedOrder.getMerchantUid(), messageUtil.get(MessageCode.ORDER_COMPLETE));
    }

    // 장바구니 주문
    public MessageResponse placeCartOrder(CartOrderRequest request, String email) {
        Customer customer = getCustomerByEmail(email);

        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND));

        List<OrderItemRequest> orderItemRequests = request.getOrderItems();

        if (orderItemRequests.isEmpty()) {
            throw new AppException(CART_ITEM_NOT_EXIST_IN_CART);
        }

        MemberShip memberShip = customer.getMemberShip();
        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());
        delivery.createDeliveryStatus(DeliveryStatus.READY);

        List<OrderItem> orderItems = createOrderItemsFromCart(orderItemRequests, memberShip);
        Order order = Order.createOrders(request.getMerchantUid(), customer, delivery, orderItems);
        orderRepository.save(order);

        orderItems.forEach(orderItem -> {
            cartItemRepository.deleteCartItem(cart, orderItem.getItem());
        });

        return new MessageResponse(order.getMerchantUid(), messageUtil.get(MessageCode.ORDER_COMPLETE));
    }

    // 주문 롤백
    public MessageResponse rollbackOrder(String email, OrderRollbackRequest request) {

        Order order = orderRepository.findByMerchantUid(request.getMerchantUid())
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));

        if (!email.equals(order.getCustomer().getEmail())) {
            throw new AppException(UNAUTHORIZED_ORDER_ACCESS);
        }

        List<OrderItem> orderItemList = order.getOrderItemList();

        for (OrderItem orderItem : orderItemList) {

            Long itemId = orderItem.getItem().getId();

            Item item = itemRepository.findPessimisticLockById(itemId)
                    .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

            item.increase(orderItem.getCount());
        }

        order.rollbackPayment();

        return new MessageResponse(order.getMerchantUid(), messageUtil.get(MessageCode.ORDER_ROLLBACK));
    }

    // 주문 취소
    public MessageResponse cancelOrder(UUID merchantUid, CancelItemRequest request) throws IamportResponseException, IOException {

        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));

        BigDecimal totalRefundAmount = BigDecimal.ZERO;

        if (request.getItemUuidList().isEmpty()) {
            totalRefundAmount = totalRefundAmount.add(order.getTotalPrice());
        } else {
            List<UUID> itemUuidList = request.getItemUuidList();

            for (UUID uuid : itemUuidList) {
                Long itemId = itemRepository.findIdByUuid(uuid)
                        .orElseThrow(() -> new AppException(ITEM_ID_NOT_FOUND));

                Item item = itemRepository.findPessimisticLockById(itemId)
                        .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

                OrderItem orderItem = orderItemRepository.findByOrderAndItem(order, item)
                        .orElseThrow(() -> new AppException(ORDER_ITEM_NOT_FOUND));

                item.increase(orderItem.getCount());
                totalRefundAmount = totalRefundAmount.add(orderItem.getOrderPrice());
            }

        }

        cancelReservation(new CancelRequest(order.getImpUid(), totalRefundAmount));

        order.getDelivery().cancelDelivery();
        order.cancelPayment();
        asyncCustomerService.subtractMonthlyPurchaseAmount(order.getCustomer().getId(), totalRefundAmount);

        return new MessageResponse(order.getMerchantUid(), messageUtil.get(MessageCode.ORDER_CANCEL));
    }


    // 결제 사전 검증
    public PreparationResponse validatePrePayment(PreparationRequest request) throws IamportResponseException, IOException {
        String merchantUid = UUIDGenerator.generateUUIDv7().toString();
        PrepareData prepareData = new PrepareData(merchantUid, request.getTotalPrice());
        IamportResponse<Prepare> response = iamportClient.postPrepare(prepareData);

        if (response.getCode() != 0) {
            throw new AppException(FAILED_PREPARE_VALID, response.getMessage());
        }
        return PreparationResponse.builder().merchantUid(merchantUid).build();
    }

    // 결제 사후 검증
    public MessageResponse verifyPostPayment(PostVerificationRequest request) throws IamportResponseException, IOException {

        Payment payment = getPayment(request.getImpUid());

        UUID merchantUid = request.getMerchantUid();

        Order order = orderRepository.findByMerchantUid(merchantUid)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND));

        if (!order.getOrderStatus().equals(READY)) {
            throw new AppException(NOT_READY_ORDER_STATUS);
        }

        // 주문 금액과 결제 금액 비교
        BigDecimal expectedAmount = calculateTotalAmount(order.getOrderItemList());
        BigDecimal orderTotalPrice = order.getTotalPrice();
        BigDecimal actualAmount = payment.getAmount();

        // 금액 불일치 시 결제 취소 및 예외 발생
        if (actualAmount.compareTo(expectedAmount) != 0 || actualAmount.compareTo(orderTotalPrice) != 0) {
            CancelData cancelData = createCancelData(payment, BigDecimal.ZERO);
            iamportClient.cancelPaymentByImpUid(cancelData);
            cancelOrder(order);
            throw new AppException(WRONG_PAYMENT_AMOUNT);
        }

        // 결제 정보가 일치하면 주문에 imp_uid 설정 및 주문 상태 변경
        order.completePayment(request.getImpUid());

        asyncCustomerService.addMonthlyPurchaseAmount(order.getCustomer().getId(), order.getTotalPrice());

        // 성공 메시지 응답
        return new MessageResponse(messageUtil.get(ORDER_POST_VERIFICATION));
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

    // === private utils ===
    private Customer getCustomerByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));
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

    private List<OrderItem> createOrderItemsFromCart(List<OrderItemRequest> orderItemRequests, MemberShip memberShip) {
        List<OrderItem> orderItems = new ArrayList<>();

        for (OrderItemRequest itemRequest : orderItemRequests) {
            Long itemId = itemRepository.findIdByUuid(itemRequest.getItemUuid())
                    .orElseThrow(() -> new AppException(ITEM_ID_NOT_FOUND));
            Item item = itemRepository.findPessimisticLockById(itemId)
                    .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

            BigDecimal discountedPrice = memberShip.applyDiscount(item.getPrice());

            OrderItem orderItem = OrderItem.createOrderItem(item, discountedPrice, itemRequest.getItemCnt());
            orderItems.add(orderItem);
        }

        return orderItems;
    }

    private void clearCartItem(Cart cart, OrderItem orderItem) {
        cartItemRepository.deleteCartItem(cart, orderItem.getItem());
    }

    private void cancelOrder(Order order) {
        Delivery delivery = order.getDelivery();
        List<OrderItem> orderItemList = order.getOrderItemList();

        if (delivery.getStatus().equals(DeliveryStatus.COMP)) {
            throw new AppException(ALREADY_ARRIVED, ALREADY_ARRIVED.getMessage());
        }
        delivery.cancelDelivery();
        order.cancelPayment();
        for (OrderItem orderItem : orderItemList) {
            // 비관적 락으로 Item 조회
            Item item = itemRepository.findPessimisticLockById(orderItem.getItem().getId())
                    .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));

            // 재고 증가
            item.increase(orderItem.getCount());
        }
    }
}
