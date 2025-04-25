package store.myproject.onlineshop.repository.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipe.Recipe;

public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    @Query("select r from Recipe r join r.itemList ri where ri.item.id = :itemId")
    Page<Recipe> findAllByItemId(@Param("itemId") Long itemId, Pageable pageable);
}
