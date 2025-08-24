package store.myproject.onlineshop.domain.recipe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.dto.recipe.RecipeItemDto;

import java.util.List;
import java.util.UUID;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    @Query("""
                select new store.myproject.onlineshop.dto.recipe.RecipeItemDto(
                    i.uuid,
                    i.itemName,
                    i.itemPrice,
                    b.brandName,
                    i.thumbnail
                )
                from RecipeItem ri
                join ri.item i
                join i.brand b
                where ri.recipe.uuid = :recipUuid
            """)
    List<RecipeItemDto> findItemsByRecipeUuid(@Param("recipUuid") UUID recipUuid);
}
