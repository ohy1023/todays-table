package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;

import static store.myproject.onlineshop.exception.ErrorCode.EMAIL_NOT_FOUND;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "accounts")
    public AccountInfo findAccount(String email) {

        Customer customer = findCustomerByEmail(email);

        return customer.toAccountInfo();
    }

    public AccountCreateResponse saveAccount(AccountCreateRequest request, String email) {

        Customer customer = findCustomerByEmail(email);

        customer.registerAccount(request);

        return customer.toAccountCreateResponse();
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public AccountUpdateResponse updateAccount(AccountUpdateRequest request, String email) {

        Customer customer = findCustomerByEmail(email);

        customer.updateAccount(request);

        return customer.toAccountUpdateResponse();
    }

    @CacheEvict(value = "accounts", allEntries = true)
    public AccountDeleteResponse deleteAccount(String email) {
        Customer customer = findCustomerByEmail(email);
        customer.deleteAccount();

        return customer.toAccountDeleteResponse();
    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() ->
                new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));
    }

}
