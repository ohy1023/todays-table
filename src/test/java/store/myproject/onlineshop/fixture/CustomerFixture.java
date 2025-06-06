package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import org.instancio.Instancio;
import store.myproject.onlineshop.domain.customer.*;
import store.myproject.onlineshop.domain.customer.dto.*;
import store.myproject.onlineshop.domain.membership.MemberShip;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Locale;

import static org.instancio.Select.field;

public class CustomerFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    // 기본 Customer 생성 (구매 금액 0)
    public static Customer createCustomer() {
        return createCustomerWithPurchaseAmount(BigDecimal.ZERO);
    }

    public static Customer createCustomerEntity() {
        Customer customer = Customer.builder()
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

        customer.setCreatedDate(LocalDateTime.now());

        return customer;
    }

    // 구매 금액을 설정할 수 있는 Customer 생성
    public static Customer createCustomerWithPurchaseAmount(BigDecimal purchaseAmount) {

        Customer customer = Customer.builder()
                .id(1L)
                .email(faker.internet().emailAddress())
                .password(faker.internet().password())
                .userName(faker.name().firstName())
                .nickName(faker.name().username())
                .gender(faker.options().option(Gender.class))
                .tel(faker.phoneNumber().cellPhone())
                .address(Address.builder()
                        .city(faker.address().city())
                        .street(faker.address().streetName())
                        .detail(faker.address().secondaryAddress())
                        .zipcode(faker.address().zipCode())
                        .build())
                .customerRole(CustomerRole.ROLE_USER)
                .monthlyPurchaseAmount(purchaseAmount)
                .build();

        customer.setCreatedDate(LocalDateTime.now());

        return customer;
    }


    public static CustomerJoinRequest createJoinRequest() {
        return Instancio.of(CustomerJoinRequest.class)
                .set(field("email"), faker.internet().emailAddress())
                .set(field("password"), "1Q2w3e4r!!")
                .set(field("userName"), faker.name().fullName())
                .set(field("nickName"), faker.name().username())
                .set(field("gender"), Gender.MALE)
                .set(field("tel"), "010-" + faker.number().digits(4) + "-" + faker.number().digits(4))
                .set(field("city"), faker.address().city())
                .set(field("street"), faker.address().streetAddress())
                .set(field("detail"), "상세 " + faker.number().numberBetween(100, 200) + "호")
                .set(field("zipcode"), faker.address().zipCode())
                .create();
    }

    public static CustomerJoinRequest createInvalidJoinRequest(String email) {
        return Instancio.of(CustomerJoinRequest.class)
                .set(field("email"), email)
                .set(field("password"), "1Q2w3e4r!!")
                .set(field("userName"), faker.name().fullName())
                .set(field("nickName"), faker.name().username())
                .set(field("gender"), Gender.MALE)
                .set(field("tel"), "010-" + faker.number().digits(4) + "-" + faker.number().digits(4))
                .set(field("city"), faker.address().city())
                .set(field("street"), faker.address().streetAddress())
                .set(field("detail"), "상세 " + faker.number().numberBetween(100, 200) + "호")
                .set(field("zipcode"), faker.address().zipCode())
                .create();
    }

    public static CustomerLoginRequest createLoginRequest() {
        return Instancio.of(CustomerLoginRequest.class)
                .set(field("email"), faker.internet().emailAddress())
                .set(field("password"), "1Q2w3e4r!!")
                .create();
    }

    public static CustomerModifyRequest createModifyRequest() {
        return Instancio.create(CustomerModifyRequest.class);
    }

    public static TokenRequest createTokenRequest() {
        return Instancio.of(TokenRequest.class)
                .set(field("accessToken"), "accessToken")
                .set(field("refreshToken"), "refreshToken")
                .create();
    }

    public static CustomerEmailCheckRequest createEmailCheckRequest() {
        return new CustomerEmailCheckRequest(faker.internet().emailAddress());
    }

    public static CustomerNickNameCheckRequest createNickNameCheckRequest() {
        return new CustomerNickNameCheckRequest(faker.name().username());
    }

    public static CustomerChangePasswordRequest createChangePasswordRequest(String curPassword) {
        return new CustomerChangePasswordRequest(curPassword, "newPassword12!!");
    }

    public static CustomerTempPasswordRequest createTempPasswordRequest() {
        return new CustomerTempPasswordRequest(faker.internet().emailAddress(), "010-1234-5678");
    }

    public static LoginResponse createLoginResponse() {
        return LoginResponse.builder()
                .accessToken("accessToken")
                .refreshToken("refreshToken")
                .build();
    }

    public static CustomerInfoResponse createCustomerInfoResponse() {
        return CustomerInfoResponse.builder()
                .email("test@naver.com")
                .userName("test")
                .nickName("test")
                .gender(Gender.MALE)
                .tel("010-1234-5678")
                .createdDate("2023-06-07")
                .address(Address.builder()
                        .city("서울특별시")
                        .street("시흥대로 589-8")
                        .detail("1601호")
                        .zipcode("07445")
                        .build())
                .build();
    }

    public static CustomerTempPasswordResponse createTempPasswordResponse(String email) {
        return new CustomerTempPasswordResponse(email, "tempPassword");
    }

}
