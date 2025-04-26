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
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.recipe.Recipe;
import store.myproject.onlineshop.domain.recipeitem.RecipeItem;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.fixture.RecipeFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.repository.recipeitem.RecipeItemRepository;

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

    @Autowired
    private RecipeItemRepository recipeItemRepository;

    @Nested
    @DisplayName("아이템 ID로 레시피 조회")
    class FindAllByItemId {

        @Test
        @DisplayName("성공")
        void find_all_by_item_Id_success() {
            // given
            Brand brand = brandRepository.save(BrandFixture.createBrand());
            Item item = itemRepository.save(ItemFixture.createItem(brand));
            Customer customer = customerRepository.save(CustomerFixture.createCustomer());
            Recipe recipe = recipeRepository.save(RecipeFixture.createRecipe(customer));
            RecipeItem recipeItem = recipeItemRepository.save(RecipeItem.createRecipeItem(item));
            recipe.addItem(recipeItem);

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<Recipe> result = recipeRepository.findAllByItemId(item.getId(), pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getId()).isEqualTo(recipe.getId());
        }

    }
}
