package store.myproject.onlineshop.domain.corporation.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorporationLoginRequest {

    @Email
    private String companyEmail;

    @NotBlank
    private String registrationNumber;

    @NotBlank
    private String password;
}
