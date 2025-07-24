package store.myproject.onlineshop.repository.brand;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;
import store.myproject.onlineshop.mapper.BrandMapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class BrandCustomRepositoryImpl implements BrandCustomRepository {

    private final BrandMapper brandMapper;

    @Override
    public Page<BrandInfo> searchBrand(String brandName, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        List<BrandInfo> brands = brandMapper.searchBrand(brandName);

        PageInfo<BrandInfo> pageInfo = new PageInfo<>(brands);

        return new PageImpl<>(brands, pageable, pageInfo.getTotal());
    }
}
