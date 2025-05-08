package store.myproject.onlineshop.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "품목 정보 수정 요청 DTO")
public class ItemUpdateRequest {

    @NotBlank(message = "아이템 이름은 비어 있을 수 없습니다.")
    @Schema(description = "아이템 이름", example = "스마트폰", required = true)
    private String itemName;

    @NotNull(message = "가격은 비어 있을 수 없습니다.")
    @Schema(description = "가격", example = "299.99", required = true)
    private BigDecimal price;

    @NotNull(message = "재고 수량은 비어 있을 수 없습니다.")
    @Schema(description = "재고 수량", example = "100", required = true)
    private Long stock;

    @NotBlank(message = "브랜드 이름은 비어 있을 수 없습니다.")
    @Schema(description = "브랜드 이름", example = "애플", required = true)
    private String brandName;

}
