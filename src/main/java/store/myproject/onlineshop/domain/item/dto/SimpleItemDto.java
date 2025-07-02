package store.myproject.onlineshop.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@Schema(description = "간단한 품목 정보 DTO")
public class SimpleItemDto {

    @Schema(description = "품목 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID uuid;

    @Schema(description = "품목 이름", example = "Onion")
    private String itemName;

    @Schema(description = "품목 가격", example = "3200")
    private BigDecimal price;

    @Schema(description = "품목 썸네일 이미지 URL", example = "image1.jpg")
    private String thumbnail;

    @Schema(description = "품목 브랜드 이름", example = "풀무원")
    private String brandName;

    @QueryProjection
    public SimpleItemDto(UUID uuid, String itemName, BigDecimal price, String thumbnail, String brandName) {
        this.uuid = uuid;
        this.itemName = itemName;
        this.price = price;
        this.thumbnail = thumbnail;
        this.brandName = brandName;
    }
}
