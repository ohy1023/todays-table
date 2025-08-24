package store.myproject.onlineshop.dto.brand;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.global.utils.UUIDGenerator;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "브랜드 생성 요청 DTO")
public class BrandCreateRequest {

    @NotBlank(message = "브랜드명은 공백일수 없습니다.")
    @Schema(description = "브랜드 이름", example = "풀무원", requiredMode = Schema.RequiredMode.REQUIRED)
    private String brandName;

    public Brand toEntity() {
        return Brand.builder()
                .brandName(this.brandName)
                .uuid(UUIDGenerator.generateUUIDv7())
                .build();
    }
}