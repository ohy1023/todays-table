package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByRecipeTitle(String title);
}
