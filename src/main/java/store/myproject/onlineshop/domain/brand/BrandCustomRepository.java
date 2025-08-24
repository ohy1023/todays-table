package store.myproject.onlineshop.domain.brand;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.dto.brand.BrandInfo;

public interface BrandCustomRepository {
    Page<BrandInfo> searchBrand(String brandName, Pageable pageable);
}
