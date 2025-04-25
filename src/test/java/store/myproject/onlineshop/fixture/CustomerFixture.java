package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import org.instancio.Instancio;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.customer.Gender;
import store.myproject.onlineshop.domain.customer.dto.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.instancio.Select.field;

public class CustomerFixture {

    private static final Faker faker = new Faker();

    /**
     * 고정된 값 기반 Customer 객체 생성
     */
    public static Customer createCustomer() {
        Faker faker = new Faker();

        String email = faker.internet().emailAddress();
        String nickName = faker.name().username();
        String password = faker.internet().password();
        Gender gender = faker.options().option(Gender.class); // MALE 또는 FEMALE 랜덤 선택

        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .password(password)
                .userName(faker.name().firstName())
                .nickName(nickName)
                .gender(gender)
                .tel(faker.phoneNumber().cellPhone())
                .address(Address.builder()
                        .city(faker.address().city())
                        .street(faker.address().streetName())
                        .detail(faker.address().secondaryAddress())
                        .zipcode(faker.address().zipCode())
                        .build())
                .customerRole(CustomerRole.ROLE_USER)
                .totalPurchaseAmount(BigDecimal.ZERO)
                .build();

        customer.setCreatedDate(LocalDateTime.now());

        return customer;
    }

//    public static Customer createCustomer(String email, String nickName, String password) {
//        Customer customer = Customer.builder()
//                .id(1L)
//                .email(email)
//                .password(password)
//                .userName("test")
//                .nickName(nickName)
//                .gender(MALE)
//                .tel("010-1234-5678")
//                .address(Address.builder()
//                        .city("서울특별시")
//                        .street("시흥대로 589-8")
//                        .detail("1601호")
//                        .zipcode("07445")
//                        .build())
//                .build();
//
//        customer.setCreatedDate(LocalDateTime.now());
//        return customer;
//    }


    public static CustomerJoinRequest createJoinRequest() {
        return Instancio.of(CustomerJoinRequest.class)
                .set(field("email"), faker.internet().emailAddress())
                .set(field("password"), faker.internet().password())
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
                .set(field("password"), faker.internet().password())
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
                .set(field("password"), faker.internet().password())
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

    public static CustomerChangePasswordRequest createChangePasswordRequest() {
        return new CustomerChangePasswordRequest("password", "newPassword");
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
