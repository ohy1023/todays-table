package store.myproject.onlineshop.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "주문 롤백 DTO")
public class OrderRollbackRequest {

    @NotNull(message = "주문 UUID는 필수입니다.")
    @Schema(description = "주문 UUID", example = "11dd3e84-2b3a-11f0-9aef-59f7f88a8400", required = true)
    private UUID merchantUid;

}
