package store.myproject.onlineshop.domain.brand;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BrandRepository extends JpaRepository<Brand, Long>, BrandCustomRepository {

    boolean existsByBrandName(String brandName);

    Optional<Brand> findBrandByBrandName(String brandName);

    Optional<Brand> findByUuid(UUID uuid);
}
