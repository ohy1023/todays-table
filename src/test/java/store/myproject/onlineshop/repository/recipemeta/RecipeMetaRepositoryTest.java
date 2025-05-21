package store.myproject.onlineshop.repository.recipemeta;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import store.myproject.onlineshop.domain.recipemeta.RecipeMeta;
import store.myproject.onlineshop.global.config.TestConfig;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestConfig.class)
@ActiveProfiles("test")
class RecipeMetaRepositoryTest {

    @Autowired
    private RecipeMetaRepository recipeMetaRepository;

    @Autowired
    private EntityManager em;

    @Nested
    @DisplayName("RecipeMeta 수치 업데이트")
    class UpdateCounts {

        @Test
        @DisplayName("조회수 증가 성공")
        void increment_view_cnt_success() {
            // given
            RecipeMeta recipeMeta = recipeMetaRepository.save(RecipeMeta.builder()
                            .viewCnt(0L)
                            .likeCnt(0L)
                            .reviewCnt(0L)
                    .build());

            // when
            recipeMetaRepository.incrementViewCnt(recipeMeta.getId());
            em.flush();
            em.clear();

            RecipeMeta updated = recipeMetaRepository.findById(recipeMeta.getId()).orElseThrow();

            // then
            assertThat(updated.getViewCnt()).isEqualTo(1);
        }

        @Test
        @DisplayName("좋아요 수 증가 성공")
        void increment_like_cnt_success() {
            // given
            RecipeMeta recipeMeta = recipeMetaRepository.save(RecipeMeta.builder()
                    .viewCnt(0L)
                    .likeCnt(0L)
                    .reviewCnt(0L)
                    .build());

            // when
            recipeMetaRepository.incrementLikeCnt(recipeMeta.getId());
            em.flush();
            em.clear();

            RecipeMeta updated = recipeMetaRepository.findById(recipeMeta.getId()).orElseThrow();

            // then
            assertThat(updated.getLikeCnt()).isEqualTo(1);
        }

        @Test
        @DisplayName("좋아요 수 감소 성공")
        void decrement_like_cnt_success() {
            // given
            RecipeMeta recipeMeta = recipeMetaRepository.save(RecipeMeta.builder()
                    .viewCnt(0L)
                    .likeCnt(1L)
                    .reviewCnt(0L)
                    .build());

            // when
            recipeMetaRepository.decrementLikeCnt(recipeMeta.getId());
            em.flush();
            em.clear();

            RecipeMeta updated = recipeMetaRepository.findById(recipeMeta.getId()).orElseThrow();

            // then
            assertThat(updated.getLikeCnt()).isEqualTo(0);
        }

        @Test
        @DisplayName("리뷰 수 증가 성공")
        void increment_review_cnt_success() {
            // given
            RecipeMeta recipeMeta = recipeMetaRepository.save(RecipeMeta.builder()
                    .viewCnt(0L)
                    .likeCnt(0L)
                    .reviewCnt(0L)
                    .build());

            // when
            recipeMetaRepository.incrementReviewCnt(recipeMeta.getId());
            em.flush();
            em.clear();

            RecipeMeta updated = recipeMetaRepository.findById(recipeMeta.getId()).orElseThrow();

            // then
            assertThat(updated.getReviewCnt()).isEqualTo(1);
        }

        @Test
        @DisplayName("리뷰 수 감소 성공")
        void decrement_review_cnt_success() {
            // given
            RecipeMeta recipeMeta = recipeMetaRepository.save(RecipeMeta.builder()
                    .viewCnt(0L)
                    .likeCnt(0L)
                    .reviewCnt(1L)
                    .build());

            // when
            recipeMetaRepository.decrementReviewCnt(recipeMeta.getId());
            em.flush();
            em.clear();

            RecipeMeta updated = recipeMetaRepository.findById(recipeMeta.getId()).orElseThrow();

            // then
            assertThat(updated.getReviewCnt()).isEqualTo(0);
        }
    }
}
