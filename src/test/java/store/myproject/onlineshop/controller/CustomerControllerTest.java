package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.custom.WithMockCustomUser;
import store.myproject.onlineshop.domain.dto.customer.*;
import store.myproject.onlineshop.domain.entity.Address;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.service.CustomerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.domain.enums.Gender.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@WebMvcTest(CustomerController.class)
@WithMockCustomUser
class CustomerControllerTest {

    @MockBean
    CustomerService customerService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("회원가입 성공")
    public void join_success() throws Exception {

        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test@naver.com")
                .password("test")
                .userName("test")
                .nickName("test")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();

        given(customerService.join(any(CustomerJoinRequest.class)))
                .willReturn("test");

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("회원가입 성공"))
                .andDo(print());

    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 형식이 아님")
    public void join_fail_notEmailFormat() throws Exception {

        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test")
                .password("test")
                .userName("test")
                .nickName("test")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();

        given(customerService.join(any(CustomerJoinRequest.class)))
                .willReturn("test");

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("Email"))
                .andExpect(jsonPath("$.result.message").value("must be a well-formed email address"))
                .andDo(print());

    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void join_fail_duplicatedEmail() throws Exception {
        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test@naver.com")
                .password("test")
                .userName("test")
                .nickName("test")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();
        given(customerService.join(any(CustomerJoinRequest.class)))
                .willThrow(new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_EMAIL.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void join_fail_duplicatedNickName() throws Exception {
        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test@naver.com")
                .password("test")
                .userName("test")
                .nickName("test")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();
        given(customerService.join(any(CustomerJoinRequest.class)))
                .willThrow(new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/join")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATE_NICKNAME"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_NICKNAME.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_success() throws Exception {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();

        CustomerLoginResponse response = CustomerLoginResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();


        given(customerService.login(any(CustomerLoginRequest.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                .andExpect(cookie().value("Authorization", "accessToken"))
                .andExpect(cookie().value("Authorization-refresh", "refreshToken"))
                .andDo(print());

    }

    @Test
    @DisplayName("로그인 실패 - 토큰이 널인 경우")
    public void login_fail_nullToken() throws Exception {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();


        given(customerService.login(any(CustomerLoginRequest.class)))
                .willThrow(new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    public void login_fail_wrongPassword() throws Exception {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();


        given(customerService.login(any(CustomerLoginRequest.class)))
                .willThrow(new AppException(INVALID_PASSWORD, INVALID_PASSWORD.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.result.message").value(INVALID_PASSWORD.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 성공")
    public void logout_success() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .build();

        given(customerService.logout(any(CustomerTokenRequest.class), any(String.class)))
                .willReturn("로그아웃 되셨습니다.");


        // when & then
        mockMvc.perform(post("/api/v1/customers/logout")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("로그아웃 되셨습니다."))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 실패 - 만료된 토큰")
    public void logout_fail_tokenExpire() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .build();

        given(customerService.logout(any(CustomerTokenRequest.class), any(String.class)))
                .willThrow(new AppException(EXPIRED_TOKEN, EXPIRED_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/logout")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("EXPIRED_TOKEN"))
                .andExpect(jsonPath("$.result.message").value(EXPIRED_TOKEN.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("로그아웃 실패 - 잘못된 토큰")
    public void logout_fail_tokenInvalid() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(customerService.logout(any(CustomerTokenRequest.class), any(String.class)))
                .willThrow(new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/logout")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("토큰 재발급")
    public void reissue_success() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        CustomerLoginResponse response = CustomerLoginResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();


        given(customerService.reissue(any(CustomerTokenRequest.class), any(String.class)))
                .willReturn(response);


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.accessToken").value("accessToken"))
                .andExpect(jsonPath("$.result.refreshToken").value("refreshToken"))
                .andDo(print());

    }

    @Test
    @DisplayName("토큰 재발급 실패 - 잘못된 리프레쉬 토큰")
    public void reissue_fail_tokenInvalid() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(customerService.reissue(any(CustomerTokenRequest.class), any(String.class)))
                .willThrow(new AppException(EXPIRED_TOKEN, EXPIRED_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("EXPIRED_TOKEN"))
                .andExpect(jsonPath("$.result.message").value(EXPIRED_TOKEN.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 리프레쉬 토큰")
    public void reissue_fail_tokenExpire() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();

        given(customerService.reissue(any(CustomerTokenRequest.class), any(String.class)))
                .willThrow(new AppException(INVALID_TOKEN, INVALID_TOKEN.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").value(INVALID_TOKEN.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 리프레쉬 토큰이 없는 경우")
    public void reissue_fail_tokenEmpty() throws Exception {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken("accessToken")
                .build();

        given(customerService.reissue(any(CustomerTokenRequest.class), any(String.class)))
                .willThrow(new AppException(INVALID_REQUEST, INVALID_REQUEST.getMessage()));


        // when & then
        mockMvc.perform(post("/api/v1/customers/reissue")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_REQUEST"))
                .andExpect(jsonPath("$.result.message").value(INVALID_REQUEST.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    public void info_success() throws Exception {

        // given
        CustomerInfoResponse response = CustomerInfoResponse.builder()
                .createdDate("2023-06-07")
                .email("test@naver.com")
                .userName("test")
                .nickName("test")
                .gender(MALE)
                .tel("010-1234-5678")
                .address(Address.builder()
                        .city("서울특별시")
                        .street("시흥대로 589-8")
                        .detail("1601호")
                        .zipcode("07445")
                        .build())
                .build();


        given(customerService.getInfo(any(String.class)))
                .willReturn(response);

        // when & then

        mockMvc.perform(get("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.email").value("test@naver.com"))
                .andExpect(jsonPath("$.result.nickName").value("test"))
                .andExpect(jsonPath("$.result.userName").value("test"))
                .andExpect(jsonPath("$.result.tel").value("010-1234-5678"))
                .andExpect(jsonPath("$.result.address.city").value("서울특별시"))
                .andExpect(jsonPath("$.result.address.street").value("시흥대로 589-8"))
                .andExpect(jsonPath("$.result.address.detail").value("1601호"))
                .andExpect(jsonPath("$.result.address.zipcode").value("07445"))
                .andExpect(jsonPath("$.result.gender").value("MALE"))
                .andExpect(jsonPath("$.result.createdDate").value("2023-06-07"))
                .andDo(print());

    }


    @Test
    @DisplayName("회원 정보 조회 실패 - 이메일을 찾을 수 없는 경우")
    public void info_fail_notFoundEmail() throws Exception {

        // given
        given(customerService.getInfo(any(String.class)))
                .willThrow(new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));


        // when & then
        mockMvc.perform(get("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 수정 성공")
    public void modify_success() throws Exception {

        // given
        CustomerModifyRequest request = CustomerModifyRequest.builder()
                .userName("test")
                .nickName("test")
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();

        given(customerService.modify(any(CustomerModifyRequest.class), any(String.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(patch("/api/v1/customers")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value(1))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 수정 실패")
    public void modify_fail_notFoundEmail() throws Exception {

        // given
        CustomerModifyRequest request = CustomerModifyRequest.builder()
                .userName("test")
                .nickName("test")
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();

        given(customerService.modify(any(CustomerModifyRequest.class), any(String.class)))
                .willThrow(new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(patch("/api/v1/customers")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    public void delete_success() throws Exception {

        // given
        given(customerService.delete(any(String.class)))
                .willReturn(1L);

        // when & then
        mockMvc.perform(delete("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value(1))
                .andDo(print());

    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 이메일을 찾을 수 없는 경우")
    public void delete_fail_notFoundEmail() throws Exception {

        // given
        given(customerService.delete(any(String.class)))
                .willThrow(new AppException(EMAIL_NOT_FOUND, EMAIL_NOT_FOUND.getMessage()));


        // when & then
        mockMvc.perform(delete("/api/v1/customers")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("EMAIL_NOT_FOUND"))
                .andExpect(jsonPath("$.result.message").value(EMAIL_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("이메일 중복 체크 성공")
    public void email_check_success() throws Exception {

        // given
        CustomerEmailCheckRequest request = new CustomerEmailCheckRequest("test@naver.com");
        given(customerService.emailCheck(any(CustomerEmailCheckRequest.class)))
                .willReturn("사용 가능한 이메일 입니다.");

        // when & then
        mockMvc.perform(post("/api/v1/customers/email")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("사용 가능한 이메일 입니다."))
                .andDo(print());

    }


    @Test
    @DisplayName("이메일 중복 체크 실패- 이메일 중복")
    public void email_check_fail_duplicate() throws Exception {

        // given
        CustomerEmailCheckRequest request = new CustomerEmailCheckRequest("test@naver.com");

        given(customerService.emailCheck(any(CustomerEmailCheckRequest.class)))
                .willThrow(new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/email")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATE_EMAIL"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_EMAIL.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("닉네임 중복 체크 성공")
    public void nick_name_check_success() throws Exception {

        // given
        CustomerNickNameCheckRequest request = new CustomerNickNameCheckRequest("test");
        given(customerService.nickNameCheck(any(CustomerNickNameCheckRequest.class)))
                .willReturn("사용 가능한 닉네임 입니다.");

        // when & then
        mockMvc.perform(post("/api/v1/customers/nickname")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result").value("사용 가능한 닉네임 입니다."))
                .andDo(print());

    }


    @Test
    @DisplayName("닉네임 중복 체크 실패- 닉네임 중복")
    public void nick_name_check_fail_duplicate() throws Exception {

        // given
        CustomerNickNameCheckRequest request = new CustomerNickNameCheckRequest("test");
        given(customerService.nickNameCheck(any(CustomerNickNameCheckRequest.class)))
                .willThrow(new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/nickname")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("DUPLICATE_NICKNAME"))
                .andExpect(jsonPath("$.result.message").value(DUPLICATE_NICKNAME.getMessage()))
                .andDo(print());

    }
}