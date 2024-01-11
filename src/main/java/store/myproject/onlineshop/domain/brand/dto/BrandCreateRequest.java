package store.myproject.onlineshop.domain.brand.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BrandCreateRequest {

    @NotBlank(message = "브랜드명은 공백일수 없습니다.")
    private String name;

    public Brand toEntity() {
        return Brand.builder()
                .name(this.name)
                .build();
    }
}