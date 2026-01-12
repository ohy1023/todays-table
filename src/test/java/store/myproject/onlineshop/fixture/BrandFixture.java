package store.myproject.onlineshop.fixture;


import net.datafaker.Faker;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.dto.brand.BrandCreateRequest;
import store.myproject.onlineshop.dto.brand.BrandInfo;
import store.myproject.onlineshop.dto.brand.BrandUpdateRequest;

import java.util.Locale;
import java.util.UUID;


public class BrandFixture {

    private static final Faker faker = new Faker(Locale.KOREA);

    public static Brand createBrandEntity() {
        return Brand.builder()
                .id(1L)
                .uuid(UUID.randomUUID())
                .brandName(faker.company().name())
                .build();
    }

    public static Brand createBrand() {
        return Brand.builder()
                .uuid(UUID.randomUUID())
                .brandName(faker.company().name())
                .build();
    }

    public static BrandCreateRequest createRequest() {
        return BrandCreateRequest.builder()
                .brandName(faker.company().name())
                .build();
    }

    public static BrandUpdateRequest updateRequest() {
        return BrandUpdateRequest.builder()
                .brandName(faker.company().name())
                .build();
    }

    public static BrandInfo createBrandInfo(UUID uuid) {
        return BrandInfo.builder()
                .uuid(uuid)
                .brandName(faker.company().name())
                .brandImgUrl(faker.internet().image())
                .build();
    }

}
