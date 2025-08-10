package store.myproject.onlineshop.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "임시 비밀번호 발급 응답 DTO")
public class CustomerTempPasswordResponse {

    private String email;

    private String tempPassword;

    public static CustomerTempPasswordResponse of(final String email, final String tempPassword) {
        return new CustomerTempPasswordResponse(email, tempPassword);
    }

}