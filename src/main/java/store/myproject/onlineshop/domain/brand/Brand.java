package store.myproject.onlineshop.domain.brand;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import store.myproject.onlineshop.domain.common.BaseEntity; // 1. BaseEntity 임포트
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.dto.brand.BrandInfo;
import store.myproject.onlineshop.dto.brand.BrandUpdateRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE brand SET deleted_date = CURRENT_TIMESTAMP WHERE brand_id = ?")
@SQLRestriction("deleted_date IS NULL")
@Table(name = "brand")
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "brand_id")
    private Long id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(name = "brand_uuid", nullable = false, unique = true, updatable = false)
    private UUID uuid;

    @Column(name = "brand_name", nullable = false)
    private String brandName;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "image_file_id")
    private ImageFile imageFile;

    @Builder.Default
    @OneToMany(mappedBy = "brand")
    private List<Item> itemList = new ArrayList<>();

    public BrandInfo toBrandInfo() {
        return BrandInfo.builder()
                .uuid(this.uuid)
                .brandName(this.brandName)
                .brandImgUrl(this.imageFile != null ? this.imageFile.getImageUrl() : null)
                .build();
    }

    public void update(BrandUpdateRequest updatedBrand) {
        if (updatedBrand.getBrandName() != null) {
            this.brandName = updatedBrand.getBrandName();
        }
    }

    public void addImage(ImageFile imageFile) {
        this.imageFile = imageFile;
    }

    public void removeImage() {
        this.imageFile = null;
    }
}