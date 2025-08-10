package store.myproject.onlineshop.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "로그인 응답 DTO")
public class LoginResponse {

    private String accessToken;
    private String refreshToken;


    public static LoginResponse of(final String accessToken, final String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}
