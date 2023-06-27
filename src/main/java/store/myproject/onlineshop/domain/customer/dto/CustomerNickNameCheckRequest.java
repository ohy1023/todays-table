package store.myproject.onlineshop.domain.customer.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNickNameCheckRequest {

    @NotBlank(message = "닉네임이 Null 또는 공백일 수 없습니다.")
    private String nickName;
}
