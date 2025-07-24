package store.myproject.onlineshop.repository.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.brand.Brand;

import java.util.Optional;
import java.util.UUID;


public interface BrandRepository extends JpaRepository<Brand, Long>, BrandCustomRepository {

    boolean existsByName(String brandName);

    Optional<Brand> findBrandByName(String name);

    Optional<Brand> findByUuid(UUID uuid);
}
