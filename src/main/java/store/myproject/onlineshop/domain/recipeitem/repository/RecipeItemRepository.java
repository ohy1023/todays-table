package store.myproject.onlineshop.domain.recipeitem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;

public interface RecipeItemRepository extends JpaRepository<RecipeItem, Long> {
}
