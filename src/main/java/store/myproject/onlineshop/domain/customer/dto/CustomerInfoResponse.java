package store.myproject.onlineshop.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import store.myproject.onlineshop.domain.customer.Address;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.domain.customer.Gender;

import java.time.format.DateTimeFormatter;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "회원 정보 응답 DTO")
public class CustomerInfoResponse {

    private String email;
    private String nickName;
    private String userName;
    private String tel;
    private Address address;
    private Gender gender;
    private String createdDate;

    public static CustomerInfoResponse toDto(Customer savedCustomer) {

        return CustomerInfoResponse.builder()
                .createdDate(savedCustomer.getCreatedDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")))
                .email(savedCustomer.getEmail())
                .nickName(savedCustomer.getNickName())
                .userName(savedCustomer.getUserName())
                .tel(savedCustomer.getTel())
                .gender(savedCustomer.getGender())
                .address(Address.builder()
                        .city(savedCustomer.getAddress().getCity())
                        .street(savedCustomer.getAddress().getStreet())
                        .detail(savedCustomer.getAddress().getDetail())
                        .zipcode(savedCustomer.getAddress().getZipcode())
                        .build())
                .build();
    }
}
