package store.myproject.onlineshop.domain.imagefile.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.List;

public interface ImageFileRepository extends JpaRepository<ImageFile, Long> {

    List<ImageFile> findAllByRecipe(Recipe recipe);
}
