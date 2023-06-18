package store.myproject.onlineshop.service;

import org.assertj.core.api.AbstractThrowableAssert;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import store.myproject.onlineshop.domain.dto.customer.CustomerJoinRequest;
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
                .willThrow(new AppException(DUPLICATE_NICKNAME, DUPLICATE_NICKNAME.getMessage()));

        AbstractThrowableAssert<?, ? extends Throwable> o = assertThatThrownBy(() -> customerService.join(request))
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
                .willThrow(new AppException(DUPLICATE_EMAIL, DUPLICATE_EMAIL.getMessage()));

        assertThatThrownBy(() -> customerService.join(request))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_EMAIL.getMessage());
    }


}