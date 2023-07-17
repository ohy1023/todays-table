package store.myproject.onlineshop.domain.delivery.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.delivery.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {

}
