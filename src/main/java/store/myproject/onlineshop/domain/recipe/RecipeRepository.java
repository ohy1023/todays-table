package store.myproject.onlineshop.domain.recipe;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import store.myproject.onlineshop.dto.recipe.SimpleRecipeDto;

import java.util.Optional;
import java.util.UUID;

public interface RecipeRepository extends JpaRepository<Recipe, Long>, RecipeCustomRepository {

    @Query("SELECT r FROM Recipe r JOIN FETCH r.recipeMeta WHERE r.uuid = :uuid")
    Optional<Recipe> findByIdWithMeta(@Param("uuid") UUID uuid);

    @Query("SELECT r.recipeMeta.id FROM Recipe r WHERE r.uuid = :recipeUuid")
    Long findRecipeMetaIdByRecipeUuid(@Param("recipeUuid") UUID recipeUuid);

    Optional<Recipe> findByUuid(UUID uuid);

    @Query(value = """
            SELECT new store.myproject.onlineshop.dto.recipe.SimpleRecipeDto(
                r.uuid,
                r.recipeTitle,
                r.recipeDescription,
                r.thumbnailUrl,
                c.nickName,
                r.recipeCookingTime,
                r.recipeServings,
                rm.viewCnt,
                rm.reviewCnt,
                rm.likeCnt
            )
            FROM Recipe r
            JOIN RecipeMeta rm ON r.recipeMeta = rm
            JOIN Customer c ON r.customer = c
            ORDER BY r.createdDate DESC
            """,
            countQuery = """
                        SELECT COUNT(r) FROM Recipe r
                    """)
    Page<SimpleRecipeDto> findRecipeVer1(Pageable pageable);

    @Query("""
            SELECT new store.myproject.onlineshop.dto.recipe.SimpleRecipeDto(
                r.uuid,
                r.recipeTitle,
                r.recipeDescription,
                r.thumbnailUrl,
                c.nickName,
                r.recipeCookingTime,
                r.recipeServings,
                rm.viewCnt,
                rm.reviewCnt,
                rm.likeCnt
            )
            FROM Recipe r
            JOIN Customer c ON r.customer = c
            JOIN RecipeMeta rm ON r.recipeMeta = rm
            ORDER BY r.createdDate DESC
            """)
    Slice<SimpleRecipeDto> findRecipeVer2(Pageable pageable);

//    @Query(value = """
//    SELECT new store.myproject.onlineshop.dto.recipe.SimpleRecipeDto(
//        r.uuid,
//        r.recipeTitle,
//        r.recipeDescription,
//        r.thumbnailUrl,
//        c.nickName,
//        r.recipeCookingTime,
//        r.recipeServings,
//        0L,
//        (SELECT COUNT(i.recipe) FROM Like i WHERE i.recipe = r),
//        (SELECT COUNT(rv.recipe) FROM Review rv WHERE rv.recipe = r)
//    )
//    FROM Recipe r
//    JOIN Customer c ON r.customer = c
//    ORDER BY r.createdDate DESC
//    """,
//            countQuery = "SELECT COUNT(r) FROM Recipe r")
//    Page<SimpleRecipeDto> findRecipeVer4(Pageable pageable);

    @Query(value = """
            SELECT r
            FROM Recipe r
            JOIN Customer c ON r.customer = c
            ORDER BY r.createdDate DESC
            """,
            countQuery = """
                    SELECT COUNT(r) FROM Recipe r
                    """)
    Page<Recipe> findRecipeVer5(Pageable pageable);
}
