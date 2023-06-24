package store.myproject.onlineshop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.entity.Brand;
import store.myproject.onlineshop.domain.entity.Item;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    boolean existsByName(String brandName);
}
