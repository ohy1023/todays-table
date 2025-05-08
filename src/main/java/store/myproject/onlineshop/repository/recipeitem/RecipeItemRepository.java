package store.myproject.onlineshop.repository.recipeitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto;

import java.util.List;
import java.util.UUID;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    @Query("""
                select new store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto(
                    i.uuid,
                    i.itemName,
                    i.price,
                    b.name,
                    (select min(ii.imageUrl) from ImageFile ii where ii.item.id = i.id)
                )
                from RecipeItem ri
                join ri.item i
                join i.brand b
                where ri.recipe.uuid = :recipUuid
            """)
    List<RecipeItemDto> findItemsByRecipeUuid(@Param("recipUuid") UUID recipUuid);
}
