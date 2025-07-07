package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.faillog.AsyncFailureLog;
import store.myproject.onlineshop.domain.faillog.FailureStatus;
import store.myproject.onlineshop.domain.faillog.JobType;
import store.myproject.onlineshop.repository.asyncFailureLog.AsyncFailureLogRepository;
import store.myproject.onlineshop.repository.recipemeta.RecipeMetaRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeMetaService {

    private final RecipeMetaRepository recipeMetaRepository;
    private final AsyncFailureLogRepository asyncFailureLogRepository;

    private static final int MAX_RETRY = 3;

    @Async(value = "recipeMetaExecutor")
    public void asyncIncreaseViewCnt(Long recipeMetaId) {
        asyncIncreaseViewCntRetry(recipeMetaId, 0);
    }

    @Transactional
    protected void asyncIncreaseViewCntRetry(Long recipeMetaId, int retryCount) {
        try {
            recipeMetaRepository.incrementViewCnt(recipeMetaId);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("조회수 증가 실패, 재시도 {}/{}: recipeMetaId={}", retryCount + 1, MAX_RETRY, recipeMetaId, e);
                asyncIncreaseViewCntRetry(recipeMetaId, retryCount + 1);
            } else {
                log.error("조회수 증가 최종 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
                saveFailureRecord(recipeMetaId, JobType.RECIPE_VIEW_COUNT_INCREMENT, e.getMessage());
            }
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncIncreaseLikeCnt(Long recipeMetaId) {
        asyncIncreaseLikeCntRetry(recipeMetaId, 0);
    }

    @Transactional
    protected void asyncIncreaseLikeCntRetry(Long recipeMetaId, int retryCount) {
        try {
            recipeMetaRepository.incrementLikeCnt(recipeMetaId);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("좋아요 수 증가 실패, 재시도 {}/{}: recipeMetaId={}", retryCount + 1, MAX_RETRY, recipeMetaId, e);
                asyncIncreaseLikeCntRetry(recipeMetaId, retryCount + 1);
            } else {
                log.error("좋아요 수 증가 최종 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
                saveFailureRecord(recipeMetaId, JobType.LIKE_COUNT_INCREMENT, e.getMessage());
            }
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncDecreaseLikeCnt(Long recipeMetaId) {
        asyncDecreaseLikeCntRetry(recipeMetaId, 0);
    }

    @Transactional
    protected void asyncDecreaseLikeCntRetry(Long recipeMetaId, int retryCount) {
        try {
            recipeMetaRepository.decrementLikeCnt(recipeMetaId);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("좋아요 수 감소 실패, 재시도 {}/{}: recipeMetaId={}", retryCount + 1, MAX_RETRY, recipeMetaId, e);
                asyncDecreaseLikeCntRetry(recipeMetaId, retryCount + 1);
            } else {
                log.error("좋아요 수 감소 최종 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
                saveFailureRecord(recipeMetaId, JobType.LIKE_COUNT_DECREMENT, e.getMessage());
            }
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncIncreaseReviewCnt(Long recipeMetaId) {
        asyncIncreaseReviewCntRetry(recipeMetaId, 0);
    }

    @Transactional
    protected void asyncIncreaseReviewCntRetry(Long recipeMetaId, int retryCount) {
        try {
            recipeMetaRepository.incrementReviewCnt(recipeMetaId);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("리뷰 수 증가 실패, 재시도 {}/{}: recipeMetaId={}", retryCount + 1, MAX_RETRY, recipeMetaId, e);
                asyncIncreaseReviewCntRetry(recipeMetaId, retryCount + 1);
            } else {
                log.error("리뷰 수 증가 최종 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
                saveFailureRecord(recipeMetaId, JobType.REVIEW_COUNT_INCREMENT, e.getMessage());
            }
        }
    }

    @Async(value = "recipeMetaExecutor")
    public void asyncDecreaseReviewCnt(Long recipeMetaId) {
        asyncDecreaseReviewCntRetry(recipeMetaId, 0);
    }

    @Transactional
    protected void asyncDecreaseReviewCntRetry(Long recipeMetaId, int retryCount) {
        try {
            recipeMetaRepository.decrementReviewCnt(recipeMetaId);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("리뷰 수 감소 실패, 재시도 {}/{}: recipeMetaId={}", retryCount + 1, MAX_RETRY, recipeMetaId, e);
                asyncDecreaseReviewCntRetry(recipeMetaId, retryCount + 1);
            } else {
                log.error("리뷰 수 감소 최종 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
                saveFailureRecord(recipeMetaId, JobType.REVIEW_COUNT_DECREMENT, e.getMessage());
            }
        }
    }

    private void saveFailureRecord(Long targetId, JobType jobType, String errorMessage) {
        AsyncFailureLog asyncFailureLog = AsyncFailureLog.builder()
                .jobType(jobType)
                .targetId(targetId)
                .status(FailureStatus.FAILED)
                .errorMessage(errorMessage)
                .build();

        asyncFailureLogRepository.save(asyncFailureLog);
    }
}
