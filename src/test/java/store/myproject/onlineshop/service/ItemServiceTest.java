package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.repository.BrandRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.imagefile.repository.ImageFileRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.fixture.ImageFileFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.global.s3.service.AwsS3Service;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;
import static store.myproject.onlineshop.exception.ErrorCode.DUPLICATE_ITEM;

@ExtendWith(MockitoExtension.class)
class ItemServiceTest {

    @InjectMocks
    private ItemService itemService;

    @Mock private ItemRepository itemRepository;
    @Mock private BrandRepository brandRepository;
    @Mock private ImageFileRepository imageFileRepository;
    @Mock private AwsS3Service awsS3Service;
    @Mock private MultipartFile multipartFile;

    Item item = ItemFixture.createItem();
    Brand brand = BrandFixture.createBrand();
    ImageFile imageFile = ImageFileFixture.withItem(item);

    @Test
    @DisplayName("품목 등록 성공")
    void save_item_success() {
        // given
        ItemCreateRequest request = ItemFixture.createRequest();

        given(itemRepository.findItemByItemName(request.getItemName())).willReturn(Optional.empty());
        given(brandRepository.findBrandByName(request.getBrandName())).willReturn(Optional.of(brand));
        given(itemRepository.save(any(Item.class))).willReturn(item);
        given(awsS3Service.uploadItemOriginImage(any(MultipartFile.class))).willReturn(imageFile.getImageUrl());
        given(imageFileRepository.save(any(ImageFile.class))).willReturn(imageFile);

        // when
        ItemDto result = itemService.saveItem(request, List.of(multipartFile));

        // then
        assertThat(result).isNotNull();
        assertThat(result.getItemName()).isEqualTo(item.getItemName());
        assertThat(result.getPrice()).isEqualTo(item.getPrice());
        assertThat(result.getStock()).isEqualTo(item.getStock());

        then(itemRepository).should().save(any(Item.class));
        then(imageFileRepository).should().save(any(ImageFile.class));
    }

    @Test
    @DisplayName("품목 등록 실패 - 중복 이름")
    void save_item_fail_duplicate_item() {
        // given
        ItemCreateRequest request = ItemFixture.createRequest();

        given(itemRepository.findItemByItemName(anyString()))
                .willReturn(Optional.of(item));

        // when & then
        assertThatThrownBy(() -> itemService.saveItem(request,List.of(multipartFile)))
                .isInstanceOf(AppException.class)
                .hasMessage(DUPLICATE_ITEM.getMessage());
    }

    @Test
    @DisplayName("품목 수정 성공")
    void update_item_success() {
        // given
        ItemUpdateRequest request = ItemFixture.updateRequest();

        given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
        given(brandRepository.findBrandByName(any(String.class))).willReturn(Optional.of(brand));
        given(awsS3Service.uploadItemOriginImage(any(MultipartFile.class))).willReturn(imageFile.getImageUrl());

        // when
        MessageResponse result = itemService.updateItem(item.getId(), request, List.of(multipartFile));

        // then
        assertThat(result.getMsg()).isEqualTo("해당 품목이 수정되었습니다.");
    }

    @Test
    @DisplayName("품목 삭제 성공")
    void deleteItem_success() {
        // given
        Long itemId = 1L;
        Item item = mock(Item.class);
        Long imageFileId = 1L;
        ImageFile imageFile = mock(ImageFile.class);

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(item.getId()).willReturn(itemId);
        given(item.getImageFileList()).willReturn(List.of(imageFile));
        given(imageFile.getImageUrl()).willReturn("https://s3.url/delete.jpg");
        given(imageFile.getId()).willReturn(imageFileId);

        // when
        MessageResponse result = itemService.deleteItem(itemId);

        // then
        assertThat(result.getMsg()).isEqualTo("해당 품목이 삭제되었습니다.");
        then(imageFileRepository).should().deleteById(imageFileId);
        then(itemRepository).should().deleteById(itemId);
    }
}
