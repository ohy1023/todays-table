package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    @Query("SELECT r FROM Recipe r JOIN FETCH r.recipeMeta WHERE r.id = :id")
    Optional<Recipe> findByIdWithMeta(@Param("id") Long id);
}
