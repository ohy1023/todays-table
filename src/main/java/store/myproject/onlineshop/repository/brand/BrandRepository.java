package store.myproject.onlineshop.repository.brand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.brand.Brand;

import java.util.Optional;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long>, BrandCustomRepository {

    boolean existsByName(String brandName);

    Optional<Brand> findBrandByName(String name);
}
