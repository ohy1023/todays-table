package store.myproject.onlineshop.repository.recipe;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.fixture.RecipeFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.item.ItemRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class RecipeRepositoryTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Nested
    @DisplayName("ID로 레시피 + 메타 정보 조회 (findByIdWithMeta)")
    class FindByIdWithMeta {

        @Test
        @DisplayName("성공 - ID로 레시피와 메타정보를 조회")
        void find_by_id_with_meta_success() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = RecipeFixture.createRecipe(customer);
            Recipe savedRecipe = recipeRepository.save(recipe);

            // when
            Recipe result = recipeRepository.findByIdWithMeta(savedRecipe.getId())
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));

            // then
            assertThat(result.getRecipeMeta()).isNotNull();
            assertThat(result.getId()).isEqualTo(savedRecipe.getId());
        }

        @Test
        @DisplayName("실패 - 존재하지 않는 ID 조회 시 empty")
        void find_by_id_with_meta_empty() {
            // when
            Optional<Recipe> result = recipeRepository.findByIdWithMeta(9999L); // 존재하지 않는 ID

            // then
            assertThat(result).isEmpty();
        }
    }


    @Nested
    @DisplayName("레시피 목록 조회")
    class FindAllSimpleRecipes {

        @Test
        @DisplayName("성공")
        void find_all_simple_recipes_success() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            for (int i = 0; i < 2; i++) {
                recipeRepository.save(RecipeFixture.createRecipe(customer));
            }

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.hasNext()).isFalse();
        }

        @Test
        @DisplayName("성공 - Sort 없이 기본정렬로 슬라이스 조회")
        void find_all_simple_recipes_default_sort_success() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            recipeRepository.save(RecipeFixture.createRecipe(customer));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.unsorted());

            // when
            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
        }

        @Test
        @DisplayName("좋아요순 정렬")
        void find_all_simple_recipes_sort_by_likeCnt() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            recipeRepository.save(RecipeFixture.createRecipe(customer));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("likeCnt")));

            // when
            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("조회수순 정렬")
        void find_all_simple_recipes_sort_by_viewCnt() {
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            recipeRepository.save(RecipeFixture.createRecipe(customer));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("viewCnt")));

            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("리뷰수순 정렬")
        void find_all_simple_recipes_sort_by_reviewCnt() {
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            recipeRepository.save(RecipeFixture.createRecipe(customer));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("reviewCnt")));

            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("레시피 제목순")
        void find_all_simple_recipes_sort_by_recipeTitle() {
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            recipeRepository.save(RecipeFixture.createRecipe(customer));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("recipeTitle")));

            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("생성일자순 정렬")
        void find_all_simple_recipes_sort_by_createdDate() {
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            recipeRepository.save(RecipeFixture.createRecipe(customer));

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("createdDate")));

            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("hasNext true (페이지 크기 초과)")
        void find_all_simple_recipes_has_next_true() {
            // given
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            for (int i = 0; i < 11; i++) {
                recipeRepository.save(RecipeFixture.createRecipe(customer));
            }

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Slice<SimpleRecipeDto> result = recipeRepository.findAllSimpleRecipes(pageRequest);

            // then
            assertThat(result.getContent()).hasSize(10);
            assertThat(result.hasNext()).isTrue();
        }
    }

    @Nested
    @DisplayName("아이템 ID로 레시피 목록 조회")
    class FindRecipeUseItem {

        @Test
        @DisplayName("성공")
        void find_recipe_use_item_success() {
            // given
            Brand brand = brandRepository.save(BrandFixture.createBrand());
            Item item = itemRepository.save(ItemFixture.createItem(brand));
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());

            Recipe recipe = RecipeFixture.createRecipe(customer);
            RecipeItem recipeItem = RecipeItem.createRecipeItem(item);

            recipe.addItem(recipeItem);
            recipeRepository.save(recipe);

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<SimpleRecipeDto> result = recipeRepository.findRecipeUseItem(item.getId(), pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getRecipeId()).isEqualTo(recipe.getId());
        }

        @Test
        @DisplayName("해당 아이템을 사용하는 레시피가 없을 경우")
        void find_recipe_use_item_empty() {
            // given
            Brand brand = brandRepository.save(BrandFixture.createBrand());
            Item item = itemRepository.save(ItemFixture.createItem(brand));
            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<SimpleRecipeDto> result = recipeRepository.findRecipeUseItem(item.getId(), pageRequest);

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("정렬 필드가 잘못됐을 때 기본 정렬로 동작")
        void find_recipe_use_item_invalid_sort_success() {
            // given
            Brand brand = brandRepository.save(BrandFixture.createBrand());
            Item item = itemRepository.save(ItemFixture.createItem(brand));
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());

            Recipe recipe = RecipeFixture.createRecipe(customer);
            RecipeItem recipeItem = RecipeItem.createRecipeItem(item);

            recipe.addItem(recipeItem);
            recipeRepository.save(recipe);

            PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("wrongField"))); // 잘못된 필드 정렬

            // when
            Page<SimpleRecipeDto> result = recipeRepository.findRecipeUseItem(item.getId(), pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }
}
