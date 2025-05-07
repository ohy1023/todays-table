package store.myproject.onlineshop.repository.recipestep;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;

import java.util.List;
import java.util.UUID;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    @Query("""
                select new store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto(
                    s.stepOrder,
                    s.content,
                    s.imageUrl
                )
                from RecipeStep s
                where s.recipe.uuid = :recipUuid
                order by s.stepOrder asc
            """)
    List<RecipeStepDto> findStepsByRecipeUuid(@Param("recipUuid") UUID recipUuid);
}
