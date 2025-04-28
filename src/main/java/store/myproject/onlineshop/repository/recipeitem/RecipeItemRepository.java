package store.myproject.onlineshop.repository.recipeitem;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto;

import java.util.List;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
    @Query("""
                select new store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto(
                    i.id,
                    i.itemName,
                    i.price,
                    b.name,
                    (select min(ii.imageUrl) from ImageFile ii where ii.item.id = i.id)
                )
                from RecipeItem ri
                join ri.item i
                join i.brand b
                where ri.recipe.id = :recipeId
            """)
    List<RecipeItemDto> findItemsByRecipeId(@Param("recipeId") Long recipeId);
}
