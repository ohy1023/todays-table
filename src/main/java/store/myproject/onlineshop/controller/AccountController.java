package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.service.AccountService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    @Tag(name = "Account", description = "계좌 API")
    @Operation(summary = "계좌 조회")
    @GetMapping
    public Response<AccountInfo> getAccount(Authentication authentication) {

        String email = authentication.getName();
        log.info("email:{}", email);

        AccountInfo response = accountService.findAccount(email);

        return Response.success(response);
    }

    @Tag(name = "Account", description = "계좌 API")
    @Operation(summary = "계좌 등록")
    @PostMapping
    public Response<AccountCreateResponse> createAccount(@Valid @RequestBody AccountCreateRequest request, Authentication authentication) {

        String email = authentication.getName();
        log.info("email:{}", email);

        AccountCreateResponse response = accountService.saveAccount(request, email);

        return Response.success(response);
    }

    @Tag(name = "Account", description = "계좌 API")
    @Operation(summary = "계좌 수정")
    @PatchMapping
    public Response<AccountUpdateResponse> modifyAccount(@Valid @RequestBody AccountUpdateRequest request, Authentication authentication) {

        String email = authentication.getName();
        log.info("email:{}", email);

        AccountUpdateResponse response = accountService.updateAccount(request, email);

        return Response.success(response);
    }

    @Tag(name = "Account", description = "계좌 API")
    @Operation(summary = "계좌 삭제")
    @DeleteMapping
    public Response<AccountDeleteResponse> removeAccount(Authentication authentication) {

        String email = authentication.getName();
        log.info("email:{}", email);

        AccountDeleteResponse response = accountService.deleteAccount(email);

        return Response.success(response);
    }

}
