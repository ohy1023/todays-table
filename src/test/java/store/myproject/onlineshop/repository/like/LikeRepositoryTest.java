package store.myproject.onlineshop.repository.like;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.like.Like;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.RecipeFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.recipe.RecipeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class LikeRepositoryTest {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    RecipeRepository recipeRepository;

    @Nested
    @DisplayName("특정 레시피와 고객으로 좋아요 조회")
    class FindByRecipeAndCustomerTest {

        @Test
        @DisplayName("성공")
        void find_by_recipe_and_customer_success() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer));
            Like like = Like.of(customer, recipe);
            likeRepository.save(like);

            // when
            Optional<Like> result = likeRepository.findByRecipeAndCustomer(recipe, customer);

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getCustomer()).isEqualTo(customer);
            assertThat(result.get().getRecipe()).isEqualTo(recipe);
        }

        @Test
        @DisplayName("실패")
        void find_by_recipe_and_customer_fail() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer));

            // when
            Optional<Like> result = likeRepository.findByRecipeAndCustomer(recipe, customer);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("특정 레시피의 좋아요 개수 조회")
    class CountByRecipeTest {

        @Test
        @DisplayName("좋아요 1")
        void count_by_recipe_success() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer));
            Like like = Like.of(customer, recipe);
            likeRepository.save(like);

            // when
            Long count = likeRepository.countByRecipe(recipe);

            // then
            assertThat(count).isEqualTo(1L);
        }

        @Test
        @DisplayName("좋아요 없음")
        void count_by_recipe_zero() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer));

            // when
            Long count = likeRepository.countByRecipe(recipe);

            // then
            assertThat(count).isZero();
        }
    }

    @Nested
    @DisplayName("레시피 ID 리스트로 좋아요 수 조회")
    class GetLikeCountByRecipeIdsTest {

        @Test
        @DisplayName("성공")
        void get_like_count_by_recipe_ids_success() {
            // given
            Customer customer1 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer2 = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe1 = recipeRepository.save(RecipeFixture.createRecipe(customer1));
            Recipe recipe2 = recipeRepository.save(RecipeFixture.createRecipe(customer1));

            likeRepository.save(Like.of(customer1, recipe1));
            likeRepository.save(Like.of(customer1, recipe2));
            likeRepository.save(Like.of(customer2, recipe2)); // recipe2에 좋아요 2개

            List<Long> recipeIds = List.of(recipe1.getId(), recipe2.getId());

            // when
            Map<Long, Long> likeCounts = likeRepository.getLikeCountByRecipeIds(recipeIds);

            // then
            assertThat(likeCounts.get(recipe1.getId())).isEqualTo(1L);
            assertThat(likeCounts.get(recipe2.getId())).isEqualTo(2L);
        }

        @Test
        @DisplayName("좋아요 0")
        void get_like_count_by_recipe_ids_empty() {
            // given
            List<Long> recipeIds = List.of(1L, 2L, 3L);

            // when
            Map<Long, Long> likeCounts = likeRepository.getLikeCountByRecipeIds(recipeIds);

            // then
            assertThat(likeCounts).isEmpty();
        }
    }
}
