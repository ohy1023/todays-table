package store.myproject.onlineshop.domain.brand.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.brand.Brand;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByName(String brandName);
}
