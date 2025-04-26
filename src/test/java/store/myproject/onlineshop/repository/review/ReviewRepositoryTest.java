package store.myproject.onlineshop.repository.review;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.review.Review;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.RecipeFixture;
import store.myproject.onlineshop.fixture.ReviewFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.recipe.RecipeRepository;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class ReviewRepositoryTest {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Nested
    @DisplayName("특정 레시피의 부모 리뷰 조회")
    class FindParentReviews {

        @Test
        @DisplayName("부모 리뷰 페이징 조회 성공")
        void find_parent_reviews_success() {
            // given
            Customer customer1 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer2 = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer1));
            Review parentReview = reviewRepository.save(ReviewFixture.createParentReview(recipe, customer2));

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<Review> result = reviewRepository.findParentReviews(recipe.getId(), pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(parentReview.getId());
        }
    }

    @Nested
    @DisplayName("부모 리뷰들의 자식 리뷰 3개 조회")
    class FindTop3ChildReviews {
        @Test
        @DisplayName("자식 리뷰 조회 성공")
        void find_top3_child_reviews_success() {
            // given
            Customer customer1 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer2 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer3 = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer1));
            Review parentReview = reviewRepository.save(ReviewFixture.createParentReview(recipe, customer2));
            reviewRepository.save(ReviewFixture.createChildReview(recipe, customer3, parentReview));
            reviewRepository.save(ReviewFixture.createChildReview(recipe, customer3, parentReview));

            PageRequest pageRequest = PageRequest.of(0, 3);

            // when
            List<Review> childReviews = reviewRepository.findTop3ChildReviews(List.of(parentReview.getId()), pageRequest);

            // then
            assertThat(childReviews).hasSize(2);
            assertThat(childReviews).extracting("parentId").containsOnly(parentReview.getId());
        }
    }

    @Nested
    @DisplayName("부모 리뷰 ID별 자식 리뷰 개수 조회")
    class CountByParentIds {
        @Test
        @DisplayName("부모 리뷰별 자식 리뷰 개수 조회 성공")
        void count_by_parent_ids_success() {
            // given
            Customer customer1 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer2 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer3 = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer1));
            Review parentReview = reviewRepository.save(ReviewFixture.createParentReview(recipe, customer2));
            reviewRepository.save(ReviewFixture.createChildReview(recipe, customer3, parentReview));
            reviewRepository.save(ReviewFixture.createChildReview(recipe, customer3, parentReview));

            // when
            Map<Long, Long> result = reviewRepository.countByParentIds(List.of(parentReview.getId()));

            // then
            assertThat(result).containsEntry(parentReview.getId(), 2L);
        }
    }

    @Nested
    @DisplayName("레시피 ID별 리뷰 개수 조회")
    class GetReviewCountByRecipeIds {
        @Test
        @DisplayName("레시피별 리뷰 개수 조회 성공")
        void get_review_count_By_recipe_ids_success() {
            // given
            Customer customer1 = customerRepository.save(CustomerFixture.createCustomer());
            Customer customer2 = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer1));
            reviewRepository.save(ReviewFixture.createParentReview(recipe, customer2));
            reviewRepository.save(ReviewFixture.createParentReview(recipe, customer2));

            // when
            Map<Long, Long> result = reviewRepository.getReviewCountByRecipeIds(List.of(recipe.getId()));

            // then
            assertThat(result).containsEntry(recipe.getId(), 2L);
        }
    }


}
