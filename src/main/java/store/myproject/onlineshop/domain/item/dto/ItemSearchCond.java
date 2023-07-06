package store.myproject.onlineshop.domain.item.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ItemSearchCond {

    private String itemName;

    private String brandName;

    private Long priceGoe;

    private Long priceLoe;

    private Long stockGoe;

    private Long stockLoe;

    private LocalDateTime startDate;

    private LocalDateTime endDate;
}
