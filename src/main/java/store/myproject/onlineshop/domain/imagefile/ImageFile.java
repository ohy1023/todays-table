package store.myproject.onlineshop.domain.imagefile;

import jakarta.persistence.*;
import lombok.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.recipe.Recipe;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ImageFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "image_file_id")
    private Long id;

    private String imageUrl;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    // 빌더 메서드에 추가된 생성자를 위한 빌더 메서드
    @Builder(builderMethodName = "createImageBuilder")
    private ImageFile(String imageUrl, Brand brand, Recipe recipe, Item item) {
        this.imageUrl = imageUrl;
        this.brand = brand;
        this.recipe = recipe;
        this.item = item;
    }

    // 오브젝트가 브랜드 인지 레시피인지 아이템인지에 따른 객체 생성 메서드
    public static ImageFile createImage(String imageUrl, Object o) {
        // 브랜드, 레시피, 아이템에 맞게 객체 생성 로직 작성
        if (o instanceof Brand) {
            return createImageBuilder().brand((Brand) o).imageUrl(imageUrl).build();
        } else if (o instanceof Recipe) {
            return createImageBuilder().recipe((Recipe) o).imageUrl(imageUrl).build();
        } else if (o instanceof Item) {
            return createImageBuilder().item((Item) o).imageUrl(imageUrl).build();
        } else {
            throw new IllegalArgumentException("Unsupported object type");
        }
    }


    public void addBrand(Brand brand) {
        this.brand = brand;
        brand.setImageFile(this);
    }

    public void removeBrand() {
        if (this.brand != null) {
            this.brand.removeImage();
            this.brand = null;
        }
    }

//    public void addRecipe(Recipe recipe) {
//        this.recipe = recipe;
//        recipe.getImageFileList().add(this); // 레시피에 ImageFile 추가
//    }
//
//    public void removeRecipe(Recipe recipe) {
//        if (this.recipe != null && this.recipe.equals(recipe)) {
//            this.recipe = null;
//            recipe.getImageFileList().remove(this); // 레시피에 연결된 ImageFile 제거
//        }
//    }

    public void addItem(Item item) {
        this.item = item;
        item.getImageFileList().add(this); // 아이템에 ImageFile 추가
    }

    public void removeItem(Item item) {
        if (this.item != null && this.item.equals(item)) {
            this.item = null;
            item.getImageFileList().remove(this); // 아이템에 연결된 ImageFile 제거
        }
    }
}
