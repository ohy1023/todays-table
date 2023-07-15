package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
import store.myproject.onlineshop.domain.order.repository.OrderRepository;
import store.myproject.onlineshop.domain.orderitem.OrderItem;
import store.myproject.onlineshop.domain.orderitem.repository.OrderItemRepository;
import store.myproject.onlineshop.exception.AppException;

import java.math.BigDecimal;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    private final CustomerRepository customerRepository;

    private final OrderItemRepository orderItemRepository;

    private final ItemRepository itemRepository;


    public OrderInfo orderByOne(OrderInfoRequest request, String email) {

        Customer findCustomer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        MemberShip memberShip = findCustomer.getMemberShip();

        Item findItem = itemRepository.findById(request.getItemId())
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));

        Delivery delivery = Delivery.setDeliveryInfo(request);

        BigDecimal price = memberShip.applyDiscount(findItem.getPrice());

        log.info("할인된 가격 : {}", price);

        OrderItem orderItem = OrderItem.createOrderItem(findItem, price, request.getItemCnt());

        Order order = Order.createOrder(findCustomer, delivery, orderItem);

        Order savedOrder = orderRepository.save(order);

        orderItem.setOrder(savedOrder);

        orderItemRepository.save(orderItem);

        log.info("tp:{}", orderItem.getTotalPrice());

        findCustomer.purchase(orderItem.getTotalPrice());

        findCustomer.addPurchaseAmount(orderItem.getTotalPrice());

        return savedOrder.toOrderInfo();

    }


}
