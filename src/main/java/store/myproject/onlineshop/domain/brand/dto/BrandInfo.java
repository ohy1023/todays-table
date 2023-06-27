package store.myproject.onlineshop.domain.brand.dto;

import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BrandInfo {

    private Long id;
    private String name;
    private String originImagePath;

    public Brand toEntity() {
        return Brand.builder()
                .id(this.id)
                .name(this.name)
                .originImagePath(this.originImagePath)
                .build();
    }
}