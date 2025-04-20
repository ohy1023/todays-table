package store.myproject.onlineshop.fixture;

import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.recipe.Recipe;

public class ImageFileFixture {

    private static final String DEFAULT_IMAGE_URL = "https://example.com/image.jpg";

    public static ImageFile withBrand(Brand brand) {
        return ImageFile.createImage(DEFAULT_IMAGE_URL, brand);
    }

    public static ImageFile withRecipe(Recipe recipe) {
        return ImageFile.createImage(DEFAULT_IMAGE_URL, recipe);
    }

    public static ImageFile withItem(Item item) {
        return ImageFile.createImage(DEFAULT_IMAGE_URL, item);
    }

    public static ImageFile dummy() {
        return ImageFile.createImage(DEFAULT_IMAGE_URL, (Object) null); // 필요 시 예외 테스트용
    }
}
