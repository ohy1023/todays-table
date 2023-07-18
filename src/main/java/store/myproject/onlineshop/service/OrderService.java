package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
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
import store.myproject.onlineshop.domain.orderitem.repository.OrderItemRepository;
import store.myproject.onlineshop.exception.AppException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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

    public OrderInfo orderByOne(OrderInfoRequest request, String email) {

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        MemberShip memberShip = findCustomer.getMemberShip();

        Item findItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

        Delivery delivery = Delivery.setDeliveryInfo(request.toDeliveryInfoRequest());

        delivery.setDeliveryStatus(DeliveryStatus.READY);

        BigDecimal price = memberShip.applyDiscount(findItem.getPrice());

        log.info("할인된 가격 : {}", price);

        OrderItem orderItem = OrderItem.createOrderItem(findItem, price, request.getItemCnt());

        Order order = Order.createOrder(findCustomer, delivery, Collections.singletonList(orderItem));

        Order savedOrder = orderRepository.save(order);

        orderItem.setOrder(savedOrder);

        log.info("tp:{}", orderItem.getTotalPrice());

        findCustomer.purchase(orderItem.getTotalPrice());

        findCustomer.addPurchaseAmount(orderItem.getTotalPrice());

        return savedOrder.toOrderInfo();

    }


    public MessageResponse cancelForOrder(Long orderId) {

        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));

        findOrder.cancel();

        return new MessageResponse("해당 주문이 취소 되었습니다.");
    }

    public OrderInfo orderByCart(DeliveryInfoRequest request, String email) {

        List<OrderItem> orderItemList = new ArrayList<>();

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        Cart findCart = cartRepository.findByCustomer(findCustomer)
                .orElseThrow(() -> new AppException(CART_NOT_FOUND, CART_NOT_FOUND.getMessage()));

        MemberShip memberShip = findCustomer.getMemberShip();

        Delivery delivery = Delivery.setDeliveryInfo(request);

        delivery.setDeliveryStatus(DeliveryStatus.READY);


        for (CartItem cartItem : findCart.getCartItems()) {

            if (cartItem.isChecked()) {
                BigDecimal price = memberShip.applyDiscount(cartItem.getItem().getPrice());

                OrderItem orderItem = OrderItem.createOrderItem(cartItem.getItem(), price, cartItem.getCartItemCnt());

                orderItemList.add(orderItem);

                findCustomer.purchase(orderItem.getTotalPrice());

                findCustomer.addPurchaseAmount(orderItem.getTotalPrice());

            }

        }

        Order order = Order.createOrder(findCustomer, delivery, orderItemList);

        orderRepository.save(order);

        // 장바구니 비우기
        for (OrderItem orderItem : orderItemList) {
            cartItemRepository.deleteCartItem(findCart, orderItem.getItem());
        }

        return order.toOrderInfo();
    }
}
