//package store.myproject.onlineshop.repository.membership;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
//import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.test.context.ActiveProfiles;
//import store.myproject.onlineshop.domain.customer.Level;
//import store.myproject.onlineshop.domain.membership.MemberShip;
//import store.myproject.onlineshop.fixture.MemberShipFixture;
//import store.myproject.onlineshop.global.config.TestConfig;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.*;
//
//
//@DataJpaTest
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
//@Import(TestConfig.class)
//@ActiveProfiles("test")
//class MemberShipRepositoryTest {
//
//    @Autowired
//    private MemberShipRepository memberShipRepository;
//
//
//    @Nested
//    @DisplayName("레벨 기반 멤버십 조회")
//    class findMemberShipByLevel {
//
//        @Test
//        @DisplayName("성공")
//        void find_membership_by_level_success() {
//            // given
//            MemberShip membership = MemberShipFixture.createBronzeMembership();
//            memberShipRepository.save(membership);
//
//            // when
//            Optional<MemberShip> result = memberShipRepository.findMemberShipByLevel(Level.BRONZE);
//
//            // then
//            assertThat(result.isPresent()).isTrue();
//            assertThat(result.get().getLevel()).isEqualTo(Level.BRONZE);
//            assertThat(result.get().getBaseline()).isEqualTo(membership.getBaseline());
//            assertThat(result.get().getDiscountRate()).isEqualTo(membership.getDiscountRate());
//
//        }
//
//        @Test
//        @DisplayName("실패 - 존재하지 않음")
//        void find_membership_by_level_fail() {
//            // when
//            Optional<MemberShip> result = memberShipRepository.findMemberShipByLevel(Level.BRONZE);
//
//            // then
//            assertThat(result.isEmpty()).isTrue();
//        }
//
//    }
//
//    @Nested
//    @DisplayName("해당 레벨 존재 여부")
//    class existsByLevel {
//        @Test
//        @DisplayName("존재 O")
//        void exists_by_level_true() {
//            // given
//            MemberShip membership = MemberShipFixture.createBronzeMembership();
//            memberShipRepository.save(membership);
//
//            // when
//            boolean result = memberShipRepository.existsByLevel(Level.BRONZE);
//
//            assertThat(result).isTrue();
//
//        }
//
//        @Test
//        @DisplayName("존재 X")
//        void exists_by_level_false() {
//            // given
//            MemberShip membership = MemberShipFixture.createBronzeMembership();
//            memberShipRepository.save(membership);
//
//            // when
//            boolean result = memberShipRepository.existsByLevel(Level.SILVER);
//
//            assertThat(result).isFalse();
//        }
//    }
//
//    @Nested
//    @DisplayName("기준 금액이 가장 낮은 멤버십 조회")
//    class findTopByLowestBaseline {
//
//        @Test
//        @DisplayName("성공")
//        void find_lowest_baseline_success() {
//            // given
//            MemberShip bronze = MemberShipFixture.createBronzeMembership(); // baseline: 0
//            MemberShip silver = MemberShipFixture.createSilverMembership(); // baseline: 100000
//
//            memberShipRepository.saveAll(List.of(silver, bronze));
//
//            // when
//            List<MemberShip> result = memberShipRepository.findTopByLowestBaseline(PageRequest.of(0,1));
//
//            // then
//            assertThat(result.size()).isEqualTo(1);
//            assertThat(result.get(0).getBaseline()).isEqualTo(bronze.getBaseline());
//        }
//
//        @Test
//        @DisplayName("실패 - 멤버십이 없을 경우")
//        void find_lowest_baseline_empty() {
//            // when
//            List<MemberShip> result = memberShipRepository.findTopByLowestBaseline(PageRequest.of(0,1));
//
//            // then
//            assertThat(result).isEmpty();
//        }
//    }
//
//    @Nested
//    @DisplayName("UUID 기반 멤버십 조회")
//    class FindByUuid {
//
//        @Test
//        @DisplayName("성공")
//        void find_by_uuid_success() {
//            // given
//            MemberShip membership = MemberShipFixture.createBronzeMembership();
//            MemberShip saved = memberShipRepository.save(membership);
//
//            // when
//            Optional<MemberShip> result = memberShipRepository.findByUuid(saved.getUuid());
//
//            // then
//            assertThat(result).isPresent();
//            assertThat(result.get().getUuid()).isEqualTo(saved.getUuid());
//            assertThat(result.get().getLevel()).isEqualTo(saved.getLevel());
//        }
//
//        @Test
//        @DisplayName("실패 - 존재하지 않음")
//        void find_by_uuid_fail() {
//            // given
//            UUID randomUuid = UUID.randomUUID();
//
//            // when
//            Optional<MemberShip> result = memberShipRepository.findByUuid(randomUuid);
//
//            // then
//            assertThat(result).isEmpty();
//        }
//    }
//
//
//}