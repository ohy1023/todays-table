package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.delivery.Delivery;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.repository.OrderRepository;
import store.myproject.onlineshop.exception.AppException;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DeliveryService {

    private final OrderRepository orderRepository;

    public OrderInfo updateDeliveryInfo(Long orderId, DeliveryUpdateRequest request) {
        Order findOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new AppException(ORDER_NOT_FOUND, ORDER_NOT_FOUND.getMessage()));

        Delivery delivery = findOrder.getDelivery();

        delivery.setInfo(request);

        return findOrder.toOrderInfo();
    }
}
