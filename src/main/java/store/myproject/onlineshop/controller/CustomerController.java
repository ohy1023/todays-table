package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.dto.common.Response;
import store.myproject.onlineshop.dto.cusotmer.*;
import store.myproject.onlineshop.global.utils.CookieUtils;
import store.myproject.onlineshop.service.CustomerService;

import java.net.URI;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/customers")
@Tag(name = "Customer", description = "회원 API")
public class CustomerController {

    private final CustomerService customerService;

    @Operation(summary = "회원 가입", description = "새로운 회원 정보를 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원 가입 성공"),
            @ApiResponse(responseCode = "400", description = "멤버쉽 없음"),
            @ApiResponse(responseCode = "409", description = "중복된 이메일 또는 닉네임 존재")
    })
    @PostMapping("/join")
    public ResponseEntity<Response<MessageResponse>> join(@Valid @RequestBody CustomerJoinRequest request) {
        MessageResponse response = customerService.registerCustomer(request);
        URI location = URI.create("/api/v1/customers");
        return ResponseEntity.created(location).body(Response.success(response));
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 로그인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "이메일 또는 비밀번호가 올바르지 않음")
    })
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@Valid @RequestBody CustomerLoginRequest customerLoginRequest,
                                                         HttpServletResponse response) {
        LoginResponse loginResponse = customerService.login(customerLoginRequest);

        CookieUtils.addAccessTokenAtCookie(response, loginResponse.getAccessToken());
        CookieUtils.addRefreshTokenAtCookie(response, loginResponse.getRefreshToken());

        return ResponseEntity.ok(Response.success(loginResponse));
    }

    @Operation(summary = "로그아웃", description = "현재 로그인한 사용자 로그아웃")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
            @ApiResponse(responseCode = "400", description = "잘못된 토큰 형식")
    })
    @PostMapping("/logout")
    public ResponseEntity<Response<MessageResponse>> logout(@Valid @RequestBody TokenRequest tokenRequest,
                                                            Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = customerService.logout(tokenRequest, email);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "회원 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "회원 정보 수정 성공"),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음")
    })
    @PutMapping
    public ResponseEntity<Response<MessageResponse>> modify(@RequestBody CustomerModifyRequest customerModifyRequest,
                                                            Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = customerService.updateCustomerInfo(customerModifyRequest, email);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 탈퇴 처리합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 정보 없음")
    })
    @DeleteMapping
    public ResponseEntity<Void> delete(Authentication authentication) {
        String email = authentication.getName();
        customerService.deleteCustomer(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "이메일 중복 체크", description = "이메일이 이미 사용 중인지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 이메일"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 이메일")
    })
    @PostMapping("/email")
    public ResponseEntity<Response<MessageResponse>> emailCheck(@Valid @RequestBody CustomerEmailCheckRequest request) {
        MessageResponse response = customerService.checkEmail(request);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "닉네임 중복 체크", description = "닉네임이 이미 사용 중인지 확인합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용 가능한 닉네임"),
            @ApiResponse(responseCode = "409", description = "이미 사용 중인 닉네임")
    })
    @PostMapping("/nickname")
    public ResponseEntity<Response<MessageResponse>> nickNameCheck(@Valid @RequestBody CustomerNickNameCheckRequest request) {
        MessageResponse response = customerService.checkNickName(request);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "토큰 재발급", description = "만료된 Access Token을 Refresh Token으로 재발급합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "재발급 성공"),
            @ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않음")
    })
    @PostMapping("/reissue")
    public ResponseEntity<Response<LoginResponse>> reissue(@Valid @RequestBody TokenRequest userTokenRequest,
                                                           Authentication authentication) {
        String info = authentication.getName();
        LoginResponse loginResponse = customerService.reissueToken(userTokenRequest, info);
        return ResponseEntity.ok(Response.success(loginResponse));
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping
    public ResponseEntity<Response<CustomerInfoResponse>> getInfo(Authentication authentication) {
        String email = authentication.getName();
        CustomerInfoResponse customerInfoResponse = customerService.getCustomerInfo(email);
        return ResponseEntity.ok(Response.success(customerInfoResponse));
    }

    @Operation(summary = "임시 비밀번호 발급")
    @PutMapping("/temp-password")
    public ResponseEntity<Response<CustomerTempPasswordResponse>> findPassword(@Valid @RequestBody CustomerTempPasswordRequest request) {
        CustomerTempPasswordResponse response = customerService.sendTempPassword(request);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "비밀번호 변경")
    @PutMapping("/password")
    public ResponseEntity<Response<MessageResponse>> changePassword(@Valid @RequestBody CustomerChangePasswordRequest request,
                                                                    Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = customerService.updatePassword(request, email);
        return ResponseEntity.ok(Response.success(response));
    }
}
