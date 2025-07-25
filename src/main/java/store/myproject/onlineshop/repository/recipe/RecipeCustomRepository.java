package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeListCond;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;

import java.util.Optional;
import java.util.UUID;

public interface RecipeCustomRepository {

    Optional<RecipeDto> findRecipeDtoByUuid(UUID recipeUuid);

    Slice<SimpleRecipeDto> findRecipeList(RecipeListCond cond, Pageable pageable);

    Page<SimpleRecipeDto> findRecipeUseItem(Long itemId, Pageable pageable);
}
