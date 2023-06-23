package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import store.myproject.onlineshop.domain.dto.customer.*;
import store.myproject.onlineshop.domain.entity.Customer;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.CustomerInfoFixture;
import store.myproject.onlineshop.global.redis.RedisDao;
import store.myproject.onlineshop.global.utils.JwtUtils;
import store.myproject.onlineshop.repository.CustomerRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static store.myproject.onlineshop.domain.enums.Gender.MALE;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private BCryptPasswordEncoder encoder;

    @Mock
    private RedisDao redisDao;

    @Mock
    private JwtUtils jwtUtils;

    @InjectMocks
    private CustomerService customerService;

    Customer customer1 = CustomerInfoFixture.get("test@naver.com", "customer1", "test");
    String accessToken = "accessToken";
    String refreshToken = "refreshToken";

    @Test
    @DisplayName("회원가입 성공")
    public void join_success() {

        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test@naver.com")
                .password("test")
                .userName("test")
                .nickName("customer1")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();


        given(customerRepository.findByNickName(customer1.getNickName()))
                .willReturn(Optional.empty());

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.empty());

        given(customerRepository.save(any(Customer.class)))
                .willReturn(customer1);


        // when
        String email = customerService.join(request);

        // then
        assertThat(email).isEqualTo(customer1.getEmail());


    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    public void join_fail_nickNameDuplicate() {

        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test@naver.com")
                .password("test")
                .userName("test")
                .nickName("customer1")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();

        given(customerRepository.findByNickName(customer1.getNickName()))
                .willReturn(Optional.of(customer1));

        // when & then
        assertThatThrownBy(() -> customerService.join(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_NICKNAME.getMessage());

    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    public void join_fail_emailDuplicate() {

        // given
        CustomerJoinRequest request = CustomerJoinRequest.builder()
                .email("test@naver.com")
                .password("test")
                .userName("test")
                .nickName("customer1")
                .gender(MALE)
                .tel("010-1234-5678")
                .city("서울특별시")
                .street("시흥대로 589-8")
                .detail("1601호")
                .zipcode("07445")
                .build();

        given(customerRepository.findByNickName(customer1.getNickName()))
                .willReturn(Optional.empty());

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        // when & then
        assertThatThrownBy(() -> customerService.join(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_EMAIL.getMessage());
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_success() {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer1));

        given(encoder.matches(request.getPassword(), customer1.getPassword()))
                .willReturn(true);

        given(jwtUtils.createAccessToken(request.getEmail()))
                .willReturn(accessToken);

        given(jwtUtils.createRefreshToken(request.getEmail()))
                .willReturn(refreshToken);

        // when
        CustomerLoginResponse response = customerService.login(request);

        // then
        assertThat(response.getAccessToken()).isEqualTo(accessToken);
        assertThat(response.getRefreshToken()).isEqualTo(refreshToken);

    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    public void login_fail_notFoundEmail() {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(EMAIL_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    public void login_fail_invalidPassword() {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer1));

        given(encoder.matches(request.getPassword(), customer1.getPassword()))
                .willReturn(false);


        // then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_PASSWORD.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - accessToken이 null인 경우")
    public void login_fail_accessTokenNull() {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer1));

        given(encoder.matches(request.getPassword(), customer1.getPassword()))
                .willReturn(true);

        given(jwtUtils.createAccessToken(request.getEmail()))
                .willReturn(null);

        given(jwtUtils.createRefreshToken(request.getEmail()))
                .willReturn(refreshToken);

        // when & then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그인 실패 - refresh token이 null인 경우")
    public void login_fail_refreshTokenNull() {

        // given
        CustomerLoginRequest request = CustomerLoginRequest.builder()
                .email("test@naver.com")
                .password("test")
                .build();

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer1));

        given(encoder.matches(request.getPassword(), customer1.getPassword()))
                .willReturn(true);


        given(jwtUtils.createAccessToken(request.getEmail()))
                .willReturn(accessToken);

        given(jwtUtils.createRefreshToken(request.getEmail()))
                .willReturn(null);

        // when & then
        assertThatThrownBy(() -> customerService.login(request))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃 성공")
    public void logout_success() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(jwtUtils.getExpiration(request.getAccessToken()))
                .willReturn(12000L);

        // when
        String msg = customerService.logout(request, customer1.getEmail());

        // then
        assertThat(msg).isEqualTo("로그아웃 되었습니다.");

    }

    @Test
    @DisplayName("로그아웃 실패 - 존재하지 않는 이메일")
    public void logout_fail_notFoundEmail() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.logout(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(EMAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 - 만료된 토큰")
    public void logout_fail_expiredToken() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(jwtUtils.isExpired(request.getAccessToken()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> customerService.logout(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("로그아웃 실패 - 유효하지 않은 토큰")
    public void logout_fail_invalidToken() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(jwtUtils.isExpired(request.getAccessToken()))
                .willReturn(false);

        given(jwtUtils.isValid(request.getAccessToken()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> customerService.logout(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 성공")
    public void reissue_success() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(redisDao.getValues("RT:" + customer1.getEmail()))
                .willReturn(refreshToken);

        given(jwtUtils.createAccessToken(customer1.getEmail()))
                .willReturn("newAccessToken");

        given(jwtUtils.createRefreshToken(customer1.getEmail()))
                .willReturn("newRefreshToken");

        // when
        CustomerLoginResponse response = customerService.reissue(request, customer1.getEmail());

        // then
        assertThat(response.getAccessToken()).isEqualTo("newAccessToken");
        assertThat(response.getRefreshToken()).isEqualTo("newRefreshToken");

    }


    @Test
    @DisplayName("토큰 재발급 실패 - 존재하지 않는 이메일")
    public void reissue_fail_notFoundEmail() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.reissue(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(EMAIL_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 만료된 토큰")
    public void reissue_fail_expiredToken() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(jwtUtils.isExpired(request.getRefreshToken()))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() -> customerService.reissue(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("토큰 재발급 실패 - 존재하지 않는 토큰")
    public void reissue_fail_tokenNull() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(redisDao.getValues("RT:" + customer1.getEmail()))
                .willReturn(null);

        // when & then
        assertThatThrownBy(() -> customerService.reissue(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_REQUEST.getMessage());
    }


    @Test
    @DisplayName("토큰 재발급 실패 - 토큰 불일치")
    public void reissue_fail_mismatchedToken() {

        // given
        CustomerTokenRequest request = CustomerTokenRequest.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        given(redisDao.getValues("RT:" + customer1.getEmail()))
                .willReturn("mismatchToken");

        // when & then
        assertThatThrownBy(() -> customerService.reissue(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(INVALID_TOKEN.getMessage());
    }

    @Test
    @DisplayName("회원 정보 조회 성공")
    public void info_success() {

        // given
        String request = "test@naver.com";

        given(customerRepository.findByEmail(request))
                .willReturn(Optional.of(customer1));

        // when
        CustomerInfoResponse response = customerService.getInfo(request);

        // then
        assertThat(response.getEmail()).isEqualTo(customer1.getEmail());
        assertThat(response.getUserName()).isEqualTo(customer1.getUserName());
        assertThat(response.getNickName()).isEqualTo(customer1.getNickName());

    }

    @Test
    @DisplayName("회원 정보 조회 실패 - 존재하지 않는 이메일")
    public void info_fail_notFoundEmail() {

        // given
        String request = "test@naver.com";

        given(customerRepository.findByEmail(request))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.getInfo(request))
                .isInstanceOf(AppException.class)
                .hasMessage(EMAIL_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    public void modify_success() {

        // given
        CustomerModifyRequest request = CustomerModifyRequest.builder()
                .userName("newTest")
                .tel("010-5678-1234")
                .nickName("newTest")
                .street("test")
                .city("test")
                .zipcode("test")
                .detail("test")
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        // when
        Long modifyCustomerId = customerService.modify(request, customer1.getEmail());

        // then
        assertThat(modifyCustomerId).isEqualTo(customer1.getId());

    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 존재하지 않는 이메일")
    public void modify_fail_notFoundEmail() {

        // given
        CustomerModifyRequest request = CustomerModifyRequest.builder()
                .userName("newTest")
                .tel("010-5678-1234")
                .nickName("newTest")
                .street("test")
                .city("test")
                .zipcode("test")
                .detail("test")
                .build();

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.modify(request, customer1.getEmail()))
                .isInstanceOf(AppException.class)
                .hasMessage(EMAIL_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    public void delete_success() {

        // given
        String request = "test@naver.com";

        given(customerRepository.findByEmail(customer1.getEmail()))
                .willReturn(Optional.of(customer1));

        // when
        Long deleteCustomerId = customerService.delete(request);

        // then
        assertThat(deleteCustomerId).isEqualTo(customer1.getId());

    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 존재하지 않는 이메일")
    public void delete_fail_notFoundEmail() {

        // given
        String request = "test@naver.com";

        given(customerRepository.findByEmail(request))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> customerService.delete(request))
                .isInstanceOf(AppException.class)
                .hasMessage(EMAIL_NOT_FOUND.getMessage());

    }

    @Test
    @DisplayName("이메일 중복 체크 성공")
    public void email_check_success() {

        // given
        CustomerEmailCheckRequest request = new CustomerEmailCheckRequest("test@naver.com");

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.empty());

        // when
        String msg = customerService.emailCheck(request);

        // then
        assertThat(msg).isEqualTo("사용 가능한 이메일 입니다.");

    }

    @Test
    @DisplayName("이메일 중복 체크 실패")
    public void email_check_fail_duplicate_nickname() {

        // given
        CustomerEmailCheckRequest request = new CustomerEmailCheckRequest("test@naver.com");

        given(customerRepository.findByEmail(request.getEmail()))
                .willReturn(Optional.of(customer1));

        // when & then
        assertThatThrownBy(() -> customerService.emailCheck(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_EMAIL.getMessage());

    }

    @Test
    @DisplayName("닉네임 중복 체크 성공")
    public void nickName_check_success() {

        // given
        CustomerNickNameCheckRequest request = new CustomerNickNameCheckRequest("test");

        given(customerRepository.findByNickName(request.getNickName()))
                .willReturn(Optional.empty());

        // when
        String msg = customerService.nickNameCheck(request);

        // then
        assertThat(msg).isEqualTo("사용 가능한 닉네임 입니다.");

    }

    @Test
    @DisplayName("닉네임 중복 체크 실패")
    public void nickName_check_fail_duplicate_nickname() {

        // given
        CustomerNickNameCheckRequest request = new CustomerNickNameCheckRequest("test");

        given(customerRepository.findByNickName(request.getNickName()))
                .willReturn(Optional.of(customer1));

        // when & then
        assertThatThrownBy(() -> customerService.nickNameCheck(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_NICKNAME.getMessage());

    }


}