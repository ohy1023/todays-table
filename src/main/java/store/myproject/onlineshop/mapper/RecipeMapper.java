package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeListCond;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RecipeMapper {

    RecipeDto findRecipeDtoByUuid(@Param("recipeUuid") UUID recipeUuid);

    List<SimpleRecipeDto>findRecipeList(@Param("cond") RecipeListCond cond);

    List<SimpleRecipeDto> findRecipeUseItem(@Param("itemId") Long itemId);
}
