package store.myproject.onlineshop.domain.recipemeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.dto.recipemeta.RecipeMetaDto;

public interface RecipeMetaRepository extends JpaRepository<RecipeMeta, Long> {

    @Modifying
    @Query("UPDATE RecipeMeta rm SET rm.viewCnt = rm.viewCnt + 1 WHERE rm.id = :id")
    void incrementViewCnt(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RecipeMeta rm SET rm.likeCnt = rm.likeCnt + 1 WHERE rm.id = :id")
    void incrementLikeCnt(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RecipeMeta rm SET rm.likeCnt = rm.likeCnt - 1 WHERE rm.id = :id AND rm.likeCnt > 0")
    void decrementLikeCnt(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RecipeMeta rm SET rm.reviewCnt = rm.reviewCnt + 1 WHERE rm.id = :id")
    void incrementReviewCnt(@Param("id") Long id);

    @Modifying
    @Query("UPDATE RecipeMeta rm SET rm.reviewCnt = rm.reviewCnt - 1 WHERE rm.id = :id AND rm.reviewCnt > 0")
    void decrementReviewCnt(@Param("id") Long id);

    @Query("SELECT new store.myproject.onlineshop.dto.recipemeta.RecipeMetaDto(rm.viewCnt, rm.likeCnt, rm.reviewCnt) FROM RecipeMeta rm WHERE rm.id = :recipeId")
    RecipeMetaDto findRecipeMetaDto(@Param("recipeId") Long recipeId);
}
