package store.myproject.onlineshop.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "주문 품목 취소 요청 DTO")
public class CancelItemRequest {

    private String impUid;

    private List<UUID> itemUuidList;
}