package store.myproject.onlineshop.fixture;

import net.datafaker.Faker;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.item.Item;

import java.util.Locale;

public class ImageFileFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static ImageFile withBrand(Brand brand) {
        return ImageFile.createImage(faker.internet().image());
    }

    public static ImageFile withItem(Item item) {
        return ImageFile.createImage(faker.internet().image());
    }

}
