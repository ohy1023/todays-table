package store.myproject.onlineshop.repository.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.brand.Brand;

import java.util.Optional;
import java.util.UUID;


public interface BrandRepository extends JpaRepository<Brand, Long>, BrandCustomRepository {

    boolean existsByBrandName(String brandName);

    Optional<Brand> findBrandByBrandName(String brandName);

    Optional<Brand> findByUuid(UUID uuid);
}
