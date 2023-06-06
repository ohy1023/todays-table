package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.dto.Response;
import store.myproject.onlineshop.domain.dto.customer.*;
import store.myproject.onlineshop.global.utils.CookieUtils;
import store.myproject.onlineshop.service.CustomerService;

@Slf4j
@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {

    private final CustomerService customerService;

    @Value("${access-token-maxage}")
    public int accessTokenMaxAge;
    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;

    @Operation(summary = "회원 가입")
    @PostMapping("/join")
    public Response<String> join(@Valid @RequestBody CustomerJoinRequest reqeust) {
        String email = customerService.join(reqeust);
        return Response.success("회원가입 성공");
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public Response<CustomerLoginResponse> login(@Valid @RequestBody CustomerLoginRequest customerLoginRequest, HttpServletRequest request,
                                                 HttpServletResponse response) {

        CustomerLoginResponse customerLoginResponse = customerService.login(customerLoginRequest);

        String accessToken = customerLoginResponse.getAccessToken();
        String refreshToken = customerLoginResponse.getRefreshToken();


        log.info("쿠키에 저장된 AccessToken :");
        log.info("Authorization = {};", accessToken);
        CookieUtils.addAccessTokenAtCookie(response, accessToken);

        log.info("쿠키에 저장된 RefreshToken :");
        log.info("Authorization-refresh= {}; Path=/; Secure; HttpOnly; Expires=DOW, DAY MONTH YEAR HH:MM:SS GMT;", refreshToken);
        CookieUtils.addRefreshTokenAtCookie(response, refreshToken);

        return Response.success(customerLoginResponse);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public Response<String> logout(@RequestBody CustomerTokenRequest customerTokenRequest, Authentication authentication) {
        String msg = customerService.logout(customerTokenRequest, authentication.getName());
        return Response.success(msg);
    }


    @Operation(summary = "회원 정보 수정")
    @PutMapping
    public Response<Long> modify(@RequestBody CustomerModifyRequest customerModifyRequest, Authentication authentication) {
        Long customerId = customerService.modifyUser(customerModifyRequest, authentication.getName());
        return Response.success(customerId);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public Response<Long> delete(Authentication authentication) {
        Long customerId = customerService.deleteUser(authentication.getName());
        return Response.success(customerId);
    }

    @Operation(summary = "이메일 중복 체크")
    @PostMapping("/check")
    public Response<String> check(@Valid @RequestBody CustomerCheckRequest customerCheckRequest) {
        String msg = customerService.userNameCheck(customerCheckRequest);
        return Response.success(msg);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public Response<CustomerLoginResponse> reissue(@RequestBody CustomerTokenRequest userTokenRequest, Authentication authentication) {
        CustomerLoginResponse customerLoginResponse = customerService.reissue(userTokenRequest, authentication.getName());
        return Response.success(customerLoginResponse);
    }
}