package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.dto.*;

import java.util.Locale;
import java.util.UUID;


public class BrandFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static Brand createBrandEntity() {
        return Brand.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .name(faker.company().name())
                .build();
    }

    public static Brand createBrand() {
        return Brand.builder()
                .uuid(UUID.randomUUID())
                .name(faker.company().name())
                .build();
    }

    public static BrandCreateRequest createRequest() {
        return BrandCreateRequest.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandUpdateRequest updateRequest() {
        return BrandUpdateRequest.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandInfo createBrandInfo(UUID uuid) {
        return BrandInfo.builder()
                .uuid(uuid)
                .name(faker.company().name())
                .brandImgUrl(faker.internet().image())
                .build();
    }

}
