package store.myproject.onlineshop.repository.cartitem;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import store.myproject.onlineshop.domain.cart.Cart;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.domain.cartitem.dto.QCartItemResponse;

import java.util.List;

import static store.myproject.onlineshop.domain.cartitem.QCartItem.cartItem;
import static store.myproject.onlineshop.domain.item.QItem.item;


@Slf4j
@RequiredArgsConstructor
public class CartItemRepositoryImpl implements CartItemCustomRepository {

    private final JPAQueryFactory queryFactory;


    @Override
    public Page<CartItemResponse> findByCartPage(Cart findCart, Pageable pageable) {
        List<CartItemResponse> cartItemResponses = queryFactory
                .select(new QCartItemResponse(
                        item.id,
                        item.itemName,
                        item.price,
                        item.stock,
                        cartItem.cartItemCnt)
                )
                .from(cartItem)
                .where(cartEq(findCart))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory.select(cartItem.count())
                .from(cartItem)
                .where(cartEq(findCart));

        return PageableExecutionUtils.getPage(cartItemResponses, pageable, countQuery::fetchOne);
    }

    private BooleanExpression cartEq(Cart cart) {
        return ObjectUtils.isEmpty(cart) ? null : cartItem.cart.eq(cart);
    }
}
