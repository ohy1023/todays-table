package store.myproject.onlineshop.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemSearchCond {

    private String itemName;

    private String brandName;

    private Long priceGoe;

    private Long priceLoe;

    private Long stockGoe;

    private Long stockLoe;

    @Schema(description = "검색 시작 날짜",type = "string", example = "2023-07-16T00:00:00")
    private LocalDateTime startDate;

    @Schema(description = "검색 종료 날짜",type = "string", example = "2023-07-17T00:00:00")
    private LocalDateTime endDate;

}
