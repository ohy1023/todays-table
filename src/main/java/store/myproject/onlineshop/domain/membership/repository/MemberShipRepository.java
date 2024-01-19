package store.myproject.onlineshop.domain.membership.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface MemberShipRepository extends JpaRepository<MemberShip, Long> {

    Optional<MemberShip> findMemberShipByLevel(Level level);

    @Query("SELECT ms FROM MemberShip ms WHERE ms.baseline < :usedMoney ORDER BY ms.baseline DESC")
    List<MemberShip> findNextMemberShip(@Param("usedMoney") BigDecimal usedMoney);

}
