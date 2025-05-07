package store.myproject.onlineshop.domain.brand.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandInfo {

    private UUID uuid;
    private String name;
    private String brandImgUrl;

    @QueryProjection
    public BrandInfo(UUID uuid, String name, String brandImgUrl) {
        this.uuid = uuid;
        this.name = name;
        this.brandImgUrl = brandImgUrl;
    }

}