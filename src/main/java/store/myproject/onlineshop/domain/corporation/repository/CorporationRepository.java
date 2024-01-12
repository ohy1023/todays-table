package store.myproject.onlineshop.domain.corporation.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.corporation.Corporation;

import java.util.Optional;

public interface CorporationRepository extends JpaRepository<Corporation, Long> {

    Optional<Corporation> findByCompanyName(String companyName);
    Optional<Corporation> findByRegistrationNumber(String registrationNumber);
    Optional<Corporation> findByCompanyEmail(String companyEmail);
}
