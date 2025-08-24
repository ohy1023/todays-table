package store.myproject.onlineshop.domain.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import store.myproject.onlineshop.dto.recipe.RecipeDto;
import store.myproject.onlineshop.dto.recipe.RecipeListCond;
import store.myproject.onlineshop.dto.recipe.SimpleRecipeDto;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RecipeCustomRepository {

    Optional<RecipeDto> findRecipeDtoByUuid(UUID recipeUuid);

    List<SimpleRecipeDto> findRecipeList(RecipeListCond cond);

    Page<SimpleRecipeDto> findRecipeUseItem(Long itemId, Pageable pageable);

//    List<SimpleRecipeDto> findRecipeVer3(RecipeCond cond);
}
