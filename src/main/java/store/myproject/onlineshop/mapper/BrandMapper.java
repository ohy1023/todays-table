package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;

import java.util.List;

@Mapper
public interface BrandMapper {

    List<BrandInfo> searchBrand(@Param("brandName") String brandName);
}
