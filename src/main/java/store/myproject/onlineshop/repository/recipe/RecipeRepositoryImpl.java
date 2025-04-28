package store.myproject.onlineshop.repository.recipe;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.support.PageableExecutionUtils;
import store.myproject.onlineshop.domain.recipe.dto.QSimpleRecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;

import java.util.List;

import static store.myproject.onlineshop.domain.recipe.QRecipe.recipe;
import static store.myproject.onlineshop.domain.recipeitem.QRecipeItem.recipeItem;


@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustomRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Slice<SimpleRecipeDto> findAllSimpleRecipes(Pageable pageable) {
        List<SimpleRecipeDto> content = queryFactory
                .select(new QSimpleRecipeDto(
                        recipe.id,
                        recipe.recipeTitle,
                        recipe.recipeDescription,
                        recipe.customer.nickName,
                        recipe.recipeCookingTime,
                        recipe.recipeServings,
                        recipe.thumbnailUrl,
                        recipe.recipeMeta.viewCnt,
                        recipe.recipeMeta.likeCnt,
                        recipe.recipeMeta.reviewCnt
                ))
                .from(recipe)
                .join(recipe.customer)
                .join(recipe.recipeMeta)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize() + 1)
                .orderBy(getOrderSpecifier(pageable))
                .fetch();

        boolean hasNext = content.size() > pageable.getPageSize();

        if (hasNext) {
            content.remove(pageable.getPageSize());
        }

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Page<SimpleRecipeDto> findRecipeUseItem(Long itemId, Pageable pageable) {
        PathBuilder<?> recipePath = new PathBuilder<>(recipe.getType(), recipe.getMetadata());

        List<SimpleRecipeDto> content = queryFactory
                .select(new QSimpleRecipeDto(
                        recipe.id,
                        recipe.recipeTitle,
                        recipe.recipeDescription,
                        recipe.customer.nickName,
                        recipe.recipeCookingTime,
                        recipe.recipeServings,
                        recipe.thumbnailUrl,
                        recipe.recipeMeta.viewCnt,
                        recipe.recipeMeta.likeCnt,
                        recipe.recipeMeta.reviewCnt
                ))
                .from(recipe)
                .join(recipe.customer)
                .join(recipe.recipeMeta)
                .join(recipe.itemList, recipeItem)
                .where(recipeItem.item.id.eq(itemId))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(getOrderSpecifier(pageable))
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(recipe.count())
                .from(recipe)
                .where(recipe.itemList.any().item.id.eq(itemId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private OrderSpecifier<?> getOrderSpecifier(Pageable pageable) {
        Order direction = Order.DESC;
        String property = "createdDate";

        if (!pageable.getSort().isEmpty()) {
            Sort.Order order = pageable.getSort().iterator().next();
            property = order.getProperty();
            direction = order.getDirection().isAscending() ? Order.ASC : Order.DESC;
        }

        switch (property) {
            case "likeCnt":
                return new OrderSpecifier<>(direction, recipe.recipeMeta.likeCnt);
            case "viewCnt":
                return new OrderSpecifier<>(direction, recipe.recipeMeta.viewCnt);
            case "reviewCnt":
                return new OrderSpecifier<>(direction, recipe.recipeMeta.reviewCnt);
            case "recipeTitle":
                return new OrderSpecifier<>(direction, recipe.recipeTitle);
            case "createdDate":
                return new OrderSpecifier<>(direction, recipe.createdDate);
            default:
                return new OrderSpecifier<>(direction, recipe.createdDate);
        }
    }

}
