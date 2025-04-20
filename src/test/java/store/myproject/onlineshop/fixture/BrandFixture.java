package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.dto.*;


public class BrandFixture {

    private static final Faker faker = new Faker();

    public static Brand createBrand() {

        return Brand.builder()
                .id(1L)
                .name(faker.company().name())
                .build();
    }

    public static BrandCreateRequest createRequest() {
        return BrandCreateRequest.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandCreateResponse createResponse() {
        return BrandCreateResponse.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandUpdateRequest updateRequest() {
        return BrandUpdateRequest.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandUpdateResponse updateResponse() {
        return BrandUpdateResponse.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandDeleteResponse deleteResponse() {
        return BrandDeleteResponse.builder()
                .name(faker.company().name())
                .build();
    }

    public static BrandInfo brandInfo(Long id) {
        return BrandInfo.builder()
                .id(id)
                .name(faker.company().name())
                .build();
    }


}
