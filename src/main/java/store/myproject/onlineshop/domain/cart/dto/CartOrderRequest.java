package store.myproject.onlineshop.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 주문 요청 DTO")
public class CartOrderRequest {

    @NotBlank(message = "수령인 이름은 필수입니다.")
    @Schema(description = "수령인 이름", example = "홍길동", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientName;

    @NotBlank(message = "수령인 전화번호는 필수입니다.")
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "전화번호 형식이 올바르지 않습니다.")
    @Schema(description = "수령인 전화번호", example = "010-1234-5678", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientTel;

    @NotBlank(message = "도시는 필수입니다.")
    @Schema(description = "수령인 도시", example = "서울특별시", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientCity;

    @NotBlank(message = "도로명 주소는 필수입니다.")
    @Schema(description = "수령인 도로명 주소", example = "강남대로 123", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientStreet;

    @NotBlank(message = "상세 주소는 필수입니다.")
    @Schema(description = "수령인 상세 주소", example = "101동 1001호", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientDetail;

    @NotBlank(message = "우편번호는 필수입니다.")
    @Pattern(regexp = "\\d{5}", message = "우편번호는 5자리 숫자여야 합니다.")
    @Schema(description = "우편번호", example = "06236", requiredMode = Schema.RequiredMode.REQUIRED)
    private String recipientZipcode;

    public DeliveryInfoRequest toDeliveryInfoRequest() {
        return DeliveryInfoRequest
                .builder()
                .recipientName(this.getRecipientName())
                .recipientTel(this.getRecipientTel())
                .recipientCity(this.getRecipientCity())
                .recipientZipcode(this.getRecipientZipcode())
                .recipientStreet(this.getRecipientStreet())
                .recipientDetail(this.getRecipientDetail())
                .build();
    }

}
