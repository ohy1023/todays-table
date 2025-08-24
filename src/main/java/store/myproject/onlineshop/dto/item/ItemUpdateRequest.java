package store.myproject.onlineshop.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "품목 정보 수정 요청 DTO")
public class ItemUpdateRequest {

    @Schema(description = "아이템 이름", example = "Onion")
    private String itemName;

    @Schema(description = "가격", example = "299.99")
    private BigDecimal price;

    @Schema(description = "재고 수량", example = "100")
    private Long stock;

    @Schema(description = "브랜드 이름", example = "애플")
    private String brandName;

}
