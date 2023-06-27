package store.myproject.onlineshop.domain.membership.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.util.Optional;

public interface MemberShipRepository extends JpaRepository<MemberShip, Long> {

    Optional<MemberShip> findMemberShipByLevel(Level level);
}
