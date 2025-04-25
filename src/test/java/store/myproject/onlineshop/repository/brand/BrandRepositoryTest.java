package store.myproject.onlineshop.repository.brand;

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
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.global.config.TestConfig;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
class BrandRepositoryTest {

    @Autowired
    BrandRepository brandRepository;

    @Nested
    @DisplayName("브랜드 이름으로 조회")
    class FindByName {

        @Test
        @DisplayName("존재하는 브랜드 이름 조회 성공")
        void find_brand_by_name_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);

            // when
            Optional<Brand> result = brandRepository.findBrandByName(brand.getName());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 이름 조회")
        void find_brand_by_name_fail() {
            // when
            Optional<Brand> result = brandRepository.findBrandByName("not-exist");

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("브랜드 존재 여부 체크")
    class ExistsByName {

        @Test
        @DisplayName("브랜드 존재함")
        void exists_true() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);

            // when
            boolean exists = brandRepository.existsByName(brand.getName());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("브랜드 존재하지 않음")
        void exists_false() {
            // when
            boolean exists = brandRepository.existsByName("non-exist");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("브랜드 검색")
    class Search {

        @Test
        @DisplayName("브랜드 검색 성공")
        void search_brand_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandRepository.save(brand);

            // when
            Page<BrandInfo> result = brandRepository.search(brand.getName(), PageRequest.of(0, 10));

            // then
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).getName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("검색 결과 없음")
        void search_brand_empty() {
            // when
            Page<BrandInfo> result = brandRepository.search("not-exist", PageRequest.of(0, 10));

            // then
            assertThat(result.getContent()).isEmpty();
        }

        @Test
        @DisplayName("검색 조건 없음 - 전체 조회")
        void search_all_when_no_condition() {
            // given
            List<Brand> brands = List.of(
                    BrandFixture.createBrand(),
                    BrandFixture.createBrand()
            );
            brandRepository.saveAll(brands);

            // when
            Page<BrandInfo> result = brandRepository.search(null, PageRequest.of(0, 10));

            // then
            assertThat(result.getTotalElements()).isEqualTo(2);
        }
    }
}
