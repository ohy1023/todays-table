package store.myproject.onlineshop.domain.brand.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandDeleteResponse {

    private String name;
}
