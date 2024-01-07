package store.myproject.onlineshop.domain.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.recipe.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {
}
