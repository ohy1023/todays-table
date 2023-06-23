package store.myproject.onlineshop.domain.dto.customer;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.entity.Address;
import store.myproject.onlineshop.domain.entity.Customer;
import store.myproject.onlineshop.domain.enums.Gender;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerJoinRequest {
    @Email
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String nickName;

    @NotBlank
    private String userName;

    @NotBlank
    private String tel;

    @NotNull
    private Gender gender;

    @NotBlank
    private String city;

    @NotBlank
    private String street;

    @NotBlank
    private String detail;

    @NotBlank
    private String zipcode;

    public Customer toEntity(String encodedPassword) {
        return Customer.builder()
                .email(this.email)
                .nickName(this.nickName)
                .userName(this.userName)
                .password(encodedPassword)
                .tel(this.tel)
                .gender(this.gender)
                .address(Address.builder()
                        .city(this.city)
                        .street(this.street)
                        .detail(this.detail)
                        .zipcode(this.zipcode)
                        .build()
                )
                .build();
    }


}
