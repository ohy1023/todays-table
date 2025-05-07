package store.myproject.onlineshop.repository.brand;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;
import store.myproject.onlineshop.domain.brand.dto.QBrandInfo;
import store.myproject.onlineshop.domain.imagefile.QImageFile;


import java.util.List;

import static store.myproject.onlineshop.domain.brand.QBrand.brand;
import static store.myproject.onlineshop.domain.imagefile.QImageFile.*;


@Slf4j
@RequiredArgsConstructor
public class BrandRepositoryImpl implements BrandCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<BrandInfo> search(String brandName, Pageable pageable) {

        List<BrandInfo> brandInfoList = queryFactory
                .select(new QBrandInfo(
                        brand.uuid,
                        brand.name,
                        imageFile.imageUrl
                        )
                )
                .from(brand)
                .where(brandNameEq(brandName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(brand.count())
                .from(brand)
                .where(brandNameEq(brandName));

        return PageableExecutionUtils.getPage(brandInfoList, pageable, countQuery::fetchOne);

    }

    private BooleanExpression brandNameEq(String brandName) {
        return StringUtils.hasText(brandName) ? brand.name.eq(brandName) : null;
    }
}
