package store.myproject.onlineshop.domain.brand.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;

public interface BrandCustomRepository {

    Page<BrandInfo> search(String brandName, Pageable pageable);
}
