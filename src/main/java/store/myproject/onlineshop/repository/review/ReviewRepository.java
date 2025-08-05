package store.myproject.onlineshop.repository.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.review.Review;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("SELECT r FROM Review r WHERE r.parentId = :reviewId")
    Page<Review> findByParentId(@Param("reviewId") Long reviewId, Pageable pageable);

    Optional<Review> findByUuid(UUID uuid);

    @Query("SELECT r FROM Review r WHERE r.recipe = :recipe AND r.parentId IS NULL")
    Page<Review> findParentReviews(@Param("recipe") Recipe recipe, Pageable pageable);

    @Query("SELECT r FROM Review r WHERE r.parentId IN :parentReviewIds ORDER BY r.createdDate ASC")
    List<Review> findTop3ChildReviews(@Param("parentReviewIds") List<Long> parentReviewIds, Pageable pageable);

    @Query("SELECT r.parentId, COUNT(r) FROM Review r WHERE r.parentId IN :parentReviewIds GROUP BY r.parentId")
    List<Object[]> countChildReviews(@Param("parentReviewIds") List<Long> parentReviewIds);

    // 변환 메서드
    default Map<Long, Long> countByParentIds(List<Long> parentReviewIds) {
        return countChildReviews(parentReviewIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    @Query("SELECT r.recipe.id, count(r) FROM Review r WHERE r.recipe.id in :recipeIds GROUP BY r.recipe.id")
    List<Object[]> countByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

    default Map<Long, Long> getReviewCountByRecipeIds(List<Long> recipeIds) {
        return countByRecipeIds(recipeIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }

    Long countByRecipe(Recipe recipe);
}
