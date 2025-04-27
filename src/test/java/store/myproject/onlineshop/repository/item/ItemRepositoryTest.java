package store.myproject.onlineshop.repository.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.brand.BrandRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(TestConfig.class)
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BrandRepository brandRepository;

    @Nested
    @DisplayName("아이템 검색 테스트")
    class Search {

        @Test
        @DisplayName("아이템 검색 성공")
        void search_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            itemRepository.save(item);

            ItemSearchCond cond = ItemSearchCond.builder()
                    .itemName(item.getItemName())
                    .brandName(brand.getName())
                    .priceGoe(1000L)
                    .priceLoe(10000L)
                    .stockGoe(1L)
                    .stockLoe(100L)
                    .startDate(LocalDateTime.now().minusDays(1))
                    .endDate(LocalDateTime.now().plusDays(1))
                    .build();

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<ItemDto> result = itemRepository.search(cond, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getItemName()).isEqualTo(item.getItemName());
            assertThat(result.getContent().get(0).getBrandName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("start 날짜 없는 경우 아이템 검색")
        void search_without_start_date_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            itemRepository.save(item);

            ItemSearchCond cond = ItemSearchCond.builder()
                    .itemName(item.getItemName())
                    .brandName(brand.getName())
                    .priceGoe(1000L)
                    .priceLoe(10000L)
                    .stockGoe(1L)
                    .stockLoe(100L)
                    .startDate(null)
                    .endDate(LocalDateTime.now().plusDays(1))
                    .build();

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<ItemDto> result = itemRepository.search(cond, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getItemName()).isEqualTo(item.getItemName());
            assertThat(result.getContent().get(0).getBrandName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("end 날짜 없는 경우 아이템 검색")
        void search_without_end_date_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            itemRepository.save(item);

            ItemSearchCond cond = ItemSearchCond.builder()
                    .itemName(item.getItemName())
                    .brandName(brand.getName())
                    .priceGoe(1000L)
                    .priceLoe(10000L)
                    .stockGoe(1L)
                    .stockLoe(100L)
                    .startDate(LocalDateTime.now().minusDays(1))
                    .endDate(null)
                    .build();

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<ItemDto> result = itemRepository.search(cond, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getItemName()).isEqualTo(item.getItemName());
            assertThat(result.getContent().get(0).getBrandName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("조건 없이 검색")
        void search_without_cond() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            itemRepository.save(item);

            ItemSearchCond cond = ItemSearchCond.builder()
                    .build();

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<ItemDto> result = itemRepository.search(cond, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("아이템 조회 테스트")
    class FindItem {

        @Test
        @DisplayName("아이템 ID 조회 성공")
        void find_by_id_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            Item savedItem = itemRepository.save(item);

            // when
            Optional<Item> result = itemRepository.findById(savedItem.getId());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getItemName()).isEqualTo(savedItem.getItemName());
            assertThat(result.get().getBrand().getName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("아이템 이름 조회 성공")
        void find_by_name_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            itemRepository.save(item);

            // when
            Optional<Item> result = itemRepository.findItemByItemName(item.getItemName());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getItemName()).isEqualTo(item.getItemName());
        }
    }
}
