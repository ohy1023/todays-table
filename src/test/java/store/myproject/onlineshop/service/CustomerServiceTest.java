package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.*;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.repository.membership.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.CustomerFixture;
import store.myproject.onlineshop.fixture.MemberShipFixture;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.repository.customer.CustomerRepository;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private MemberShipRepository memberShipRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private RedisService redisService;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private MessageUtil messageUtil;

    @InjectMocks
    private CustomerService customerService;

    Customer customer = CustomerFixture.createCustomer();
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    @Test
    @DisplayName("회원가입 성공")
    public void join_success() {

        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        MemberShip bronze = MemberShipFixture.createBronzeMembership();

        given(customerRepository.findByNickName(request.getNickName()))
                .willReturn(Optional.empty());

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        given(memberShipRepository.findTopByLowestBaseline(PageRequest.of(0, 1)))
                .willReturn(List.of(bronze));

        given(customerRepository.save(any(Customer.class)))
                .willReturn(customer);

        given(messageUtil.get(MessageCode.CUSTOMER_JOIN)).willReturn("회원 가입 성공");


        // when
        MessageResponse response = customerService.registerCustomer(request);

        // then
        assertThat(response.getMessage()).isEqualTo("회원 가입 성공");


    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    public void join_fail_duplicate_nickname() {

        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        given(customerRepository.findByNickName(request.getNickName()))
                .willReturn(Optional.of(customer));

        // when & then
        assertThatThrownBy(() -> customerService.registerCustomer(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_NICKNAME.getMessage());

    }

    @Test
    @DisplayName("회원가입 실패 - 멤버쉽 없음")
    public void join_fail_not_found_membership() {

        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        given(memberShipRepository.findTopByLowestBaseline(PageRequest.of(0, 1)))
                .willReturn(List.of());

        // when & then
        assertThatThrownBy(() -> customerService.registerCustomer(request))
                .isInstanceOf(AppException.class)
                .hasMessage(MEMBERSHIP_NOT_FOUND.getMessage());

    }


    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    public void join_fail_duplicate_email() {

        // given
        CustomerJoinRequest request = CustomerFixture.createJoinRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer));

        // when & then
        assertThatThrownBy(() -> customerService.registerCustomer(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_EMAIL.getMessage());
    }


    @Test
    @DisplayName("로그인 성공")
    public void login_success() {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer));

        given(encoder.matches(request.getPassword(), customer.getPassword()))
                .willReturn(true);

        given(jwtUtils.createAccessToken(request.getEmail()))
                .willReturn(accessToken);

        given(jwtUtils.createRefreshToken(request.getEmail()))
                .willReturn(refreshToken);

        // when
        LoginResponse response = customerService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    public void login_fail_email_not_found() {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    public void login_fail_invalid_password() {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer));

        given(encoder.matches(request.getPassword(), customer.getPassword()))
                .willReturn(false);


        // then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - accessToken이 null인 경우")
    public void login_fail_access_token_null() {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer));

        given(encoder.matches(request.getPassword(), customer.getPassword()))
                .willReturn(true);

        given(jwtUtils.createAccessToken(request.getEmail()))
                .willReturn(null);

        given(jwtUtils.createRefreshToken(request.getEmail()))
                .willReturn(refreshToken);

        // when & then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(ACCESS_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - refresh token이 null인 경우")
    public void login_fail_refresh_token_null() {

        // given
        CustomerLoginRequest request = CustomerFixture.createLoginRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer));

        given(encoder.matches(request.getPassword(), customer.getPassword()))
                .willReturn(true);


        given(jwtUtils.createAccessToken(request.getEmail()))
                .willReturn(accessToken);

        given(jwtUtils.createRefreshToken(request.getEmail()))
                .willReturn(null);

        // when & then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(REFRESH_TOKEN_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void logout_success() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(jwtUtils.getExpiration(request.getAccessToken()))
                .willReturn(12000L);

        given(messageUtil.get(MessageCode.CUSTOMER_LOGOUT)).willReturn("로그아웃 되었습니다.");

        // when
        MessageResponse response = customerService.logout(request, customer.getEmail());

        // then
        assertThat(response.getMessage()).isEqualTo("로그아웃 되었습니다.");

    }

    @Test
    @DisplayName("로그아웃 실패 - 존재하지 않는 이메일")
    public void logout_fail_email_not_found() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.logout(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 - 만료된 토큰")
    public void logout_fail_expired_token() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(jwtUtils.isExpired(request.getAccessToken()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> customerService.logout(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_ACCESS_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 - 유효하지 않은 토큰")
    public void logout_fail_invalid_token() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(jwtUtils.isExpired(request.getAccessToken()))
                .willReturn(false);

        given(jwtUtils.isInvalid(request.getAccessToken()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> customerService.logout(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_ACCESS_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    public void reissue_token_success() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(redisService.getValues("RT:" + customer.getEmail()))
                .willReturn(refreshToken);

        given(jwtUtils.createAccessToken(customer.getEmail()))
                .willReturn("newAccessToken");

        given(jwtUtils.createRefreshToken(customer.getEmail()))
                .willReturn("newRefreshToken");

        // when
        LoginResponse response = customerService.reissueToken(request, customer.getEmail());

        // then
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 존재하지 않는 이메일")
    public void reissue_token_fail_email_not_found() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.reissueToken(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 토큰")
    public void reissue_token_fail_expired_token() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(jwtUtils.isExpired(request.getRefreshToken()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> customerService.reissueToken(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(EXPIRED_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 없음")
    public void reissue_token_fail_not_found_token() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));
        given(redisService.getValues("RT:" + customer.getEmail())).willReturn(null);

        // when & then
        assertThatThrownBy(() -> customerService.reissueToken(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(MISMATCH_REFRESH_TOKEN.getMessage());
    }


    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 불일치")
    public void reissue_token_fail_mismatch_token() {

        // given
        TokenRequest request = CustomerFixture.createTokenRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(redisService.getValues("RT:" + customer.getEmail()))
                .willReturn("mismatchToken");

        // when & then
        assertThat(request.getRefreshToken()).isNotEqualTo("mismatchToken");
        assertThatThrownBy(() -> customerService.reissueToken(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(MISMATCH_REFRESH_TOKEN.getMessage());
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    public void get_customer_info_success() {

        Customer customer1 = Customer.builder()
                .id(1L)
                .email("test@example.com")
                .password("testPassword123!")
                .userName("홍길동")
                .nickName("hong123")
                .gender(Gender.MALE)
                .tel("010-1234-5678")
                .address(Address.builder()
                        .city("서울")
                        .street("테스트로")
                        .detail("123")
                        .zipcode("04524")
                        .build())
                .customerRole(CustomerRole.ROLE_USER)
                .monthlyPurchaseAmount(BigDecimal.ZERO)
                .memberShip(MemberShip.builder()
                        .id(1L)
                        .level(Level.BRONZE)
                        .baseline(BigDecimal.ZERO)
                        .discountRate(BigDecimal.ZERO)
                        .build())
                .build();

        customer1.setCreatedDate(LocalDateTime.now());

        // given
        String request = customer1.getEmail();

        given(customerRepository.findByEmail(request))
                .willReturn(Optional.of(customer1));

        // when
        CustomerInfoResponse response = customerService.getCustomerInfo(request);

        // then
        assertThat(response.getEmail()).isEqualTo(customer1.getEmail());
        assertThat(response.getUserName()).isEqualTo(customer1.getUserName());
        assertThat(response.getNickName()).isEqualTo(customer1.getNickName());

    }

    @Test
    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 이메일")
    public void get_customer_info_fail_email_not_found() {

        // given
        String request = customer.getEmail();

        given(customerRepository.findByEmail(request))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.getCustomerInfo(request))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    public void modify_customer_info_success() {

        // given
        CustomerModifyRequest request = CustomerFixture.createModifyRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(messageUtil.get(MessageCode.CUSTOMER_MODIFIED)).willReturn("수정 완료");

        // when
        MessageResponse response = customerService.updateCustomerInfo(request, customer.getEmail());

        // then
        assertThat(response.getMessage()).isEqualTo("수정 완료");

    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 이메일")
    public void modify_customer_info_fail_email_not_found() {

        // given
        CustomerModifyRequest request = CustomerFixture.createModifyRequest();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.updateCustomerInfo(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    public void delete_customer_success() {

        // given
        String request = customer.getEmail();

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.of(customer));

        given(messageUtil.get(MessageCode.CUSTOMER_DELETED)).willReturn("회원 탈퇴 성공");

        // when
        MessageResponse response = customerService.deleteCustomer(request);

        // then
        assertThat(response.getMessage()).isEqualTo("회원 탈퇴 성공");

    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 이메일")
    public void delete_customer_fail_email_not_found() {

        // given
        String request = customer.getEmail();

        given(customerRepository.findByEmail(request))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.deleteCustomer(request))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("이메일 중복 체크 성공")
    public void check_email_duplicate_success() {

        // given
        CustomerEmailCheckRequest request = CustomerFixture.createEmailCheckRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());
        given(messageUtil.get(MessageCode.EMAIL_AVAILABLE)).willReturn("사용 가능한 이메일 입니다.");

        // when
        MessageResponse response = customerService.checkEmail(request);

        // then
        assertThat(response.getMessage()).isEqualTo("사용 가능한 이메일 입니다.");

    }

    @Test
    @DisplayName("이메일 중복 체크 실패")
    public void check_email_duplicate_fail() {

        // given
        CustomerEmailCheckRequest request = CustomerFixture.createEmailCheckRequest();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer));

        // when & then
        assertThatThrownBy(() -> customerService.checkEmail(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_EMAIL.getMessage());

    }

    @Test
    @DisplayName("닉네임 중복 체크 성공")
    public void check_nickname_duplicate_success() {

        // given
        CustomerNickNameCheckRequest request = CustomerFixture.createNickNameCheckRequest();

        given(customerRepository.findByNickName(request.getNickName()))
                .willReturn(Optional.empty());
        given(messageUtil.get(MessageCode.NICKNAME_AVAILABLE)).willReturn("사용 가능한 닉네임 입니다.");

        // when
        MessageResponse response = customerService.checkNickName(request);

        // then
        assertThat(response.getMessage()).isEqualTo("사용 가능한 닉네임 입니다.");

    }

    @Test
    @DisplayName("닉네임 중복 체크 실패")
    public void check_nickname_duplicate_fail() {

        // given
        CustomerNickNameCheckRequest request = CustomerFixture.createNickNameCheckRequest();

        given(customerRepository.findByNickName(request.getNickName()))
                .willReturn(Optional.of(customer));

        // when & then
        assertThatThrownBy(() -> customerService.checkNickName(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_NICKNAME.getMessage());

    }

    @Test
    @DisplayName("임시 비밀번호 발급")
    public void temp_password_success() {
        // given
        CustomerTempPasswordRequest request = CustomerFixture.createTempPasswordRequest();

        given(customerRepository.findByEmailAndTel(request.getEmail(),request.getTel())).willReturn(Optional.of(customer));

        // when
        CustomerTempPasswordResponse response = customerService.sendTempPassword(request);

        // then
        assertThat(response.getEmail()).isEqualTo(customer.getEmail());

//        assertThat(response).is
    }

    @Test
    @DisplayName("임시 비밀번호 실패 - 회원 없음")
    public void temp_password_fail_customer_not_found() {
        // given
        CustomerTempPasswordRequest request = CustomerFixture.createTempPasswordRequest();

        given(customerRepository.findByEmailAndTel(request.getEmail(), request.getTel())).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.sendTempPassword(request))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("비밀번호 변경 성공")
    public void modify_password_success() {
        // given
        CustomerChangePasswordRequest request = CustomerFixture.createChangePasswordRequest("curPassword");

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(encoder.matches(request.getCurrentPassword(), customer.getPassword())).willReturn(true);
        given(messageUtil.get(MessageCode.CUSTOMER_PASSWORD_MODIFIED)).willReturn("비밀번호 변경 성공");

        // when
        MessageResponse response = customerService.updatePassword(request, customer.getEmail());

        // then
        then(customerRepository).should().findByEmail(customer.getEmail());
        assertThat(response.getMessage()).isEqualTo("비밀번호 변경 성공");
    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 비밀번호 미일치")
    public void modify_password_fail_mismatch_password() {
        // given
        CustomerChangePasswordRequest request = CustomerFixture.createChangePasswordRequest("wrongPassword");

        given(customerRepository.findByEmail(customer.getEmail())).willReturn(Optional.of(customer));
        given(encoder.matches(request.getCurrentPassword(), customer.getPassword())).willReturn(false);

        // when & then
        assertThatThrownBy(() -> customerService.updatePassword(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(MISMATCH_PASSWORD.getMessage());

    }

    @Test
    @DisplayName("비밀번호 변경 실패 - 이메일로 고객 조회 실패")
    public void modify_password_fail_not_found_customer() {
        // given
        CustomerChangePasswordRequest request = CustomerFixture.createChangePasswordRequest(customer.getPassword());

        given(customerRepository.findByEmail(customer.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.updatePassword(request, customer.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
    }


}