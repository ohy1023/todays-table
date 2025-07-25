package store.myproject.onlineshop.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;

import java.util.List;

@Mapper
public interface RecipeMapper {

    List<SimpleRecipeDto> findAllSimpleRecipes();

    List<SimpleRecipeDto> findRecipeUseItem(@Param("itemId") Long itemId);
}
