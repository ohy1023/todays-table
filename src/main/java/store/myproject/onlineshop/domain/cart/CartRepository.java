package store.myproject.onlineshop.domain.cart;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.customer.Customer;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer(Customer customer);
}
