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
    private String brandImgUrl;

    @QueryProjection
    public BrandInfo(Long id, String name, String brandImgUrl) {
        this.id = id;
        this.name = name;
        this.brandImgUrl = brandImgUrl;
    }

}