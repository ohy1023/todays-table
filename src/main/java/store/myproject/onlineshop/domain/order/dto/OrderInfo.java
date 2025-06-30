package store.myproject.onlineshop.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 정보 DTO")
public class OrderInfo {

    @Schema(description = "주문 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID merchantUid;

    @Schema(description = "브랜드 이름", example = "풀무원")
    private String brandName;

    @Schema(description = "상품 번호",  example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID itemUuid;

    @Schema(description = "상품 이름", example = "대파")
    private String itemName;

    @Schema(description = "주문 일자", example = "2025-05-08")
    private String orderDate;

    @Schema(description = "주문 상태", example = "배송 중")
    private String orderStatus;

    @Schema(description = "주문자 이름", example = "홍길동")
    private String orderCustomerName;

    @Schema(description = "주문자 연락처", example = "010-1234-5678")
    private String orderCustomerTel;

    @Schema(description = "수취인 이름", example = "김철수")
    private String recipientName;

    @Schema(description = "수취인 연락처", example = "010-9876-5432")
    private String recipientTel;

    @Schema(description = "수취인 주소", example = "서울특별시 강남구 테헤란로 123")
    private String recipientAddress;

    @Schema(description = "우편번호", example = "12345")
    private String zipcode;

    @Schema(description = "배송 상태", example = "배송 완료")
    private String deliveryStatus;

    @Schema(description = "총 가격", example = "50000")
    private BigDecimal totalPrice;
}
