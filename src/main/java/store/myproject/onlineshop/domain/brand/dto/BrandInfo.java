package store.myproject.onlineshop.domain.brand.dto;

import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "브랜드 정보 DTO")
public class BrandInfo {

    @Schema(description = "브랜드 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID uuid;

    @Schema(description = "브랜드 이름", example = "풀무원")
    private String name;

    @Schema(description = "브랜드 이미지 URL", example = "https://example.com/brand/nike.jpg")
    private String brandImgUrl;

    @QueryProjection
    public BrandInfo(UUID uuid, String name, String brandImgUrl) {
        this.uuid = uuid;
        this.name = name;
        this.brandImgUrl = brandImgUrl;
    }

}