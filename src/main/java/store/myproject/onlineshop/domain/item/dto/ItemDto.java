package store.myproject.onlineshop.domain.item.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "품목 정보 응답 DTO")
public class ItemDto {

    @Schema(description = "아이템 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120", required = true)
    private UUID uuid;

    @Schema(description = "아이템 이름", example = "Onion", required = true)
    private String itemName;

    @Schema(description = "가격", example = "3200", required = true)
    private BigDecimal price;

    @Schema(description = "이미지 목록", example = "[\"image1.jpg\", \"image2.jpg\"]")
    private List<String> imageList;

    @Schema(description = "브랜드 이름", example = "풀무원", required = true)
    private String brandName;

    public static ItemDto from(Item item) {

        List<String> imageUrls = item.getImageFileList().stream()
                .map(ImageFile::getImageUrl)
                .toList();

        String brandName = item.getBrand().getBrandName();

        return ItemDto.builder()
                .uuid(item.getUuid())
                .itemName(item.getItemName())
                .price(item.getItemPrice())
                .imageList(imageUrls)
                .brandName(brandName)
                .build();
    }

}
