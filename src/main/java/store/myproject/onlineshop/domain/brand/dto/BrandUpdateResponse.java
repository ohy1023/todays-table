package store.myproject.onlineshop.domain.brand.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class BrandUpdateResponse {

    private String name;

}
