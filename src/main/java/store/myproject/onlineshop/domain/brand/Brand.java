package store.myproject.onlineshop.domain.brand;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;

import java.util.ArrayList;
import java.util.List;

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
                .id(this.id)
                .name(this.name)
                .build();
    }

    public void update(BrandUpdateRequest updatedBrand) {
        this.name = updatedBrand.getName();
    }

    public void removeImage() {
        this.imageFile = null;
    }
}