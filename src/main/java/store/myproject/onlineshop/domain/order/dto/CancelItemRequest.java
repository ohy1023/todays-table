package store.myproject.onlineshop.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 품목 취소 요청 DTO")
public class CancelItemRequest {

    @Schema(description = "취소할 상품 UUID", example = "13dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
    @NotNull(message = "상품 UUID는 필수입니다.")
    private UUID itemUuid;
}