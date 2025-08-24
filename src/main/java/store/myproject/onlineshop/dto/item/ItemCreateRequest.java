package store.myproject.onlineshop.dto.item;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "품목 등록 요청 DTO")
public class ItemCreateRequest {

    @NotBlank(message = "아이템 이름은 필수입니다.")
    @Schema(description = "아이템 이름", example = "대파", requiredMode = Schema.RequiredMode.REQUIRED)
    private String itemName;

    @NotNull(message = "가격은 필수입니다.")
    @Schema(description = "가격", example = "3200", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal itemPrice;

    @NotNull(message = "재고는 필수입니다.")
    @Schema(description = "재고 수량", example = "10000", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long stock;

    @NotBlank(message = "브랜드 이름은 필수입니다.")
    @Schema(description = "브랜드 이름", example = "풀무원", requiredMode = Schema.RequiredMode.REQUIRED)
    private String brandName;


    public Item toEntity(Brand brand) {
        return Item.builder()
                .uuid(UUIDGenerator.generateUUIDv7())
                .itemName(this.itemName)
                .itemPrice(this.itemPrice)
                .stock(this.stock)
                .brand(brand)
                .build();
    }
}
