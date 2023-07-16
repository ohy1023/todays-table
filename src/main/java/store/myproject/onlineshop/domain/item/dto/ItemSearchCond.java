package store.myproject.onlineshop.domain.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
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
