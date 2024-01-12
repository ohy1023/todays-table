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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.corporation.dto.CorporationJoinRequest;
import store.myproject.onlineshop.domain.corporation.dto.CorporationLoginRequest;
import store.myproject.onlineshop.domain.customer.dto.LoginResponse;
import store.myproject.onlineshop.domain.customer.dto.TokenRequest;
import store.myproject.onlineshop.global.utils.CookieUtils;
import store.myproject.onlineshop.service.CorporationService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/corporations")
@Tag(name = "Corporation", description = "기업 API")
public class CorporationController {

    private final CorporationService corporationService;

    @Value("${access-token-maxage}")
    public int accessTokenMaxAge;
    @Value("${refresh-token-maxage}")
    public int refreshTokenMaxAge;

    @Operation(summary = "회원 가입")
    @PostMapping("/join")
    public Response<MessageResponse> join(@Valid @RequestBody CorporationJoinRequest reqeust) {
        MessageResponse response = corporationService.join(reqeust);
        return Response.success(response);
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    public Response<LoginResponse> login(@Valid @RequestBody CorporationLoginRequest corporationLoginRequest, HttpServletRequest request,
                                         HttpServletResponse response) {

        LoginResponse loginResponse = corporationService.login(corporationLoginRequest);

        String accessToken = loginResponse.getAccessToken();
        String refreshToken = loginResponse.getRefreshToken();


        log.info("쿠키에 저장된 AccessToken :");
        log.info("Authorization = {};", accessToken);
        CookieUtils.addAccessTokenAtCookie(response, accessToken);

        log.info("쿠키에 저장된 RefreshToken :");
        log.info("Authorization-refresh= {}; Path=/; Secure; HttpOnly; Expires=DOW, DAY MONTH YEAR HH:MM:SS GMT;", refreshToken);
        CookieUtils.addRefreshTokenAtCookie(response, refreshToken);

        return Response.success(loginResponse);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public Response<MessageResponse> logout(@RequestBody TokenRequest tokenRequest, Authentication authentication) {

        String email = authentication.getName();

        MessageResponse response = corporationService.logout(tokenRequest, email);
        return Response.success(response);
    }

}
