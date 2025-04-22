//package store.myproject.onlineshop.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import store.myproject.onlineshop.domain.MessageResponse;
//import store.myproject.onlineshop.domain.customer.dto.*;
//import store.myproject.onlineshop.domain.customer.Customer;
//import store.myproject.onlineshop.domain.membership.MemberShip;
//import store.myproject.onlineshop.domain.membership.repository.MemberShipRepository;
//import store.myproject.onlineshop.exception.AppException;
//import store.myproject.onlineshop.fixture.CustomerFixture;
//import store.myproject.onlineshop.fixture.MemberShipFixture;
//import store.myproject.onlineshop.global.redis.RedisDao;
//import store.myproject.onlineshop.global.utils.JwtUtils;
//import store.myproject.onlineshop.domain.customer.repository.CustomerRepository;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.BDDMockito.given;
//import static store.myproject.onlineshop.exception.ErrorCode.*;
//
//@ExtendWith(MockitoExtension.class)
//class CustomerServiceTest {
//
//    @Mock
//    private CustomerRepository customerRepository;
//
//    @Mock
//    private MemberShipRepository memberShipRepository;
//
//    @Mock
//    private BCryptPasswordEncoder encoder;
//
//    @Mock
//    private RedisDao redisDao;
//
//    @Mock
//    private JwtUtils jwtUtils;
//
//    @InjectMocks
//    private CustomerService customerService;
//
//    Customer customer = CustomerFixture.createCustomer();
//    String accessToken = "accessToken";
//    String refreshToken = "refreshToken";
//
//    @Test
//    @DisplayName("회원가입 성공")
//    public void join_success() {
//
//        // given
//        CustomerJoinRequest request = CustomerFixture.createJoinRequest();
//
//        MemberShip bronze = MemberShipFixture.createBronzeMembership();
//
//        given(customerRepository.findByNickName(request.getNickName()))
//                .willReturn(Optional.empty());
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.empty());
//
//        given(memberShipRepository.findMemberShipByLevel(any()))
//                .willReturn(Optional.of(bronze));
//
//        given(customerRepository.save(any(Customer.class)))
//                .willReturn(customer);
//
//
//        // when
//        MessageResponse response = customerService.join(request);
//
//        // then
//        assertThat(response.getMsg()).isEqualTo("회원가입 성공");
//
//
//    }
//
//    @Test
//    @DisplayName("회원가입 실패 - 닉네임 중복")
//    public void join_fail_duplicate_nickname() {
//
//        // given
//        CustomerJoinRequest request = CustomerFixture.createJoinRequest();
//
//        given(customerRepository.findByNickName(request.getNickName()))
//                .willReturn(Optional.of(customer));
//
//        // when & then
//        assertThatThrownBy(() -> customerService.join(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(DUPLICATE_NICKNAME.getMessage());
//
//    }
//
//    @Test
//    @DisplayName("회원가입 실패 - 이메일 중복")
//    public void join_fail_duplicate_email() {
//
//        // given
//        CustomerJoinRequest request = CustomerFixture.createJoinRequest();
//
//        given(customerRepository.findByNickName(request.getNickName()))
//                .willReturn(Optional.empty());
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        // when & then
//        assertThatThrownBy(() -> customerService.join(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(DUPLICATE_EMAIL.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그인 성공")
//    public void login_success() {
//
//        // given
//        CustomerLoginRequest request = CustomerFixture.createLoginRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(encoder.matches(request.getPassword(), customer.getPassword()))
//                .willReturn(true);
//
//        given(jwtUtils.createAccessToken(request.getEmail()))
//                .willReturn(accessToken);
//
//        given(jwtUtils.createRefreshToken(request.getEmail()))
//                .willReturn(refreshToken);
//
//        // when
//        LoginResponse response = customerService.login(request);
//
//        // then
//        assertThat(response.getAccessToken()).isEqualTo(accessToken);
//        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
//
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
//    public void login_fail_email_not_found() {
//
//        // given
//        CustomerLoginRequest request = CustomerFixture.createLoginRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> customerService.login(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
//
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - 비밀번호 불일치")
//    public void login_fail_invalid_password() {
//
//        // given
//        CustomerLoginRequest request = CustomerFixture.createLoginRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(encoder.matches(request.getPassword(), customer.getPassword()))
//                .willReturn(false);
//
//
//        // then
//        assertThatThrownBy(() -> customerService.login(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_PASSWORD.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - accessToken이 null인 경우")
//    public void login_fail_access_token_null() {
//
//        // given
//        CustomerLoginRequest request = CustomerFixture.createLoginRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(encoder.matches(request.getPassword(), customer.getPassword()))
//                .willReturn(true);
//
//        given(jwtUtils.createAccessToken(request.getEmail()))
//                .willReturn(null);
//
//        given(jwtUtils.createRefreshToken(request.getEmail()))
//                .willReturn(refreshToken);
//
//        // when & then
//        assertThatThrownBy(() -> customerService.login(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_TOKEN.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그인 실패 - refresh token이 null인 경우")
//    public void login_fail_refresh_token_null() {
//
//        // given
//        CustomerLoginRequest request = CustomerFixture.createLoginRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(encoder.matches(request.getPassword(), customer.getPassword()))
//                .willReturn(true);
//
//
//        given(jwtUtils.createAccessToken(request.getEmail()))
//                .willReturn(accessToken);
//
//        given(jwtUtils.createRefreshToken(request.getEmail()))
//                .willReturn(null);
//
//        // when & then
//        assertThatThrownBy(() -> customerService.login(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_TOKEN.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그아웃 성공")
//    public void logout_success() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(jwtUtils.getExpiration(request.getAccessToken()))
//                .willReturn(12000L);
//
//        // when
//        MessageResponse response = customerService.logout(request, customer.getEmail());
//
//        // then
//        assertThat(response.getMsg()).isEqualTo("로그아웃 되었습니다.");
//
//    }
//
//    @Test
//    @DisplayName("로그아웃 실패 - 존재하지 않는 이메일")
//    public void logout_fail_email_not_found() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> customerService.logout(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그아웃 실패 - 만료된 토큰")
//    public void logout_fail_expired_token() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(jwtUtils.isExpired(request.getAccessToken()))
//                .willReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> customerService.logout(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_TOKEN.getMessage());
//    }
//
//    @Test
//    @DisplayName("로그아웃 실패 - 유효하지 않은 토큰")
//    public void logout_fail_invalid_token() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(jwtUtils.isExpired(request.getAccessToken()))
//                .willReturn(false);
//
//        given(jwtUtils.isValid(request.getAccessToken()))
//                .willReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> customerService.logout(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_TOKEN.getMessage());
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 성공")
//    public void reissue_token_success() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(redisDao.getValues("RT:" + customer.getEmail()))
//                .willReturn(refreshToken);
//
//        given(jwtUtils.createAccessToken(customer.getEmail()))
//                .willReturn("newAccessToken");
//
//        given(jwtUtils.createRefreshToken(customer.getEmail()))
//                .willReturn("newRefreshToken");
//
//        // when
//        LoginResponse response = customerService.reissue(request, customer.getEmail());
//
//        // then
//        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
//        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");
//
//    }
//
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 존재하지 않는 이메일")
//    public void reissue_token_fail_email_not_found() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> customerService.reissue(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 만료된 토큰")
//    public void reissue_token_fail_expired_token() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(jwtUtils.isExpired(request.getRefreshToken()))
//                .willReturn(true);
//
//        // when & then
//        assertThatThrownBy(() -> customerService.reissue(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_TOKEN.getMessage());
//    }
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 존재하지 않는 토큰")
//    public void reissue_token_fail_missing_token() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(redisDao.getValues("RT:" + customer.getEmail()))
//                .willReturn(null);
//
//        // when & then
//        assertThatThrownBy(() -> customerService.reissue(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_REQUEST.getMessage());
//    }
//
//
//    @Test
//    @DisplayName("토큰 재발급 실패 - 토큰 불일치")
//    public void reissue_token_fail_mismatch_token() {
//
//        // given
//        TokenRequest request = CustomerFixture.createTokenRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        given(redisDao.getValues("RT:" + customer.getEmail()))
//                .willReturn("mismatchToken");
//
//        // when & then
//        assertThatThrownBy(() -> customerService.reissue(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(INVALID_TOKEN.getMessage());
//    }
//
//    @Test
//    @DisplayName("회원 정보 조회 성공")
//    public void get_customer_info_success() {
//
//        // given
//        String request = customer.getEmail();
//
//        given(customerRepository.findByEmail(request))
//                .willReturn(Optional.of(customer));
//
//        // when
//        CustomerInfoResponse response = customerService.getInfo(request);
//
//        // then
//        assertThat(response.getEmail()).isEqualTo(customer.getEmail());
//        assertThat(response.getUserName()).isEqualTo(customer.getUserName());
//        assertThat(response.getNickName()).isEqualTo(customer.getNickName());
//
//    }
//
//    @Test
//    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 이메일")
//    public void get_customer_info_fail_email_not_found() {
//
//        // given
//        String request = customer.getEmail();
//
//        given(customerRepository.findByEmail(request))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> customerService.getInfo(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
//
//    }
//
//    @Test
//    @DisplayName("회원 정보 수정 성공")
//    public void modify_customer_info_success() {
//
//        // given
//        CustomerModifyRequest request = CustomerFixture.createModifyRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        // when
//        Long modifyCustomerId = customerService.modify(request, customer.getEmail());
//
//        // then
//        assertThat(modifyCustomerId).isEqualTo(customer.getId());
//
//    }
//
//    @Test
//    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 이메일")
//    public void modify_customer_info_fail_email_not_found() {
//
//        // given
//        CustomerModifyRequest request = CustomerFixture.createModifyRequest();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> customerService.modify(request, customer.getEmail()))
//                .isInstanceOf(AppException.class)
//                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
//
//    }
//
//    @Test
//    @DisplayName("회원 탈퇴 성공")
//    public void delete_customer_success() {
//
//        // given
//        String request = customer.getEmail();
//
//        given(customerRepository.findByEmail(customer.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        // when
//        Long deleteCustomerId = customerService.delete(request);
//
//        // then
//        assertThat(deleteCustomerId).isEqualTo(customer.getId());
//
//    }
//
//    @Test
//    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 이메일")
//    public void delete_customer_fail_email_not_found() {
//
//        // given
//        String request = customer.getEmail();
//
//        given(customerRepository.findByEmail(request))
//                .willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> customerService.delete(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(CUSTOMER_NOT_FOUND.getMessage());
//
//    }
//
//    @Test
//    @DisplayName("이메일 중복 체크 성공")
//    public void check_email_duplicate_success() {
//
//        // given
//        CustomerEmailCheckRequest request = CustomerFixture.createEmailCheckRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.empty());
//
//        // when
//        String msg = customerService.emailCheck(request);
//
//        // then
//        assertThat(msg).isEqualTo("사용 가능한 이메일 입니다.");
//
//    }
//
//    @Test
//    @DisplayName("이메일 중복 체크 실패")
//    public void check_email_duplicate_fail() {
//
//        // given
//        CustomerEmailCheckRequest request = CustomerFixture.createEmailCheckRequest();
//
//        given(customerRepository.findByEmail(request.getEmail()))
//                .willReturn(Optional.of(customer));
//
//        // when & then
//        assertThatThrownBy(() -> customerService.emailCheck(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(DUPLICATE_EMAIL.getMessage());
//
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 체크 성공")
//    public void check_nickname_duplicate_success() {
//
//        // given
//        CustomerNickNameCheckRequest request = CustomerFixture.createNickNameCheckRequest();
//
//        given(customerRepository.findByNickName(request.getNickName()))
//                .willReturn(Optional.empty());
//
//        // when
//        String msg = customerService.nickNameCheck(request);
//
//        // then
//        assertThat(msg).isEqualTo("사용 가능한 닉네임 입니다.");
//
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 체크 실패")
//    public void check_nickname_duplicate_fail() {
//
//        // given
//        CustomerNickNameCheckRequest request = CustomerFixture.createNickNameCheckRequest();
//
//        given(customerRepository.findByNickName(request.getNickName()))
//                .willReturn(Optional.of(customer));
//
//        // when & then
//        assertThatThrownBy(() -> customerService.nickNameCheck(request))
//                .isInstanceOf(AppException.class)
//                .hasMessage(DUPLICATE_NICKNAME.getMessage());
//
//    }
//
//
//}