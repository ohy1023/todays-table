package store.myproject.onlineshop.repository.item;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
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
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.QSimpleItemDto;
import store.myproject.onlineshop.domain.item.dto.SimpleItemDto;

import java.util.List;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;
import static store.myproject.onlineshop.domain.brand.QBrand.brand;
import static store.myproject.onlineshop.domain.item.QItem.item;


@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SimpleItemDto> search(ItemSearchCond cond, Pageable pageable) {
        String brandName = cond.getBrandName();
        String itemName = cond.getItemName();

        boolean hasBrand = StringUtils.hasText(brandName);
        boolean hasItem = StringUtils.hasText(itemName);

        if (hasBrand && !hasItem) {
            return searchByBrandOnly(brandName, pageable);
        }

        List<Long> brandIds = null;
        if (hasBrand) {
            brandIds = queryFactory
                    .select(brand.id)
                    .from(brand)
                    .where(ngramFullTextSearchBrandName(brandName))
                    .fetch();

            if (brandIds.isEmpty()) {
                return PageableExecutionUtils.getPage(List.of(), pageable, () -> 0L);
            }
        }

        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);

        JPAQuery<SimpleItemDto> query = queryFactory
                .select(new QSimpleItemDto(item.uuid, item.itemName, item.price, item.thumbnail, brand.name))
                .from(item)
                .join(item.brand, brand)
                .where(
                        brandIds != null ? brand.id.in(brandIds) : null,
                        fullTextSearchItemName(itemName)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (orderSpecifiers != null) {
            query = query.orderBy(orderSpecifiers);
        }

        List<SimpleItemDto> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .join(item.brand, brand)
                .where(
                        brandIds != null ? brand.id.in(brandIds) : null,
                        fullTextSearchItemName(itemName)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private Page<SimpleItemDto> searchByBrandOnly(String brandName, Pageable pageable) {
        OrderSpecifier<?>[] orderSpecifiers = getOrderSpecifiers(pageable);

        JPAQuery<SimpleItemDto> query = queryFactory
                .select(new QSimpleItemDto(item.uuid, item.itemName, item.price, item.thumbnail, brand.name))
                .from(item)
                .join(item.brand, brand)
                .where(ngramFullTextSearchBrandName(brandName))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        if (orderSpecifiers != null) {
            query = query.orderBy(orderSpecifiers);
        }

        List<SimpleItemDto> content = query.fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .join(item.brand, brand)
                .where(ngramFullTextSearchBrandName(brandName));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
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

    private BooleanExpression fullTextSearchItemName(String itemName) {
        if (!StringUtils.hasText(itemName)) {
            return null;
        }
        return Expressions.booleanTemplate("function('fulltext_match', {0}, {1})", item.itemName, itemName);
    }

    private OrderSpecifier<?>[] getOrderSpecifiers(Pageable pageable) {
        if (pageable.getSort().isEmpty()) {
            return null;
        }

        PathBuilder<?> entityPath = new PathBuilder<>(item.getType(), item.getMetadata());
        List<OrderSpecifier<?>> orderSpecifiers = new java.util.ArrayList<>();

        for (Sort.Order order : pageable.getSort()) {
            String property = order.getProperty();
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;

            // 정렬 대상 프로퍼티에 따라 타입 지정
            switch (property) {
                case "price":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, entityPath.get(property, Integer.class)));
                    break;
                case "stock":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, entityPath.get(property, Integer.class)));
                    break;
                case "createdDate":
                    orderSpecifiers.add(new OrderSpecifier<>(direction, entityPath.get(property, java.time.LocalDateTime.class)));
                    break;
                case "itemName":
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
