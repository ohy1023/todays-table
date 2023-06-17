package store.myproject.onlineshop.domain.dto.customer;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import store.myproject.onlineshop.domain.enums.Gender;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerModifyRequest {

    @NotNull
    private String nickName;

    @NotNull
    private String userName;

    @NotNull
    private String tel;

    @NotNull
    private String city;

    @NotNull
    private String street;

    @NotNull
    private String detail;

    @NotNull
    private String zipcode;

}
