package store.myproject.onlineshop.repository.orderitem;


import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndItem(Order order, Item item);

}
