package store.myproject.onlineshop.domain.customer.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "회원 정보 수정 요청 DTO")
public class CustomerModifyRequest {

    @NotBlank
    @Schema(description = "닉네임", example = "콩거", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nickName;

    @NotBlank
    @Schema(description = "이름", example = "오형상", requiredMode = Schema.RequiredMode.REQUIRED)
    private String userName;

    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    @Schema(description = "전화번호", example = "01012345678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String tel;

    @NotBlank
    @Schema(description = "도시", example = "서울", requiredMode = Schema.RequiredMode.REQUIRED)
    private String city;

    @NotBlank
    @Schema(description = "거리", example = "성동구 왕십리로", requiredMode = Schema.RequiredMode.REQUIRED)
    private String street;

    @NotBlank
    @Schema(description = "상세 주소", example = "101호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String detail;

    @NotBlank
    @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다.")
    @Schema(description = "우편번호", example = "04780", requiredMode = Schema.RequiredMode.REQUIRED)
    private String zipcode;
}
