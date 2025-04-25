package store.myproject.onlineshop.repository.like;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipe.Recipe;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public interface LikeRepository extends JpaRepository<Like, Long> {

    /**
     * 특정 레시피와 사용자에 대한 좋아요를 조회하는 메서드입니다.
     *
     * @param recipe   조회하고자 하는 레시피
     * @param customer 조회하고자 하는 사용자
     * @return Optional<Like> 좋아요 객체가 존재하면 반환, 그렇지 않으면 빈 Optional
     */
    Optional<Like> findByRecipeAndCustomer(Recipe recipe, Customer customer);

    /**
     * 특정 레시피에 대한 좋아요 개수를 조회하는 메서드입니다.
     *
     * @param recipe 조회하고자 하는 레시피
     * @return Integer 좋아요 개수
     */
    Long countByRecipe(Recipe recipe);

    @Query("SELECT l.recipe.id, count(l) FROM Like l WHERE l.recipe.id in :recipeIds GROUP BY l.recipe.id")
    List<Object[]> countByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

    default Map<Long,Long> getLikeCountByRecipeIds(List<Long> recipeIds) {
        return countByRecipeIds(recipeIds).stream().collect(
                Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));
    }
}
