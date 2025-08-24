package store.myproject.onlineshop.dto.brand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.global.utils.UUIDGenerator;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "브랜드 수정 요청 DTO")
public class BrandUpdateRequest {

    @NotEmpty(message = "브랜드 명을 입력하세요.")
    @Schema(description = "수정할 브랜드명", example = "샘표", requiredMode = Schema.RequiredMode.REQUIRED)
    private String brandName;

    public Brand toEntity() {
        return Brand.builder()
                .brandName(this.brandName)
                .uuid(UUIDGenerator.generateUUIDv7())
                .build();
    }

}
