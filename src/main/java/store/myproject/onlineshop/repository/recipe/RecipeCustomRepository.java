package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;

import java.util.UUID;

public interface RecipeCustomRepository {

    Slice<SimpleRecipeDto> findAllSimpleRecipes(Pageable pageable);

    Page<SimpleRecipeDto> findRecipeUseItem(Long itemId, Pageable pageable);
}
