package store.myproject.onlineshop.repository.recipestep;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.recipestep.RecipeStep;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {
}
