package store.myproject.onlineshop.domain.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "배송지 수정 요청 DTO")
public class DeliveryUpdateRequest {

    @Schema(description = "수취인의 이름", example = "홍길동", required = true)
    @NotBlank(message = "수취인 이름은 필수입니다.")
    private String recipientName;

    @Schema(description = "수취인의 전화번호", example = "010-1234-5678", required = true)
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^010-\\d{4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다. 예: 010-1234-5678")
    private String recipientTel;

    @Schema(description = "도시", example = "서울시", required = true)
    @NotBlank(message = "도시는 필수입니다.")
    private String city;

    @Schema(description = "거리 주소", example = "강남구 테헤란로", required = true)
    @NotBlank(message = "거리 주소는 필수입니다.")
    private String street;

    @Schema(description = "상세 주소", example = "6층 602호", required = true)
    @NotBlank(message = "상세 주소는 필수입니다.")
    private String detail;

    @Schema(description = "우편번호", example = "12345", required = true)
    @NotBlank(message = "우편번호는 필수입니다.")
    @Size(min = 5, max = 5, message = "우편번호는 5자리여야 합니다.")
    private String zipcode;
}
