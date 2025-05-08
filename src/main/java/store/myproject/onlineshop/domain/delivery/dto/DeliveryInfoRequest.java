package store.myproject.onlineshop.domain.delivery.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "배송지 정보 요청 DTO")
public class DeliveryInfoRequest {

    @Schema(description = "수취인의 이름", example = "홍길동", required = true)
    private String recipientName;

    @Schema(description = "수취인의 전화번호", example = "010-1234-5678", required = true)
    private String recipientTel;

    @Schema(description = "수취인의 도시", example = "서울시", required = true)
    private String recipientCity;

    @Schema(description = "수취인의 거리 주소", example = "강남구 테헤란로", required = true)
    private String recipientStreet;

    @Schema(description = "수취인의 상세 주소", example = "6층 602호", required = true)
    private String recipientDetail;

    @Schema(description = "수취인의 우편번호", example = "12345", required = true)
    private String recipientZipcode;
}
