package store.myproject.onlineshop.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.dto.brand.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    private String originImagePath;


    public BrandInfo toBrandInfo() {
        return BrandInfo.builder()
                .id(this.id)
                .name(this.name)
                .originImagePath(this.originImagePath)
                .build();
    }

    public BrandCreateResponse toBrandCreateResponse() {
        return BrandCreateResponse.builder()
                .name(this.name)
                .originImagePath(this.originImagePath)
                .build();
    }

    public BrandDeleteResponse toBrandDeleteResponse() {
        return BrandDeleteResponse.builder()
                .name(this.name)
                .build();
    }

    public BrandUpdateResponse toBrandUpdateResponse() {
        return BrandUpdateResponse.builder()
                .name(this.name)
                .originImagePath(this.originImagePath)
                .build();
    }

    public void update(BrandUpdateRequest updatedBrand) {
        this.name = updatedBrand.getName();
        this.originImagePath = updatedBrand.getOriginImagePath();
    }
}