package store.myproject.onlineshop.fixture;

import com.github.javafaker.Faker;
import org.springframework.mock.web.MockMultipartFile;
import store.myproject.onlineshop.domain.brand.dto.*;

import java.nio.charset.StandardCharsets;

public class BrandFixture {

    private static final Faker faker = new Faker();

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

    public static MockMultipartFile mockMultipartFile() {
        return new MockMultipartFile(
                "multipartFile",
                "image.png",
                "image/png",
                "<<image data>>".getBytes(StandardCharsets.UTF_8)
        );
    }
}
