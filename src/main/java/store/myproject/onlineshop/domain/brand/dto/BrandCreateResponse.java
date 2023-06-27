package store.myproject.onlineshop.domain.brand.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandCreateResponse {

    private String name;
    private String originImagePath;
}