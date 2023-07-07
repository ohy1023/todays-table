package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.global.utils.CookieUtils;
import store.myproject.onlineshop.service.CustomerService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "회원 API")
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
    @PatchMapping
    public Response<Long> modify(@RequestBody CustomerModifyRequest customerModifyRequest, Authentication authentication) {
        Long customerId = customerService.modify(customerModifyRequest, authentication.getName());
        return Response.success(customerId);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping
    public Response<Long> delete(Authentication authentication) {
        Long customerId = customerService.delete(authentication.getName());
        return Response.success(customerId);
    }

    @Operation(summary = "이메일 중복 체크")
    @PostMapping("/email")
    public Response<String> emailCheck(@Valid @RequestBody CustomerEmailCheckRequest request) {
        String msg = customerService.emailCheck(request);
        return Response.success(msg);
    }

    @Operation(summary = "닉네임 중복 체크")
    @PostMapping("/nickname")
    public Response<String> nickNameCheck(@Valid @RequestBody CustomerNickNameCheckRequest request) {
        String msg = customerService.nickNameCheck(request);
        return Response.success(msg);
    }

    @Operation(summary = "토큰 재발급")
    @PostMapping("/reissue")
    public Response<CustomerLoginResponse> reissue(@RequestBody CustomerTokenRequest userTokenRequest, Authentication authentication) {
        CustomerLoginResponse customerLoginResponse = customerService.reissue(userTokenRequest, authentication.getName());
        return Response.success(customerLoginResponse);
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping
    public Response<CustomerInfoResponse> getInfo(Authentication authentication) {
        String email = authentication.getName();
        log.info("userEmail : {}", email);

        CustomerInfoResponse customerInfoResponse = customerService.getInfo(email);

        return Response.success(customerInfoResponse);
    }


    @Operation(summary = "임시 비밀번호 발급")
    @PutMapping("/password")
    public Response<String> findPassword(@Valid @RequestBody CustomerTempPasswordRequest request){
        customerService.setTempPassword(request);
        return Response.success("ok");
    }

    @Operation(summary = "관리자 권한 부여")
    @PutMapping("/admin")
    public Response<MessageResponse> changeRole(Authentication authentication) {

        MessageResponse response = customerService.settingAdmin(authentication);

        return Response.success(response);
    }

    @Operation(summary = "멤버쉽 등급 변경")
    @PutMapping("/membership")
    public Response<MessageResponse> changeGrade(Authentication authentication) {

        MessageResponse response = customerService.changeMemberShip(authentication);

        return Response.success(response);
    }



}