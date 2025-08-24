package store.myproject.onlineshop.dto.cart;

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
    @Schema(description = "추가할 품목 UUID", example = "cffb8f4d-2be3-11f0-bff7-453261748c60", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID itemUuid;

    @NotNull
    @Schema(description = "추가할 수량", example = "3", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long itemCnt;
}
