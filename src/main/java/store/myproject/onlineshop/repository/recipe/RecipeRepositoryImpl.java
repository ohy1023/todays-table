package store.myproject.onlineshop.repository.recipe;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.mapper.RecipeMapper;


import java.util.List;


@RequiredArgsConstructor
public class RecipeRepositoryImpl implements RecipeCustomRepository {

    private final RecipeMapper recipeMapper;

    @Override
    public Slice<SimpleRecipeDto> findAllSimpleRecipes(Pageable pageable) {
        PageHelper.startPage(pageable.getPageNumber() + 1, pageable.getPageSize());

        List<SimpleRecipeDto> content = recipeMapper.findAllSimpleRecipes();

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

}
