package store.myproject.onlineshop.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 주문 아이템 DTO")
public class OrderItemRequest {

    @NotBlank(message = "아이템 UUID는 필수입니다.")
    @Schema(description = "아이템 UUID", example = "d39fcb28-7d7b-4d89-bdb7-1f22f9a18a88", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID itemUuid;

    @Schema(description = "아이템 수량", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long itemCnt;
}
