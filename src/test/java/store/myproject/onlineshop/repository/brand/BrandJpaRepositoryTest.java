package store.myproject.onlineshop.repository.brand;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.global.config.TestConfig;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
class BrandJpaRepositoryTest {

    @Autowired
    BrandJpaRepository brandJpaRepository;

    @Nested
    @DisplayName("브랜드 이름으로 조회")
    class FindByName {

        @Test
        @DisplayName("존재하는 브랜드 이름 조회 성공")
        void find_brand_by_name_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandJpaRepository.save(brand);

            // when
            Optional<Brand> result = brandJpaRepository.findBrandByName(brand.getName());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 이름 조회")
        void find_brand_by_name_fail() {
            // when
            Optional<Brand> result = brandJpaRepository.findBrandByName("not-exist");

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
            brandJpaRepository.save(brand);

            // when
            boolean exists = brandJpaRepository.existsByName(brand.getName());

            // then
            assertThat(exists).isTrue();
        }

        @Test
        @DisplayName("브랜드 존재하지 않음")
        void exists_false() {
            // when
            boolean exists = brandJpaRepository.existsByName("non-exist");

            // then
            assertThat(exists).isFalse();
        }
    }

    @Nested
    @DisplayName("UUID 조회")
    class FindByUuid {

        @Test
        @DisplayName("존재하는 브랜드 UUID 조회 성공")
        void find_brand_by_uuid_success() {
            // given
            Brand brand = BrandFixture.createBrand();
            brandJpaRepository.save(brand);

            // when
            Optional<Brand> result = brandJpaRepository.findByUuid(brand.getUuid());

            // then
            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo(brand.getName());
        }

        @Test
        @DisplayName("존재하지 않는 브랜드 UUID 조회")
        void find_brand_by_uuid_fail() {
            // when
            Optional<Brand> result = brandJpaRepository.findByUuid(UUID.randomUUID());

            // then
            assertThat(result).isEmpty();
        }
    }

}
