package store.myproject.onlineshop.domain.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Optional<Recipe> findByRecipeTitle(String title);

    @Modifying
    @Query("update Recipe r set r.recipeViewCnt = r.recipeViewCnt + 1 where r.id = :id")
    int updateView(Long id);
}
