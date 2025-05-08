package store.myproject.onlineshop.domain.cart.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "장바구니 추가 요청 DTO")
public class CartAddRequest {

    @NotNull
    @Schema(description = "추가할 품목 UUID", example = "550e8400-e29b-41d4-a716-446655440000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID itemUuid;

    @NotNull
    @Schema(description = "추가할 수량", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long itemCnt;
}
