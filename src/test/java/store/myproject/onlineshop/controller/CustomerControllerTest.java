package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.service.CustomerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.ERROR;
import static store.myproject.onlineshop.fixture.ResultCode.SUCCESS;

@WebMvcTest(CustomerController.class)
@WithMockUser
class CustomerControllerTest {

    @MockBean
    CustomerService customerService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("회원 가입 성공")
    public void join_success() throws Exception {

        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        MessageResponse response = new MessageResponse("회원 가입 성공");

        given(customerService.registerCustomer(any(CustomerJoinRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "no-at.com", "test@", "@test.com", "abc@.com"})
    @DisplayName("회원가입 실패 - 이메일 형식이 아님")
    void join_fail_not_email_format(String invalidEmail) throws Exception {

        // given: 잘못된 email만 주입
        CustomerJoinRequest invalidRequest = CustomerFixture.createInvalidJoinRequest(invalidEmail);

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(invalidRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value("Email"))
                .andExpect(jsonPath("$.result.message").value("must be a well-formed email address"))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void join_fail_duplicate_email() throws Exception {
        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        given(customerService.registerCustomer(any(CustomerJoinRequest.class)))
                .willThrow(new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_EMAIL.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void join_fail_duplicate_nickname() throws Exception {
        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        given(customerService.registerCustomer(any(CustomerJoinRequest.class)))
                .willThrow(new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATE_NICKNAME"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_NICKNAME.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_success() throws Exception {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        LoginResponse response = CustomerFixture.createLoginResponse();

        given(customerService.login(any(CustomerLoginRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                .andExpect(cookie().value("Authorization", "accessToken"))
                .andExpect(cookie().value("Authorization-refresh", "refreshToken"))
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 실패 - 토큰이 널인 경우")
    public void login_fail_null_token() throws Exception {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerService.login(any(CustomerLoginRequest.class)))
                .willThrow(new AppException(INVALID_ACCESS_TOKEN, INVALID_ACCESS_TOKEN.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_ACCESS_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_ACCESS_TOKEN.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    public void login_fail_invalid_password() throws Exception {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerService.login(any(CustomerLoginRequest.class)))
                .willThrow(new AppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.result.message").value(INVALID_PASSWORD.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 성공")
    public void logout_success() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        MessageResponse response = new MessageResponse("로그아웃 되셨습니다.");

        given(customerService.logout(any(TokenRequest.class), any(String.class)))
                .willReturn(response);


        // when & then
        mockMvc.perform(post("/api/v1/customers/logout")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 실패 - 만료된 토큰")
    public void logout_fail_expired_token() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerService.logout(any(TokenRequest.class), any(String.class)))
                .willThrow(new AppException(EXPIRED_ACCESS_TOKEN, EXPIRED_ACCESS_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/logout")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(EXPIRED_ACCESS_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(EXPIRED_ACCESS_TOKEN.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 실패 - 잘못된 토큰")
    public void logout_fail_invalid_token() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerService.logout(any(TokenRequest.class), any(String.class)))
                .willThrow(new AppException(INVALID_ACCESS_TOKEN, INVALID_ACCESS_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/logout")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(INVALID_ACCESS_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(INVALID_ACCESS_TOKEN.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("토큰 재발급")
    public void reissue_token_success() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        LoginResponse response = CustomerFixture.createLoginResponse();

        given(customerService.reissueToken(any(TokenRequest.class), any(String.class)))
                .willReturn(response);


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                .andDo(print());

    }

    @Test
    @DisplayName("토큰 재발급 실패 - 잘못된 리프레쉬 토큰")
    public void reissue_token_fail_invalid_refresh_token() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerService.reissueToken(any(TokenRequest.class), any(String.class)))
                .willThrow(new AppException(MISMATCH_REFRESH_TOKEN, MISMATCH_REFRESH_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(MISMATCH_REFRESH_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(MISMATCH_REFRESH_TOKEN.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 리프레쉬 토큰")
    public void reissue_token_fail_expired_refresh_token() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerService.reissueToken(any(TokenRequest.class), any(String.class)))
                .willThrow(new AppException(EXPIRED_REFRESH_TOKEN, EXPIRED_REFRESH_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(EXPIRED_REFRESH_TOKEN.name()))
                .andExpect(jsonPath("$.result.message").value(EXPIRED_REFRESH_TOKEN.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 리프레쉬 토큰이 없는 경우")
    public void reissue_token_fail_missing_refresh_token() throws Exception {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerService.reissueToken(any(TokenRequest.class), any(String.class)))
                .willThrow(new AppException(REFRESH_TOKEN_NOT_FOUND, REFRESH_TOKEN_NOT_FOUND.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(REFRESH_TOKEN_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(REFRESH_TOKEN_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    public void get_customer_info_success() throws Exception {

        // given
        CustomerInfoResponse response = CustomerFixture.createCustomerInfoResponse();

        given(customerService.getCustomerInfo(any(String.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.email").value(response.getEmail()))
                .andExpect(jsonPath("$.result.nickName").value(response.getNickName()))
                .andExpect(jsonPath("$.result.userName").value(response.getUserName()))
                .andExpect(jsonPath("$.result.tel").value(response.getTel()))
                .andExpect(jsonPath("$.result.address.city").value(response.getAddress().getCity()))
                .andExpect(jsonPath("$.result.address.street").value(response.getAddress().getStreet()))
                .andExpect(jsonPath("$.result.address.detail").value(response.getAddress().getDetail()))
                .andExpect(jsonPath("$.result.address.zipcode").value(response.getAddress().getZipcode()))
                .andExpect(jsonPath("$.result.gender").value(response.getGender().name()))
                .andExpect(jsonPath("$.result.createdDate").value(response.getCreatedDate()))
                .andDo(print());

    }


    @Test
    @DisplayName("회원 정보 조회 실패 - 이메일을 찾을 수 없는 경우")
    public void get_customer_info_fail_email_not_found() throws Exception {

        // given
        given(customerService.getCustomerInfo(any(String.class)))
                .willThrow(new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));


        // when & then
        mockMvc.perform(get("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 수정 성공")
    public void modify_customer_info_success() throws Exception {

        // given
        CustomerModifyRequest request = CustomerFixture.createModifyRequest();

        MessageResponse response = new MessageResponse("회원 수정 성공");

        given(customerService.updateCustomerInfo(any(CustomerModifyRequest.class), any(String.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(patch("/api/v1/customers")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 수정 실패")
    public void modify_customer_info_fail_email_not_found() throws Exception {

        // given
        CustomerModifyRequest request = CustomerFixture.createModifyRequest();

        given(customerService.updateCustomerInfo(any(CustomerModifyRequest.class), any(String.class)))
                .willThrow(new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(patch("/api/v1/customers")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(EMAIL_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    public void delete_customer_success() throws Exception {

        // given
        MessageResponse response = new MessageResponse("회원 탈퇴 성공");

        given(customerService.deleteCustomer(any(String.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(delete("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 이메일을 찾을 수 없는 경우")
    public void delete_customer_fail_email_not_found() throws Exception {

        // given
        given(customerService.deleteCustomer(any(String.class)))
                .willThrow(new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));


        // when & then
        mockMvc.perform(delete("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(EMAIL_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("이메일 중복 체크 성공")
    public void check_email_duplicate_success() throws Exception {

        // given
        CustomerEmailCheckRequest request = CustomerFixture.createEmailCheckRequest();

        MessageResponse response = new MessageResponse("사용 가능한 이메일 입니다.");

        given(customerService.checkEmail(any(CustomerEmailCheckRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/customers/email")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("이메일 중복 체크 실패- 이메일 중복")
    public void check_email_duplicate_fail() throws Exception {

        // given
        CustomerEmailCheckRequest request = CustomerFixture.createEmailCheckRequest();

        given(customerService.checkEmail(any(CustomerEmailCheckRequest.class)))
                .willThrow(new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/email")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(DUPLICATE_EMAIL.name()))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_EMAIL.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("닉네임 중복 체크 성공")
    public void check_nickname_duplicate_success() throws Exception {

        // given
        CustomerNickNameCheckRequest request = CustomerFixture.createNickNameCheckRequest();

        MessageResponse response = new MessageResponse("사용 가능한 닉네임 입니다.");

        given(customerService.checkNickName(any(CustomerNickNameCheckRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/customers/nickname")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("닉네임 중복 체크 실패- 닉네임 중복")
    public void check_nickname_duplicate_fail() throws Exception {

        // given
        CustomerNickNameCheckRequest request = CustomerFixture.createNickNameCheckRequest();

        given(customerService.checkNickName(any(CustomerNickNameCheckRequest.class)))
                .willThrow(new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/nickname")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(DUPLICATE_NICKNAME.name()))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_NICKNAME.getMessage()))
                .andDo(print());

    }

//    @Test
//    @DisplayName("admin 변경 성공")
//    public void change_to_admin_success() throws Exception {
//
//        // given
//        given(customerService.settingAdmin(any(String.class)))
//                .willReturn(new MessageResponse("회원의 권한을 Admin으로 설정하였습니다."));
//
//        // when & then
//        mockMvc.perform(put("/api/v1/customers/admin")
//                        .with(csrf()))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
//                .andExpect(jsonPath("$.result.msg").value("회원의 권한을 Admin으로 설정하였습니다."))
//                .andDo(print());
//
//    }

//    @Test
//    @DisplayName("admin 변경 실패- 이미 admin인 경우")
//    public void change_to_admin_fail_already_admin() throws Exception {
//
//        // given
//        given(customerService.settingAdmin(any(String.class)))
//                .willThrow(new AppException(ALREADY_ADMIN, ALREADY_ADMIN.getMessage()));
//
//        // when & then
//        mockMvc.perform(put("/api/v1/customers/admin")
//                        .with(csrf()))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.resultCode").value(ERROR))
//                .andExpect(jsonPath("$.result.errorCode").value("ALREADY_ADMIN"))
//                .andExpect(jsonPath("$.result.message").value(ALREADY_ADMIN.getMessage()))
//                .andDo(print());
//
//    }

    @Test
    @DisplayName("임시 비밀번호 발급 성공")
    public void set_temp_password_success() throws Exception {

        // given
        CustomerTempPasswordRequest request = CustomerFixture.createTempPasswordRequest();

        CustomerTempPasswordResponse response = CustomerFixture.createTempPasswordResponse(request.getEmail());

        given(customerService.sendTempPassword(any(CustomerTempPasswordRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/customers/temp-password")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.email").value(response.getEmail()))
                .andExpect(jsonPath("$.result.tempPassword").value(response.getTempPassword()))
                .andDo(print());

    }

    @Test
    @DisplayName("임시 비밀번호 발급 실패- 요청 이메일과 전화번호로 해당 유저를 찾을 수 없는 경우")
    public void set_temp_password_fail_customer_not_found() throws Exception {

        // given
        CustomerTempPasswordRequest request = CustomerFixture.createTempPasswordRequest();

        given(customerService.sendTempPassword(any(CustomerTempPasswordRequest.class)))
                .willThrow(new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/customers/temp-password")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(CUSTOMER_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("새 비밀번호 변경 성공")
    public void change_password_success() throws Exception {

        // given
        CustomerChangePasswordRequest request = CustomerFixture.createChangePasswordRequest("curPassword");

        MessageResponse response = new MessageResponse("비밀번호 변경 성공");

        given(customerService.updatePassword(any(CustomerChangePasswordRequest.class), any(String.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/customers/password")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("새 비밀번호 변경 실패 - 비밀번호 불일치")
    public void change_password_fail_mismatch() throws Exception {

        // given
        CustomerChangePasswordRequest request = CustomerFixture.createChangePasswordRequest("curPassword");

        given(customerService.updatePassword(any(CustomerChangePasswordRequest.class), any(String.class)))
                .willThrow(new AppException(MISMATCH_PASSWORD, MISMATCH_PASSWORD.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/customers/password")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(MISMATCH_PASSWORD.name()))
                .andExpect(jsonPath("$.result.message").value(MISMATCH_PASSWORD.getMessage()))
                .andDo(print());

    }

}