package store.myproject.onlineshop.domain.dto.brand;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandCreateResponse {

    private String name;
    private String originImagePath;
}