package store.myproject.onlineshop.global.aop;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
import store.myproject.onlineshop.exception.AppException;

import java.util.stream.Stream;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Aspect
@Component
@RequiredArgsConstructor
public class UserCheck {

    private final CustomerRepository customerRepository;

    @Around(value = "execution(* store.myproject.onlineshop.controller..*.*(..))")
    public Object validAdviceHandler(ProceedingJoinPoint joinPoint) throws Throwable {

        Stream.of(joinPoint.getArgs())
                .filter(arg -> arg instanceof Authentication)
                .map(arg -> (Authentication) arg)
                .findAny()
                .ifPresent((authentication) ->
                        customerRepository.findByEmail(authentication.getName())
                                .orElseThrow(() -> new AppException(CUSTOMER_NOT_FOUND))
                );

        return joinPoint.proceed();
    }
}