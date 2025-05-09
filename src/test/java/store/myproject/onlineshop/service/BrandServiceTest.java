package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.dto.BrandCreateRequest;
import store.myproject.onlineshop.domain.brand.dto.BrandInfo;
import store.myproject.onlineshop.domain.brand.dto.BrandUpdateRequest;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.repository.imagefile.ImageFileRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.CommonFixture;
import store.myproject.onlineshop.fixture.ImageFileFixture;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceTest {

    @InjectMocks
    private BrandService brandService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private ImageFileRepository imageFileRepository;

    @Mock
    private AwsS3Service awsS3Service;

    @Mock
    private MessageUtil messageUtil;

    Brand brand = BrandFixture.createBrand();
    ImageFile imageFile = ImageFileFixture.withBrand(brand);
    MockMultipartFile mockFile = CommonFixture.mockMultipartFile();

    @Test
    @DisplayName("브랜드 단건 조회 성공")
    void find_brand_info_success() {
        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.of(brand));

        // when
        BrandInfo response = brandService.findBrandInfoById(brandUuid);

        then(brandRepository).should(times(1)).findByUuid(brandUuid);
        assertThat(response.getName()).isEqualTo(brand.getName());
    }

    @Test
    @DisplayName("브랜드 검색 성공")
    void search_brand_success() {
        // given
        String brandName = brand.getName();
        Pageable pageable = PageRequest.of(0, 10);

        UUID brand1Uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID brand2Uuid = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");

        List<BrandInfo> content = List.of(
                BrandInfo.builder().uuid(brand1Uuid).name(brandName).build(),
                BrandFixture.createBrandInfo(brand2Uuid)
        );

        Page<BrandInfo> page = new PageImpl<>(content, pageable, content.size());
        given(brandRepository.search(brandName, pageable)).willReturn(page);

        // when & then
        Page<BrandInfo> response = brandService.searchBrands(brandName, pageable);

        assertThat(response.getContent()).hasSize(2);
        assertThat(response.getContent().get(0).getName()).isEqualTo(brandName);
        then(brandRepository).should(times(1)).search(brandName, pageable);
    }


    @Test
    @DisplayName("브랜드 등록 성공")
    void create_brand_success() {
        BrandCreateRequest request = BrandFixture.createRequest();

        given(brandRepository.existsByName(request.getName())).willReturn(false);
        given(awsS3Service.uploadBrandOriginImage(mockFile)).willReturn(imageFile.getImageUrl());
        given(brandRepository.save(any(Brand.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(imageFileRepository.save(any(ImageFile.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(messageUtil.get(MessageCode.BRAND_ADDED)).willReturn("브랜드 등록 완료");

        MessageResponse response = brandService.createBrand(request, mockFile);

        assertThat(response.getMessage()).isEqualTo("브랜드 등록 완료");
        then(brandRepository).should().save(any());
        then(imageFileRepository).should().save(any());
    }

    @Test
    @DisplayName("브랜드 등록 실패 - 이름 중복")
    void create_brand_fail_duplicate_name() {
        BrandCreateRequest request = new BrandCreateRequest("중복브랜드");

        given(brandRepository.existsByName("중복브랜드")).willReturn(true);

        assertThatThrownBy(() -> brandService.createBrand(request, mockFile))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("브랜드 수정 성공 - 이미지, 이름 포함")
    void update_brand_success_with_image_and_name() {
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        BrandUpdateRequest request = BrandFixture.updateRequest();
        String newImageUrl = "s3://new-image";

        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.of(brand));
        given(awsS3Service.uploadBrandOriginImage(mockFile)).willReturn(newImageUrl);
        given(messageUtil.get(MessageCode.BRAND_MODIFIED)).willReturn("브랜드 수정 완료");

        MessageResponse response = brandService.updateBrand(brandUuid, request, mockFile);

        assertThat(response.getMessage()).isEqualTo("브랜드 수정 완료");
        then(imageFileRepository).should().deleteById(imageFile.getId());
    }

    @Test
    @DisplayName("브랜드 수정 성공 - 이미지 없이 이름만")
    void update_brand_success_name_only() {
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        Brand brand = Brand.builder().name("예전이름").build();

        BrandUpdateRequest request = new BrandUpdateRequest("새이름");

        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.of(brand));
        given(messageUtil.get(MessageCode.BRAND_MODIFIED)).willReturn("브랜드 수정 완료");

        MessageResponse response = brandService.updateBrand(brandUuid, request, null);

        assertThat(response.getMessage()).isEqualTo("브랜드 수정 완료");
        assertThat(brand.getName()).isEqualTo("새이름");
        then(awsS3Service).should(never()).deleteBrandImage(anyString());
    }

    @Test
    @DisplayName("브랜드 수정 실패 - 존재하지 않음")
    void update_brand_fail_not_found() {
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        BrandUpdateRequest request = BrandFixture.updateRequest();

        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.updateBrand(brandUuid, request, null))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("브랜드 삭제 실패 - 존재하지 않음")
    void delete_brand_fail_not_found() {
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.empty());

        assertThatThrownBy(() -> brandService.deleteBrand(brandUuid))
                .isInstanceOf(AppException.class);
    }

    @Test
    @DisplayName("브랜드 삭제 성공")
    void delete_brand_success() {
        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");


        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.of(brand));
        given(messageUtil.get(MessageCode.BRAND_DELETED)).willReturn("브랜드 삭제 성공");

        // when
        MessageResponse response = brandService.deleteBrand(brandUuid);

        // then
        then(brandRepository).should(times(1)).deleteById(brand.getId());
        then(awsS3Service).should(times(1)).deleteBrandImage(anyString());
        assertThat(response.getMessage()).isEqualTo("브랜드 삭제 성공");
    }

    @Test
    @DisplayName("브랜드 삭제 실패 - 브랜드 없음")
    void delete_brand_not_found_brand() {
        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");


        given(brandRepository.findByUuid(brandUuid)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> brandService.deleteBrand(brandUuid))
                .isInstanceOf(AppException.class)
                .hasMessage(ErrorCode.BRAND_NOT_FOUND.getMessage());
    }
}
