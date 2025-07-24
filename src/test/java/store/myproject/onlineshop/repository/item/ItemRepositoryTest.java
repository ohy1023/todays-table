package store.myproject.onlineshop.repository.item;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.SimpleItemDto;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.global.config.TestConfig;
import store.myproject.onlineshop.repository.brand.BrandJpaRepository;
import store.myproject.onlineshop.repository.imagefile.ImageFileRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
class ItemRepositoryTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private BrandJpaRepository brandJpaRepository;
    @Autowired
    private ImageFileRepository imageFileRepository;

    @Nested
    @DisplayName("아이템 검색 테스트")
    class Search {

// H2 데이터 베이스에서는 MATCH AGAINST를 지원하지 않아 주석처리
//        @Test
//        @DisplayName("아이템 검색 성공")
//        void search_success() {
//            // given
//            Brand brand = BrandFixture.createBrand();
//            brandRepository.save(brand);
//            ImageFile brandImage = ImageFileFixture.withBrand(brand);
//            imageFileRepository.save(brandImage);
//            Item item = ItemFixture.createItem(brand);
//            itemRepository.save(item);
//            ImageFile itemImage = ImageFileFixture.withItem(item);
//            imageFileRepository.save(itemImage);
//
//            ItemSearchCond cond = ItemSearchCond.builder()
//                    .itemName(item.getItemName())
//                    .brandName(brand.getName())
//                    .build();
//
//            PageRequest pageRequest = PageRequest.of(0, 10);
//
//            // when
//            Page<SimpleItemDto> result = itemRepository.search(cond, pageRequest);
//
//            // then
//            assertThat(result.getContent()).hasSize(1);
//            assertThat(result.getContent().get(0).getItemName()).isEqualTo(item.getItemName());
//        }

        @Test
        @DisplayName("조건 없이 검색")
        void search_without_cond() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandJpaRepository.save(brand);
            Item item = ItemFixture.createItem(brand);
            itemRepository.save(item);

            ItemSearchCond cond = ItemSearchCond.builder()
                    .build();

            PageRequest pageRequest = PageRequest.of(0, 10);

            // when
            Page<SimpleItemDto> result = itemRepository.search(cond, pageRequest);

            // then
            assertThat(result.getContent()).hasSize(1);
        }
    }

    @Test
    @DisplayName("아이템 ID 조회 성공")
    void find_by_uuid_success() {
        // given
        Brand brand = BrandFixture.createBrand();
        brandJpaRepository.save(brand);
        Item item = ItemFixture.createItem(brand);
        Item savedItem = itemRepository.save(item);

        // when
        Optional<Item> result = itemRepository.findByUuid(savedItem.getUuid());

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
        brandJpaRepository.save(brand);
        Item item = ItemFixture.createItem(brand);
        itemRepository.save(item);

        // when
        Optional<Item> result = itemRepository.findItemByItemName(item.getItemName());

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getItemName()).isEqualTo(item.getItemName());
    }

//    @Test
//    @DisplayName("Pessimistic Lock을 이용한 아이템 조회 성공")
//    void find_pessimistic_lock_by_id_success() {
//        // given
//        Brand brand = brandRepository.save(BrandFixture.createBrand());
//        Item item = ItemFixture.createItem(brand);
//        itemRepository.save(item);
//        Long itemId = item.getId();
//
//
//        // when
//        Optional<Item> lockedItem = itemRepository.findPessimisticLockById(itemId);
//
//        // then
//        assertThat(lockedItem).isPresent();
//        assertThat(lockedItem.get().getId()).isEqualTo(itemId);
//    }

}
