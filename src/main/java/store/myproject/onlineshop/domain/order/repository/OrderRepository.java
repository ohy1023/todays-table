package store.myproject.onlineshop.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

}
