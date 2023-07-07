package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.account.Account;
import store.myproject.onlineshop.domain.account.dto.*;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    @Cacheable(value = "accounts")
    public AccountDto findAccount(String email) {

        Customer customer = findCustomerByEmail(email);

        return customer.getAccount().toAccountDto();
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

    public AccountDto plus(AccountDepositRequest request, String email) {
        Customer customer = findCustomerByEmail(email);

        Account account = customer.getAccount();

        if (account == null) {
            throw new AppException(ACCOUNT_NOT_FOUND, ACCOUNT_NOT_FOUND.getMessage());
        }

        account.plusMyAssets(request.getDepositPrice());

        return account.toAccountDto();

    }

    public AccountDto minus(AccountWithdrawRequest request, String email) {
        Customer customer = findCustomerByEmail(email);

        Account account = customer.getAccount();

        if (account == null) {
            throw new AppException(ACCOUNT_NOT_FOUND, ACCOUNT_NOT_FOUND.getMessage());
        }

        if (account.getMyAssets().compareTo(request.getWithdrawPrice()) < 0) {
            throw new AppException(WITHDRAW_BAD_REQUEST, WITHDRAW_BAD_REQUEST.getMessage());
        }

        account.minusMyAssets(request.getWithdrawPrice());

        return account.toAccountDto();

    }

    @Transactional(readOnly = true)
    public Customer findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email).orElseThrow(() ->
                new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));
    }

}
