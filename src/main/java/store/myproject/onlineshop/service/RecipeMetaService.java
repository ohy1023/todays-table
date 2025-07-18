package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.faillog.AsyncFailureLog;
import store.myproject.onlineshop.domain.faillog.FailureStatus;
import store.myproject.onlineshop.domain.faillog.JobType;
import store.myproject.onlineshop.repository.asyncFailureLog.AsyncFailureLogRepository;
import store.myproject.onlineshop.repository.recipe.RecipeRepository;
import store.myproject.onlineshop.repository.recipemeta.RecipeMetaRepository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecipeMetaService {

    private final RecipeRepository recipeRepository;
    private final RecipeMetaRepository recipeMetaRepository;
    private final AsyncFailureLogRepository asyncFailureLogRepository;

    private static final int MAX_RETRY = 3;

    @Retryable(
            value = Exception.class,
            maxAttempts = MAX_RETRY,
            backoff = @Backoff(delay = 200, multiplier = 2))
    @Async("recipeMetaExecutor")
    @Transactional
    public void asyncIncreaseViewCnt(Long recipeMetaId) {
        recipeMetaRepository.incrementViewCnt(recipeMetaId);
    }

    @Recover
    public void recoverIncreaseViewCnt(Exception e, Long recipeMetaId) {
        log.error("조회수 증가 재시도 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
        saveAsyncFailureLog(JobType.RECIPE_VIEW_COUNT_INCREMENT, recipeMetaId, e);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = MAX_RETRY,
            backoff = @Backoff(delay = 200, multiplier = 2))
    @Async("recipeMetaExecutor")
    @Transactional
    public void asyncIncreaseLikeCnt(Long recipeMetaId) {
        recipeMetaRepository.incrementLikeCnt(recipeMetaId);
    }

    @Recover
    public void recoverIncreaseLikeCnt(Exception e, Long recipeMetaId) {
        log.error("좋아요 수 증가 재시도 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
        saveAsyncFailureLog(JobType.LIKE_COUNT_INCREMENT, recipeMetaId, e);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = MAX_RETRY,
            backoff = @Backoff(delay = 200, multiplier = 2))
    @Async("recipeMetaExecutor")
    @Transactional
    public void asyncDecreaseLikeCnt(Long recipeMetaId) {
        recipeMetaRepository.decrementLikeCnt(recipeMetaId);
    }

    @Recover
    public void recoverDecreaseLikeCnt(Exception e, Long recipeMetaId) {
        log.error("좋아요 수 감소 재시도 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
        saveAsyncFailureLog(JobType.LIKE_COUNT_DECREMENT, recipeMetaId, e);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = MAX_RETRY,
            backoff = @Backoff(delay = 200, multiplier = 2))
    @Async("recipeMetaExecutor")
    @Transactional
    public void asyncIncreaseReviewCnt(Long recipeMetaId) {
        recipeMetaRepository.incrementReviewCnt(recipeMetaId);
    }

    @Recover
    public void recoverIncreaseReviewCnt(Exception e, Long recipeMetaId) {
        log.error("리뷰 수 증가 재시도 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
        saveAsyncFailureLog(JobType.REVIEW_COUNT_INCREMENT, recipeMetaId, e);
    }

    @Retryable(
            value = Exception.class,
            maxAttempts = MAX_RETRY,
            backoff = @Backoff(delay = 200, multiplier = 2))
    @Async("recipeMetaExecutor")
    @Transactional
    public void asyncDecreaseReviewCnt(Long recipeMetaId) {
        recipeMetaRepository.decrementReviewCnt(recipeMetaId);
    }

    @Recover
    public void recoverDecreaseReviewCnt(Exception e, Long recipeMetaId) {
        log.error("리뷰 수 감소 재시도 실패: recipeMetaId={}, error={}", recipeMetaId, e.getMessage(), e);
        saveAsyncFailureLog(JobType.REVIEW_COUNT_DECREMENT, recipeMetaId, e);
    }

    private void saveAsyncFailureLog(JobType jobType, Long targetId, Exception e) {
        AsyncFailureLog log = AsyncFailureLog.builder()
                .jobType(jobType)
                .targetId(targetId)
                .amount(null)
                .errorMessage(getStackTraceString(e))
                .failureStatus(FailureStatus.FAILED)
                .build();

        asyncFailureLogRepository.save(log);
    }

    private String getStackTraceString(Exception e) {
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }
}
