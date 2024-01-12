package store.myproject.onlineshop.domain.corporation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import store.myproject.onlineshop.domain.corporation.Corporation;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CorporationJoinRequest {


    @NotBlank
    private String companyName;

    @NotBlank
    private String companyTel;

    @NotBlank
    private String registrationNumber;

    @Email
    private String companyEmail;

    @NotBlank
    private String password;


    public Corporation toEntity(String password) {
        return Corporation.builder()
                .companyEmail(this.companyEmail)
                .companyName(this.companyName)
                .companyTel(this.companyTel)
                .password(password)
                .registrationNumber(this.registrationNumber)
                .build();
    }
}
