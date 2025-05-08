package store.myproject.onlineshop.repository.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.Order;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, OrderCustomRepository {

    @Query("select o from Order o join fetch o.delivery d join fetch o.orderItemList oi join fetch oi.item i join fetch i.brand b where o.merchantUid = :merchantUid and o.customer = :customer")
    Optional<Order> findMyOrder(@Param("merchantUid") UUID merchantUid, @Param("customer") Customer customer);

    Optional<Order> findByMerchantUid(UUID merchantUid);

    Long countByMerchantUid(UUID merchantUid);

}
