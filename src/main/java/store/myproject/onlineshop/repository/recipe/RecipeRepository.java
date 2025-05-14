package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    @Query("""
                select new store.myproject.onlineshop.domain.recipe.dto.RecipeDto(
                    r.uuid,
                    r.recipeTitle,
                    r.recipeDescription,
                    r.recipeCookingTime,
                    r.recipeServings,
                    c.nickName,
                    r.thumbnailUrl
                )
                from Recipe r
                join r.recipeMeta rm
                join r.customer c
                where r.uuid = :recipeUuid
            """)
    Optional<RecipeDto> findRecipeDtoByUuid(@Param("recipeUuid") UUID recipeUuid);

    @Query("SELECT r FROM Recipe r JOIN FETCH r.recipeMeta WHERE r.uuid = :uuid")
    Optional<Recipe> findByIdWithMeta(@Param("uuid") UUID uuid);

    @Query("SELECT r.recipeMeta.id FROM Recipe r WHERE r.uuid = :recipeUuid")
    Long findRecipeMetaIdByRecipeUuid(@Param("recipeUuid") UUID recipeUuid);

    Optional<Recipe> findByUuid(UUID uuid);
}
