package store.myproject.onlineshop.repository.brand;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BrandRepository {

    private final BrandJpaRepository brandJpaRepository;
    private final BrandMapper brandMapper;

    public Brand save(Brand brand) {
        return brandJpaRepository.save(brand);
    }

    public Optional<Brand> findById(Long id) {
        return brandJpaRepository.findById(id);
    }

    public Optional<Brand> findByUuid(UUID uuid) {
        return brandJpaRepository.findByUuid(uuid);
    }

    public Optional<Brand> findBrandByName(String brandName) {
        return brandJpaRepository.findBrandByName(brandName);
    }

    public void deleteById(Long id) {
        brandJpaRepository.deleteById(id);
    }

    public boolean existsByName(String brandName) {
        return brandJpaRepository.existsByName(brandName);
    }

    public Page<BrandInfo> searchBrand(String brandName, Pageable pageable) {
        // PageHelper로 페이징 설정
        PageHelper.startPage(
                pageable.getPageNumber() + 1,  // Spring은 0부터, PageHelper는 1부터
                pageable.getPageSize()
        );

        // 정렬 조건이 있으면 추가
        if (pageable.getSort().isSorted()) {
            String orderBy = pageable.getSort().stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            PageHelper.orderBy(orderBy);
        }

        List<BrandInfo> brands = brandMapper.searchBrand(brandName);

        PageInfo<BrandInfo> pageInfo = new PageInfo<>(brands);

        return new PageImpl<>(
                pageInfo.getList(),
                pageable,
                pageInfo.getTotal()
        );
    }
}
