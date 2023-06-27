package store.myproject.onlineshop.domain.customer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
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
