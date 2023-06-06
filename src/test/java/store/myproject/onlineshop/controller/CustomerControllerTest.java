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
import store.myproject.onlineshop.domain.dto.customer.CustomerJoinRequest;
import store.myproject.onlineshop.domain.dto.customer.CustomerLoginRequest;
import store.myproject.onlineshop.domain.dto.customer.CustomerLoginResponse;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.service.CustomerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.domain.enums.Gender.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

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
                .andExpect(jsonPath("$.result.message").value("email conflict"))
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
                .andExpect(jsonPath("$.result.message").value("Nick name conflict"))
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
                .andExpect(cookie().value("Authorization","accessToken"))
                .andExpect(cookie().value("Authorization-refresh","refreshToken"))
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
                .willThrow(new AppException(INVALID_TOKEN,INVALID_TOKEN.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_TOKEN"))
                .andExpect(jsonPath("$.result.message").value("Token invalid"))
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
                .willThrow(new AppException(INVALID_PASSWORD,INVALID_PASSWORD.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/customers/login")
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.resultCode").value("ERROR"))
                .andExpect(jsonPath("$.result.errorCode").value("INVALID_PASSWORD"))
                .andExpect(jsonPath("$.result.message").value("invalid password"))
                .andDo(print());

    }




}