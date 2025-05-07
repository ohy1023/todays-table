package store.myproject.onlineshop.domain.brand;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.global.utils.UUIDBinaryConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        indexes = {
                @Index(name = "idx_brand_uuid", columnList = "brand_uuid"), // uuid에 인덱스!
                @Index(name = "idx_brand_name", columnList = "name") // name에 인덱스!
        }
)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    @Column(name = "brand_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;

    @Column(unique = true)
    private String name;

    @Setter
    @OneToOne(mappedBy = "brand")
    private ImageFile imageFile;

    @Builder.Default
    @OneToMany(mappedBy = "brand")
    private List<Item> itemList = new ArrayList<>();


    public BrandInfo toBrandInfo() {
        return BrandInfo.builder()
                .uuid(this.uuid)
                .name(this.name)
                .brandImgUrl(this.imageFile.getImageUrl())
                .build();
    }

    public void update(BrandUpdateRequest updatedBrand) {
        this.name = updatedBrand.getName();
    }

    public void removeImage() {
        this.imageFile = null;
    }
}