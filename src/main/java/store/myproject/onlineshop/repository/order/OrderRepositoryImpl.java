package store.myproject.onlineshop.repository.order;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.order.Order;
import store.myproject.onlineshop.domain.order.OrderStatus;
import store.myproject.onlineshop.domain.order.dto.OrderSearchCond;

import java.util.List;

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
    public Page<Order> search(OrderSearchCond orderSearchCond, Customer customer, Pageable pageable) {
        List<Order> orders = queryFactory.select(order)
                .from(order)
                .join(order.delivery, delivery).fetchJoin()
                .join(order.orderItemList, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .join(item.brand, brand).fetchJoin()
                .where(
                        customerEq(customer),
                        itemNameContains(orderSearchCond.getItemName()),
                        brandNameContains(orderSearchCond.getBrandName()),
                        orderStatusEq(orderSearchCond.getOrderStatus())
                )
                .orderBy(order.createdDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(order.count())
                .from(order)
                .join(order.delivery, delivery).fetchJoin()
                .join(order.orderItemList, orderItem).fetchJoin()
                .join(orderItem.item, item).fetchJoin()
                .join(item.brand, brand).fetchJoin()
                .where(
                        customerEq(customer),
                        itemNameContains(orderSearchCond.getItemName()),
                        brandNameContains(orderSearchCond.getBrandName()),
                        orderStatusEq(orderSearchCond.getOrderStatus())
                );

        return PageableExecutionUtils.getPage(orders, pageable, countQuery::fetchOne);
    }


    private BooleanExpression customerEq(Customer customer) {
        return ObjectUtils.isEmpty(customer) ? null : order.customer.eq(customer);
    }

    private BooleanExpression itemNameContains(String itemName) {
        return StringUtils.hasText(itemName) ? item.itemName.contains(itemName) : null;
    }

    private BooleanExpression brandNameContains(String brandName) {
        return StringUtils.hasText(brandName) ? brand.name.contains(brandName) : null;
    }

    private BooleanExpression orderStatusEq(OrderStatus orderStatus) {
        return ObjectUtils.isEmpty(orderStatus) ? null : order.orderStatus.eq(orderStatus);
    }

}
