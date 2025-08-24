package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.faillog.AsyncFailureLog;
import store.myproject.onlineshop.domain.faillog.FailureStatus;
import store.myproject.onlineshop.domain.faillog.JobType;
import store.myproject.onlineshop.domain.faillog.AsyncFailureLogRepository;
import store.myproject.onlineshop.domain.customer.CustomerRepository;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AsyncCustomerService {

    private final CustomerRepository customerRepository;
    private final AsyncFailureLogRepository asyncFailureLogRepository;

    private static final int MAX_RETRY = 3;

    @Async(value = "monthlyPurchaseExecutor")
    public void addMonthlyPurchaseAmount(Long customerId, BigDecimal amountToIncrease) {
        addMonthlyPurchaseAmountRetry(customerId, amountToIncrease, 0);
    }

    @Transactional
    protected void addMonthlyPurchaseAmountRetry(Long customerId, BigDecimal amountToIncrease, int retryCount) {
        try {
            customerRepository.incrementMonthlyPurchaseAmount(customerId, amountToIncrease);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("addMonthlyPurchaseAmount 실패, 재시도 {}/{}: customerId={}, amount={}",
                        retryCount + 1, MAX_RETRY, customerId, amountToIncrease);
                addMonthlyPurchaseAmountRetry(customerId, amountToIncrease, retryCount + 1);
            } else {
                log.error("addMonthlyPurchaseAmount 최종 실패: customerId={}, amount={}, error={}",
                        customerId, amountToIncrease, e.getMessage());
                saveFailureRecord(customerId, amountToIncrease, JobType.CUSTOMER_ORDER_AMOUNT_INCREMENT, e.getMessage());
            }
        }
    }

    @Async(value = "monthlyPurchaseExecutor")
    public void subtractMonthlyPurchaseAmount(Long customerId, BigDecimal amountToDecrease) {
        subtractMonthlyPurchaseAmountRetry(customerId, amountToDecrease, 0);
    }

    @Transactional
    protected void subtractMonthlyPurchaseAmountRetry(Long customerId, BigDecimal amountToDecrease, int retryCount) {
        try {
            customerRepository.decrementMonthlyPurchaseAmount(customerId, amountToDecrease);
        } catch (Exception e) {
            if (retryCount < MAX_RETRY) {
                log.warn("subtractMonthlyPurchaseAmount 실패, 재시도 {}/{}: customerId={}, amount={}",
                        retryCount + 1, MAX_RETRY, customerId, amountToDecrease);
                subtractMonthlyPurchaseAmountRetry(customerId, amountToDecrease, retryCount + 1);
            } else {
                log.error("subtractMonthlyPurchaseAmount 최종 실패: customerId={}, amount={}, error={}",
                        customerId, amountToDecrease, e.getMessage());
                saveFailureRecord(customerId, amountToDecrease, JobType.CUSTOMER_ORDER_AMOUNT_DECREMENT, e.getMessage());
            }
        }
    }

    private void saveFailureRecord(Long customerId, BigDecimal amount, JobType jobType, String errorMessage) {
        AsyncFailureLog asyncFailureLog = AsyncFailureLog.builder()
                .jobType(jobType)
                .failureStatus(FailureStatus.FAILED)
                .targetId(customerId)
                .amount(amount)
                .errorMessage(errorMessage)
                .build();

        asyncFailureLogRepository.save(asyncFailureLog);
    }
}
