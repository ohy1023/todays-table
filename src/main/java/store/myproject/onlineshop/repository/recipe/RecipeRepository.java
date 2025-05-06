package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;

import java.util.Optional;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    @Query("""
                select new store.myproject.onlineshop.domain.recipe.dto.RecipeDto(
                    r.id,
                    r.recipeTitle,
                    r.recipeDescription,
                    r.recipeCookingTime,
                    r.recipeServings,
                    c.nickName,
                    r.thumbnailUrl,
                    rm.id,
                    rm.viewCnt,
                    rm.reviewCnt,
                    rm.likeCnt
                )
                from Recipe r
                join r.recipeMeta rm
                join r.customer c
                where r.id = :recipeId
            """)
    Optional<RecipeDto> findRecipeDtoById(@Param("recipeId") Long recipeId);

    @Query("SELECT r FROM Recipe r JOIN FETCH r.recipeMeta WHERE r.id = :id")
    Optional<Recipe> findByIdWithMeta(@Param("id") Long id);
}
