package store.myproject.onlineshop.domain.brand;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.dto.brand.BrandInfo;
import store.myproject.onlineshop.mapper.BrandMapper;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class BrandCustomRepositoryImpl implements BrandCustomRepository {

    private final BrandMapper brandMapper;

    @Override
    public Page<BrandInfo> searchBrand(String brandName, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        if (pageable.getSort().isSorted()) {
            String orderBy = pageable.getSort().stream()
                    .map(order -> order.getProperty() + " " + order.getDirection())
                    .collect(Collectors.joining(", "));
            PageHelper.orderBy(orderBy);
        }

        List<BrandInfo> brands = brandMapper.searchBrand(brandName);

        PageInfo<BrandInfo> pageInfo = new PageInfo<>(brands);

        return new PageImpl<>(brands, pageable, pageInfo.getTotal());
    }
}
