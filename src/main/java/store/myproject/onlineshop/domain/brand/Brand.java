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
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    @Column(name = "brand_uuid", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    @Convert(converter = UUIDBinaryConverter.class)
    private UUID uuid;

    @Column(name = "brand_name")
    private String brandName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "image_file_id")
    private ImageFile imageFile;

    @Builder.Default
    @OneToMany(mappedBy = "brand")
    private List<Item> itemList = new ArrayList<>();


    public BrandInfo toBrandInfo() {
        return BrandInfo.builder()
                .uuid(this.uuid)
                .brandName(this.brandName)
                .brandImgUrl(this.imageFile.getImageUrl())
                .build();
    }

    public void update(BrandUpdateRequest updatedBrand) {
        this.brandName = updatedBrand.getBrandName();
    }

    public void addImage(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public void removeImage() {
        this.imageFile = null;
    }
}