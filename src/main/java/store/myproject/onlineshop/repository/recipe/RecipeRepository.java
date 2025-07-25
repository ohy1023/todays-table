package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    @Query("SELECT r FROM Recipe r JOIN FETCH r.recipeMeta WHERE r.uuid = :uuid")
    Optional<Recipe> findByIdWithMeta(@Param("uuid") UUID uuid);

    @Query("SELECT r.recipeMeta.id FROM Recipe r WHERE r.uuid = :recipeUuid")
    Long findRecipeMetaIdByRecipeUuid(@Param("recipeUuid") UUID recipeUuid);

    Optional<Recipe> findByUuid(UUID uuid);
}
