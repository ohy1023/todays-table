package store.myproject.onlineshop.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.customer.Customer;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailAndTel(String email, String tel);

    Optional<Customer> findByNickName(String nickName);

    @Modifying
    @Query("UPDATE Customer c SET c.monthlyPurchaseAmount = c.monthlyPurchaseAmount + :amount WHERE c.id = :customerId")
    void incrementMonthlyPurchaseAmount(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

}
