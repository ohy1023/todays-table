package store.myproject.onlineshop.domain.customer.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTempPasswordRequest {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    private String tel;
}