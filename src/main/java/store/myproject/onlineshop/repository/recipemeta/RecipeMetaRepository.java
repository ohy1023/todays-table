package store.myproject.onlineshop.repository.recipemeta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;

public interface RecipeMetaRepository extends JpaRepository<RecipeMeta, Long> {

    @Modifying
    @Query("update RecipeMeta rm set rm.viewCnt = rm.viewCnt + 1 where rm.id = :id")
    void incrementViewCnt(@Param("id") Long id);

    @Modifying
    @Query("update RecipeMeta rm set rm.likeCnt = rm.likeCnt + 1 where rm.id = :id")
    void incrementLikeCnt(@Param("id") Long id);

    @Modifying
    @Query("update RecipeMeta rm set rm.likeCnt = rm.likeCnt - 1 where rm.id = :id")
    void decrementLikeCnt(@Param("id") Long id);

    @Modifying
    @Query("update RecipeMeta rm set rm.reviewCnt = rm.reviewCnt + 1 where rm.id = :id")
    void incrementReviewCnt(@Param("id") Long id);

    @Modifying
    @Query("update RecipeMeta rm set rm.reviewCnt = rm.reviewCnt - 1 where rm.id = :id")
    void decrementReviewCnt(@Param("id") Long id);

}
