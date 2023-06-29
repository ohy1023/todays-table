package store.myproject.onlineshop.domain.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemSearchCond {

    private String itemName;

    private String brandName;

    private Integer priceGoe;

    private Integer priceLoe;

    private Integer stockGoe;

    private Integer stockLoe;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
