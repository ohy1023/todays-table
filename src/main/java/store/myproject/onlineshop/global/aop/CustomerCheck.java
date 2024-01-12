package store.myproject.onlineshop.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import store.myproject.onlineshop.domain.corporation.repository.CorporationRepository;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.exception.AppException;

import java.util.stream.Stream;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Aspect
@Component
@RequiredArgsConstructor
public class CustomerCheck {

    private final CustomerRepository customerRepository;
    private final CorporationRepository corporationRepository;

    @Around(value = "execution(* store.myproject.onlineshop.controller..*.*(..))")
    public Object validAdviceHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        Stream.of(joinPoint.getArgs())
                .filter(arg -> arg instanceof Authentication)
                .map(arg -> (Authentication) arg)
                .findAny()
                .ifPresent((authentication) -> {
                    String identifier = authentication.getName();

                    if (isValidEmail(identifier)) {
                        customerRepository.findByEmail(identifier)
                                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND));
                    } else {
                        corporationRepository.findByRegistrationNumber(identifier)
                                .orElseThrow(() -> new AppException(CORPORATION_NOT_FOUND));
                    }
                });

        return joinPoint.proceed();
    }

    private boolean isValidEmail(String identifier) {
        return identifier.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$");
    }
}