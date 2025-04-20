package store.myproject.onlineshop.domain.item.dto;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemUpdateRequest {
    private String itemName;

    private BigDecimal price;

    private Long stock;

    private String brandName;

}
