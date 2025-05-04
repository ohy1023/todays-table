//package store.myproject.onlineshop.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import store.myproject.onlineshop.repository.recipemeta.RecipeMetaRepository;
//
//import static org.mockito.BDDMockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class RecipeMetaServiceTest {
//
//    @Mock
//    private RecipeMetaRepository recipeMetaRepository;
//
//    @InjectMocks
//    private RecipeMetaService recipeMetaService;
//
//    @Test
//    @DisplayName("조회수 증가 성공")
//    void async_increase_view_count_success() {
//        // given
//        Long recipeMetaId = 1L;
//
//        // when
//        recipeMetaService.asyncIncreaseViewCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).incrementViewCnt(recipeMetaId);
//    }
//
//
//    @Test
//    @DisplayName("조회수 증가 실패 - Exception 발생해도 정상 종료")
//    void async_increase_view_count_fail() {
//        // given
//        Long recipeMetaId = 1L;
//        willThrow(new RuntimeException("DB 오류"))
//                .given(recipeMetaRepository).incrementViewCnt(recipeMetaId);
//
//        // when
//        recipeMetaService.asyncIncreaseViewCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).incrementViewCnt(recipeMetaId);
//    }
//
//    @Test
//    @DisplayName("좋아요 수 증가 성공")
//    void async_increase_like_count_success() {
//        // given
//        Long recipeMetaId = 1L;
//
//        // when
//        recipeMetaService.asyncIncreaseLikeCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).incrementLikeCnt(recipeMetaId);
//    }
//
//    @Test
//    @DisplayName("좋아요 수 증가 실패 - Exception 발생해도 정상 종료")
//    void async_increase_like_count_fail() {
//        // given
//        Long recipeMetaId = 1L;
//        willThrow(new RuntimeException("DB 오류"))
//                .given(recipeMetaRepository).incrementLikeCnt(recipeMetaId);
//
//        // when
//        recipeMetaService.asyncIncreaseLikeCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).incrementLikeCnt(recipeMetaId);
//    }
//
//
//    @Test
//    @DisplayName("좋아요 수 감소 성공")
//    void async_decrease_like_count_success() {
//        // given
//        Long recipeMetaId = 1L;
//
//        // when
//        recipeMetaService.asyncDecreaseLikeCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).decrementLikeCnt(recipeMetaId);
//    }
//
//    @Test
//    @DisplayName("좋아요 수 감소 실패 - Exception 발생해도 정상 종료")
//    void async_decrease_like_count_fail() {
//        // given
//        Long recipeMetaId = 1L;
//        willThrow(new RuntimeException("DB 오류"))
//                .given(recipeMetaRepository).decrementLikeCnt(recipeMetaId);
//
//        // when
//        recipeMetaService.asyncDecreaseLikeCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).decrementLikeCnt(recipeMetaId);
//    }
//
//    @Test
//    @DisplayName("리뷰 수 증가 성공")
//    void async_increase_review_count_success() {
//        // given
//        Long recipeMetaId = 1L;
//
//        // when
//        recipeMetaService.asyncIncreaseReviewCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).incrementReviewCnt(recipeMetaId);
//    }
//
//    @Test
//    @DisplayName("리뷰 수 증가 실패 - Exception 발생해도 정상 종료")
//    void async_increase_review_count_fail() {
//        // given
//        Long recipeMetaId = 1L;
//        willThrow(new RuntimeException("DB 오류"))
//                .given(recipeMetaRepository).incrementReviewCnt(recipeMetaId);
//
//        // when
//        recipeMetaService.asyncIncreaseReviewCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).incrementReviewCnt(recipeMetaId);
//    }
//
//    @Test
//    @DisplayName("리뷰 수 감소 성공")
//    void async_decrease_review_count_success() {
//        // given
//        Long recipeMetaId = 1L;
//
//        // when
//        recipeMetaService.asyncDecreaseReviewCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).decrementReviewCnt(recipeMetaId);
//    }
//
//
//    @Test
//    @DisplayName("리뷰 수 감소 실패 - Exception 발생해도 정상 종료")
//    void async_decrease_review_count_fail() {
//        // given
//        Long recipeMetaId = 1L;
//        willThrow(new RuntimeException("DB 오류"))
//                .given(recipeMetaRepository).decrementReviewCnt(recipeMetaId);
//
//        // when
//        recipeMetaService.asyncDecreaseReviewCnt(recipeMetaId);
//
//        // then
//        then(recipeMetaRepository).should(times(1)).decrementReviewCnt(recipeMetaId);
//    }
//
//
//}
