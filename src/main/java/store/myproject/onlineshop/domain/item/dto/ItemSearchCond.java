package store.myproject.onlineshop.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "아이템 검색 조건 DTO")
public class ItemSearchCond {

    @Schema(description = "아이템 이름", example = "Salt")
    private String itemName;

    @Schema(description = "브랜드 이름", example = "유한")
    private String brandName;

}
