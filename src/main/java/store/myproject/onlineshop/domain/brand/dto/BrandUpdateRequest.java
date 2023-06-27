package store.myproject.onlineshop.domain.brand.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandUpdateRequest {

    @NotEmpty(message = "브랜드 명을 입력하세요.")
    private String name;

    private String originImagePath;

    public Brand toEntity() {
        return Brand.builder()
                .name(this.name)
                .originImagePath(this.originImagePath)
                .build();
    }

    public void setOriginImagePath(String newImageUrl) {
        this.originImagePath = newImageUrl;
    }
}
