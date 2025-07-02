package store.myproject.onlineshop.repository.order;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static store.myproject.onlineshop.domain.brand.QBrand.brand;
import static store.myproject.onlineshop.domain.delivery.QDelivery.delivery;
import static store.myproject.onlineshop.domain.item.QItem.item;
import static store.myproject.onlineshop.domain.order.QOrder.order;
import static store.myproject.onlineshop.domain.orderitem.QOrderItem.orderItem;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Order> findMyOrders(OrderSearchCond cond, Customer customer) {
        return queryFactory.selectFrom(order)
                .join(order.delivery, delivery).fetchJoin()
                .join(order.orderItemList, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .join(item.brand, brand).fetchJoin()
                .where(
                        customerEq(customer),
                        fullTextSearchBrandName(cond.getBrandName()),
                        fullTextSearchItemName(cond.getItemName()),
                        createdDateBetween(cond.getFromDate(), cond.getToDate()),
                        merchantUidLt(cond.getMerchantUid())
                )
                .orderBy(order.merchantUid.desc())
                .limit(cond.getSize() + 1)
                .fetch();
    }


    private BooleanExpression customerEq(Customer customer) {
        return ObjectUtils.isEmpty(customer) ? null : order.customer.eq(customer);
    }

    private BooleanExpression fullTextSearchBrandName(String brandName) {
        if (!StringUtils.hasText(brandName)) {
            return null;
        }

        return Expressions.booleanTemplate("function('fulltext_match', {0}, {1})", brand.name, brandName);
    }

    private BooleanExpression fullTextSearchItemName(String itemName) {
        if (!StringUtils.hasText(itemName)) {
            return null;
        }
        return Expressions.booleanTemplate("function('fulltext_match', {0}, {1})", item.itemName, itemName);
    }

    private BooleanExpression createdDateBetween(LocalDate from, LocalDate to) {
        if (from == null && to == null) return null;
        if (from != null && to != null) {
            return order.createdDate.between(from.atStartOfDay(), to.atTime(23, 59, 59));
        }
        if (from != null) {
            return order.createdDate.goe(from.atStartOfDay());
        }
        return order.createdDate.loe(to.atTime(23, 59, 59));
    }

    private BooleanExpression merchantUidLt(String cursor) {
        if (!StringUtils.hasText(cursor)) return null;
        try {
            UUID uuid = UUID.fromString(cursor);
            return order.merchantUid.lt(uuid);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
