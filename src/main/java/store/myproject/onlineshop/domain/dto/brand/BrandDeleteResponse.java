package store.myproject.onlineshop.domain.dto.brand;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BrandDeleteResponse {

    private String name;
}
