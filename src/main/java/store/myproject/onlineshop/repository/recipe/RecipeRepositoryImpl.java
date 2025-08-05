package store.myproject.onlineshop.repository.recipe;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCond;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeListCond;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.mapper.RecipeMapper;


import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustomRepository {

    private final RecipeMapper recipeMapper;

    @Override
    public Optional<RecipeDto> findRecipeDtoByUuid(UUID recipeUuid) {
        RecipeDto recipeDto = recipeMapper.findRecipeDtoByUuid(recipeUuid);
        return Optional.ofNullable(recipeDto);
    }

    @Override
    public Slice<SimpleRecipeDto> findRecipeList(RecipeListCond cond, Pageable pageable) {
        // 정렬 조건 추출 및 변환
        String orderBy = toOrderByClause(pageable);

        // PageHelper로 페이지 + 정렬 설정
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize(), false);

        if (!orderBy.isBlank()) {
            PageHelper.orderBy(orderBy);
        }

        // 쿼리 실행
        List<SimpleRecipeDto> content = recipeMapper.findRecipeList(cond);

        // PageInfo로 다음 페이지 존재 여부 계산
        PageInfo<SimpleRecipeDto> pageInfo = new PageInfo<>(content);
        boolean hasNext = pageInfo.getPageNum() < pageInfo.getPages();

        return new SliceImpl<>(content, pageable, hasNext);
    }

    @Override
    public Page<SimpleRecipeDto> findRecipeUseItem(Long itemId, Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        List<SimpleRecipeDto> content = recipeMapper.findRecipeUseItem(itemId);

        PageInfo<SimpleRecipeDto> pageInfo = new PageInfo<>(content);

        return new PageImpl<>(content, pageable, pageInfo.getTotal());
    }

    @Override
    public List<SimpleRecipeDto> findRecipeVer3(RecipeCond cond) {
        int sizePlusOne = cond.getSize() + 1;

        cond.setSizePlusOne(sizePlusOne);

        return recipeMapper.findRecipeVer3(cond);
    }

    private String toOrderByClause(Pageable pageable) {
        return pageable.getSort().stream()
                .findFirst()
                .map(order -> {
                    String column = switch (order.getProperty()) {
                        case "recipeView" -> "rm.recipe_view";
                        case "likeCnt" -> "rm.like_cnt";
                        case "createdDate" -> "r.created_date";
                        default -> "r.created_date";
                    };
                    return column + " DESC";
                })
                .orElse("r.created_date DESC");
    }

}
