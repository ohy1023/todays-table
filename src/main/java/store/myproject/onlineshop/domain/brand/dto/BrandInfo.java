package store.myproject.onlineshop.domain.brand.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandInfo {

    private Long id;
    private String name;

    @QueryProjection
    public BrandInfo(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Brand toEntity() {
        return Brand.builder()
                .id(this.id)
                .name(this.name)
                .build();
    }

}