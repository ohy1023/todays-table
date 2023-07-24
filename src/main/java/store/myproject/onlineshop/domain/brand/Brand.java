package store.myproject.onlineshop.domain.brand;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import store.myproject.onlineshop.domain.BaseEntity;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.orderitem.OrderItem;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Where(clause = "deleted_date IS NULL")
@SQLDelete(sql = "UPDATE brand SET deleted_date = CURRENT_TIMESTAMP WHERE brand_id = ?")
public class Brand extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String name;

    @Column(name = "origin_image_path")
    private String originImagePath;

    @Builder.Default
    @OneToMany(mappedBy = "brand")
    private List<Item> itemList = new ArrayList<>();

    public void addItem(Item item) {
        itemList.add(item);
        item.addBrand(this);
    }


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