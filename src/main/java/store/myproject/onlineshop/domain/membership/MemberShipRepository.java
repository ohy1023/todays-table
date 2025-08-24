package store.myproject.onlineshop.domain.membership;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import store.myproject.onlineshop.domain.customer.Level;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MemberShipRepository extends JpaRepository<MemberShip, Long> {

    Optional<MemberShip> findMemberShipByLevel(Level level);

    boolean existsByLevel(Level level);

    @Query("SELECT ms FROM MemberShip ms ORDER BY ms.baseline ASC")
    List<MemberShip> findTopByLowestBaseline(Pageable pageable);

    Optional<MemberShip> findByUuid(UUID uuid);
}
