package store.myproject.onlineshop.domain.order;


import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.item.Item;

import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    Optional<OrderItem> findByOrderAndItem(Order order, Item item);

}
