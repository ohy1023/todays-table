package store.myproject.onlineshop.repository.brand;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;
import store.myproject.onlineshop.domain.brand.dto.QBrandInfo;

import java.util.ArrayList;
import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
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
                .where(ngramFullTextSearchBrandName(brandName))
                .join(brand.imageFile, imageFile)
                .orderBy(getOrderSpecifiers(pageable))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(brand.count())
                .from(brand)
                .where(ngramFullTextSearchBrandName(brandName));

        return PageableExecutionUtils.getPage(brandInfoList, pageable, countQuery::fetchOne);

    }

    private BooleanExpression ngramFullTextSearchBrandName(String brandName) {
        if (!StringUtils.hasText(brandName)) {
            return null;
        }
        // MATCH(b.name) AGAINST ('keyword' IN NATURAL LANGUAGE MODE)
        return numberTemplate(Double.class, "function('ngram_match', {0}, {1})",
                brand.name, brandName)
                .gt(0);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return new OrderSpecifier[0];
        }

        PathBuilder<?> entityPath = new PathBuilder<>(brand.getType(), brand.getMetadata());
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            switch (property) {
                case "name":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, entityPath.get(property, String.class)));
                    break;
                default:
                    log.warn("정렬 불가능한 속성: {}", property);
                    break;
            }
        }

        return orderSpecifiers.toArray(new OrderSpecifier[0]);
    }
}
