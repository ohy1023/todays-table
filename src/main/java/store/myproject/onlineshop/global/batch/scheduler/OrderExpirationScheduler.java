//package store.myproject.onlineshop.global.batch.scheduler;
//
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//import store.myproject.onlineshop.domain.item.Item;
//import store.myproject.onlineshop.domain.order.Order;
//import store.myproject.onlineshop.domain.orderitem.OrderItem;
//import store.myproject.onlineshop.exception.AppException;
//import store.myproject.onlineshop.repository.item.ItemRepository;
//import store.myproject.onlineshop.repository.order.OrderRepository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import static store.myproject.onlineshop.domain.order.OrderStatus.*;
//import static store.myproject.onlineshop.exception.ErrorCode.*;
//
//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class OrderExpirationScheduler {
//
//    private final OrderRepository orderRepository;
//    private final ItemRepository itemRepository;
//
//    @Transactional
//    @Scheduled(fixedDelay = 60000) // 1분마다 실행
//    public void expireUnpaidOrders() {
//
//        LocalDateTime expirationTime = LocalDateTime.now().minusMinutes(15);
//        List<Order> expiredOrders = orderRepository.findAllByOrderStatusAndCreatedDateBefore(READY, expirationTime);
//
//        for (Order order : expiredOrders) {
//            log.info("[OrderExpirationScheduler] 만료 주문 처리 시작 - merchantUid: {}", order.getMerchantUid());
//
//            for (OrderItem orderItem : order.getOrderItemList()) {
//                Item item = itemRepository.findPessimisticLockById(orderItem.getItem().getId())
//                        .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
//                item.increase(orderItem.getCount());
//            }
//
//            order.rollbackPayment();
//            log.info("[OrderExpirationScheduler] 주문 상태 ROLLBACK 처리 완료 - merchantUid: {}", order.getMerchantUid());
//        }
//
//        if (!expiredOrders.isEmpty()) {
//            log.info("[OrderExpirationScheduler] 총 {}건의 미결제 주문을 롤백 처리하였습니다.", expiredOrders.size());
//        }
//    }
//}
