package store.myproject.onlineshop.repository.item;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.QSimpleItemDto;
import store.myproject.onlineshop.domain.item.dto.SimpleItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static store.myproject.onlineshop.domain.brand.QBrand.brand;
import static store.myproject.onlineshop.domain.imagefile.QImageFile.*;
import static store.myproject.onlineshop.domain.item.QItem.item;


@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<SimpleItemDto> search(ItemSearchCond itemSearchCond, Pageable pageable) {
        List<SimpleItemDto> itemDtoList = queryFactory.select(new QSimpleItemDto(item.uuid, item.itemName, item.price, imageFile.imageUrl.min(), brand.name))
                .from(item)
                .join(item.brand, brand)
                .leftJoin(item.imageFileList, imageFile)
                .where(
                        itemNameContains(itemSearchCond.getItemName()),
                        brandNameContains(itemSearchCond.getBrandName()),
                        priceLoe(itemSearchCond.getPriceLoe()),
                        priceGoe(itemSearchCond.getPriceGoe()),
                        stockLoe(itemSearchCond.getStockLoe()),
                        stockGoe(itemSearchCond.getStockGoe()),
                        betweenCreatedDate(itemSearchCond.getStartDate(), itemSearchCond.getEndDate())
                )
                .groupBy(item.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(item.count())
                .from(item)
                .join(item.brand, brand)
                .where(
                        item.deletedDate.isNull(),
                        itemNameContains(itemSearchCond.getItemName()),
                        brandNameContains(itemSearchCond.getBrandName()),
                        priceLoe(itemSearchCond.getPriceLoe()),
                        priceGoe(itemSearchCond.getPriceGoe()),
                        stockLoe(itemSearchCond.getStockLoe()),
                        stockGoe(itemSearchCond.getStockGoe()),
                        betweenCreatedDate(itemSearchCond.getStartDate(), itemSearchCond.getEndDate())
                );

        return PageableExecutionUtils.getPage(itemDtoList, pageable, countQuery::fetchOne);
    }

    private BooleanExpression brandNameContains(String brandName) {
        return StringUtils.hasText(brandName) ? brand.name.contains(brandName) : null;
    }

    private BooleanExpression itemNameContains(String itemName) {
        return StringUtils.hasText(itemName) ? item.itemName.contains(itemName) : null;
    }

    private BooleanExpression priceGoe(Long priceGoe) {
        return priceGoe == null ? null : item.price.goe(priceGoe);
    }

    private BooleanExpression priceLoe(Long priceLoe) {
        return priceLoe == null ? null : item.price.loe(priceLoe);
    }

    private BooleanExpression stockGoe(Long stockGoe) {
        return stockGoe == null ? null : item.stock.goe(stockGoe);
    }

    private BooleanExpression stockLoe(Long stockLoe) {
        return stockLoe == null ? null : item.stock.loe(stockLoe);
    }

    private BooleanExpression betweenCreatedDate(LocalDateTime start, LocalDateTime end) {

        if (start == null || end == null) {
            return null;
        }
        return item.createdDate.between(start, end);
    }
}
