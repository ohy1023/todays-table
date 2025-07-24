package store.myproject.onlineshop.repository.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.brand.Brand;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BrandJpaRepository extends JpaRepository<Brand, Long>{

    boolean existsByName(String brandName);

    Optional<Brand> findBrandByName(String name);

    Optional<Brand> findByUuid(UUID uuid);
}
