package store.myproject.onlineshop.domain.dto.brand;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BrandUpdateResponse {

    private String name;

    private String originImagePath;
}
