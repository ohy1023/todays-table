package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.Cart;
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
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;
import store.myproject.onlineshop.domain.order.repository.OrderRepository;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.exception.AppException;

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
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final ItemRepository itemRepository;

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

        Item findItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

        Delivery delivery = Delivery.createWithInfo(request.toDeliveryInfoRequest());

        delivery.createDeliveryStatus(DeliveryStatus.READY);

        // 멤버쉽 할인
        BigDecimal price = memberShip.applyDiscount(findItem.getPrice());

        log.info("할인된 가격 : {}", price);

        // 주문 정보 생성
        OrderItem orderItem = OrderItem.createOrderItem(findCustomer, findItem, price, request.getItemCnt());

        // 주문 생성
        Order order = Order.createOrder(findCustomer, delivery, Collections.singletonList(orderItem));

        Order savedOrder = orderRepository.save(order);

        // 연관 관계 설정
        orderItem.setOrder(savedOrder);

        log.info("Total Price : {}", orderItem.getTotalPrice());

        return savedOrder.toOrderInfo();

    }


    // 단건 주문 취소
    public MessageResponse cancelForOrder(Long orderId) {

        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));

        findOrder.cancel();

        return new MessageResponse("해당 주문이 취소 되었습니다.");
    }

    // 장바구니 내 품목 주문
    public OrderInfo orderByCart(DeliveryInfoRequest request, String email) {
        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Cart findCart = cartRepository.findByCustomer(findCustomer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND, CART_NOT_FOUND.getMessage()));

        // 장바구니에 품목 유무 & 체크 되어 있는지 체크
        validateCartItems(findCart.getCartItems());

        MemberShip memberShip = findCustomer.getMemberShip();

        // Delivery 정보 생성
        Delivery delivery = Delivery.createWithInfo(request);
        delivery.createDeliveryStatus(DeliveryStatus.READY);

        // 주문 정보 생성
        List<OrderItem> orderItemList = createOrderItems(findCart.getCartItems(), memberShip, findCustomer);

        // 주문 생성
        Order order = Order.createOrder(findCustomer, delivery, orderItemList);
        orderRepository.save(order);

        // 주문 후 장바구니 비우기
        clearCartItems(findCart, orderItemList);

        return order.toOrderInfo();
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
                BigDecimal price = memberShip.applyDiscount(cartItem.getItem().getPrice());

                OrderItem orderItem = OrderItem.createOrderItem(customer, cartItem.getItem(), price, cartItem.getCartItemCnt());
                orderItemList.add(orderItem);

            }
        }

        return orderItemList;
    }

    private void clearCartItems(Cart cart, List<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            cartItemRepository.deleteCartItem(cart, orderItem.getItem());
        }
    }
}
