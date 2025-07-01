package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.repository.customer.CustomerRepository;

import java.math.BigDecimal;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AsyncCustomerService {

    private final CustomerRepository customerRepository;

    @Async(value = "monthlyPurchaseExecutor")
    public void addMonthlyPurchaseAmount(Long customerId, BigDecimal amountToIncrease) {
        try {
            customerRepository.incrementMonthlyPurchaseAmount(customerId, amountToIncrease);
        } catch (Exception e) {
            log.error("이번달 구매 금액 증가 실패!");
        }
    }

    @Async(value = "monthlyPurchaseExecutor")
    public void subtractMonthlyPurchaseAmount(Long customerId, BigDecimal amountToDecrease) {
        try {
            customerRepository.decrementMonthlyPurchaseAmount(customerId, amountToDecrease);
        } catch (Exception e) {
            log.error("주문 취소로 인한 금액 감소 실패!");
        }
    }
}