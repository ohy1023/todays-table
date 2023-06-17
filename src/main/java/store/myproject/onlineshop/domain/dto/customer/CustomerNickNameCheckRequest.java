package store.myproject.onlineshop.domain.dto.customer;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerNickNameCheckRequest {

    @NotBlank(message = "닉네임이 Null 또는 공백일 수 없습니다.")
    private String nickName;
}
