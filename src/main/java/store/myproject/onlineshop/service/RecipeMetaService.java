package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.repository.recipemeta.RecipeMetaRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeMetaService {

    private final RecipeMetaRepository recipeMetaRepository;

    @Async(value = "recipeMetaExecutor")
    public void asyncIncreaseViewCnt(Long recipeMetaId) {
        try {
            recipeMetaRepository.incrementViewCnt(recipeMetaId);
        } catch (Exception e) {
            log.error("조회수 증가 실패", e);
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncIncreaseLikeCnt(Long recipeMetaId) {
        try {
            recipeMetaRepository.incrementLikeCnt(recipeMetaId);
        } catch (Exception e) {
            log.error("좋아요 수 증가 실패", e);
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncDecreaseLikeCnt(Long recipeMetaId) {
        try {
            recipeMetaRepository.decrementLikeCnt(recipeMetaId);
        } catch (Exception e) {
            log.error("좋아요 수 감소 실패", e);
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncIncreaseReviewCnt(Long recipeMetaId) {
        try {
            recipeMetaRepository.incrementReviewCnt(recipeMetaId);
        } catch (Exception e) {
            log.error("리뷰 수 증가 실패", e);
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncDecreaseReviewCnt(Long recipeMetaId) {
        try {
            recipeMetaRepository.decrementReviewCnt(recipeMetaId);
        } catch (Exception e) {
            log.error("리뷰 수 감소 실패", e);
        }
    }
}
