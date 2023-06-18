package store.myproject.onlineshop.fixture;

import store.myproject.onlineshop.domain.entity.Address;
import store.myproject.onlineshop.domain.entity.Customer;


import java.time.LocalDateTime;

import static store.myproject.onlineshop.domain.enums.Gender.MALE;

public class CustomerInfoFixture {

    public static Customer get(String email, String nickName, String password) {
        Customer customer = Customer.builder()
                .id(1L)
                .email(email)
                .password(password)
                .userName("test")
                .nickName(nickName)
                .gender(MALE)
                .tel("010-1234-5678")
                .address(Address.builder()
                        .city("서울특별시")
                        .street("시흥대로 589-8")
                        .detail("1601호")
                        .zipcode("07445")
                        .build())
                .build();

        customer.setCreatedDate(LocalDateTime.now());
        return customer;
    }

}
