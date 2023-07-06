package store.myproject.onlineshop.domain.orderitem.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
}
