package store.myproject.onlineshop.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CancelItemRequest {
    private UUID itemUuid;
}