package store.myproject.onlineshop.repository.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByEmailAndTel(String email, String tel);

    Optional<Customer> findByNickName(String nickName);

    Long countByMemberShip(MemberShip memberShip);

    @Modifying
    @Query("UPDATE Customer c SET c.monthlyPurchaseAmount = c.monthlyPurchaseAmount + :amount WHERE c.id = :customerId")
    void incrementMonthlyPurchaseAmount(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

    @Modifying
    @Query("UPDATE Customer c SET c.memberShip.id = :membershipId WHERE c.id IN :customerIds")
    void updateMemberships(@Param("customerIds") List<Long> customerIds,
                            @Param("membershipId") Long membershipId);

    @Modifying
    @Query("UPDATE Customer c SET c.monthlyPurchaseAmount = :amount WHERE c.id = :customerId")
    void updateMonthlyPurchaseAmount(@Param("customerId") Long customerId, @Param("amount") BigDecimal amount);

}
