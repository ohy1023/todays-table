package store.myproject.onlineshop.domain.dto.customer;

import jakarta.validation.constraints.NotEmpty;
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
    @NotNull
    private String email;

    @NotNull
    private String password;

    @NotNull
    private String nickName;

    @NotNull
    private String userName;

    @NotNull
    private String tel;

    @NotNull
    private Gender gender;

    @NotNull
    private String city;

    @NotNull
    private String street;

    @NotNull
    private String detail;

    @NotNull
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
