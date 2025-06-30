package store.myproject.onlineshop.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 요청 DTO")
public class OrderInfoRequest {

    @NotNull(message = "상품 UUID는 필수입니다.")
    @Schema(description = "주문할 상품 UUID", example = "13dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
    private UUID itemUuid;

    @NotNull(message = "주문 UUID는 필수입니다.")
    @Schema(description = "주문 UUID", example = "11dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
    private UUID merchantUid;

    @NotNull(message = "상품 수량은 필수입니다.")
    @Min(value = 1, message = "상품 수량은 최소 1개 이상이어야 합니다.")
    @Schema(description = "주문할 상품 수량", example = "2", required = true)
    private Long itemCnt;

    @NotNull(message = "수령인 이름은 필수입니다.")
    @Schema(description = "수령인 이름", example = "홍길동", required = true)
    private String recipientName;

    @NotNull(message = "수령인 전화번호는 필수입니다.")
    @Schema(description = "수령인 전화번호", example = "010-1234-5678", required = true)
    private String recipientTel;

    @NotNull(message = "도시는 필수입니다.")
    @Schema(description = "배송지 도시", example = "서울시", required = true)
    private String recipientCity;

    @NotNull(message = "도로명 주소는 필수입니다.")
    @Schema(description = "배송지 도로명 주소", example = "강남대로 123", required = true)
    private String recipientStreet;

    @NotNull(message = "상세 주소는 필수입니다.")
    @Schema(description = "배송지 상세 주소", example = "101동 202호", required = true)
    private String recipientDetail;

    @NotNull(message = "우편번호는 필수입니다.")
    @Schema(description = "배송지 우편번호", example = "06236", required = true)
    private String recipientZipcode;

    public DeliveryInfoRequest toDeliveryInfoRequest() {
        return DeliveryInfoRequest
                .builder()
                .recipientName(this.getRecipientName())
                .recipientTel(this.getRecipientTel())
                .recipientCity(this.getRecipientCity())
                .recipientZipcode(this.getRecipientZipcode())
                .recipientDetail(this.getRecipientDetail())
                .recipientStreet(this.getRecipientStreet())
                .build();
    }
}
