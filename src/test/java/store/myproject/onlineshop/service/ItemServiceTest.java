//package store.myproject.onlineshop.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.*;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.multipart.MultipartFile;
//import store.myproject.onlineshop.domain.MessageCode;
//import store.myproject.onlineshop.domain.MessageResponse;
//import store.myproject.onlineshop.domain.brand.Brand;
//import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
//import store.myproject.onlineshop.repository.brand.BrandRepository;
//import store.myproject.onlineshop.domain.imagefile.ImageFile;
//import store.myproject.onlineshop.repository.imagefile.ImageFileRepository;
//import store.myproject.onlineshop.domain.item.Item;
//import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
//import store.myproject.onlineshop.domain.item.dto.ItemDto;
//import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
//import store.myproject.onlineshop.repository.item.ItemRepository;
//import store.myproject.onlineshop.exception.AppException;
//import store.myproject.onlineshop.exception.ErrorCode;
//import store.myproject.onlineshop.fixture.BrandFixture;
//import store.myproject.onlineshop.fixture.ImageFileFixture;
//import store.myproject.onlineshop.fixture.ItemFixture;
//import store.myproject.onlineshop.global.utils.MessageUtil;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.BDDMockito.*;
//import static store.myproject.onlineshop.exception.ErrorCode.DUPLICATE_ITEM;
//import static store.myproject.onlineshop.exception.ErrorCode.ITEM_NOT_FOUND;
//
//@ExtendWith(MockitoExtension.class)
//class ItemServiceTest {
//
//    @InjectMocks
//    private ItemService itemService;
//
//    @Mock
//    private ItemRepository itemRepository;
//    @Mock
//    private BrandRepository brandRepository;
//    @Mock
//    private ImageFileRepository imageFileRepository;
//    @Mock
//    private AwsS3Service awsS3Service;
//    @Mock
//    private MultipartFile multipartFile;
//    @Mock
//    private MessageUtil messageUtil;
//
//    Brand brand = BrandFixture.createBrand();
//    Item item = ItemFixture.createItem(brand);
//    ImageFile imageFile = ImageFileFixture.withItem(item);
//
//    @Test
//    @DisplayName("아이템 ID로 조회 성공")
//    void getItemById_success() {
//        // given
//        Long itemId = 1L;
//
//        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//
//        // when
//        ItemDto result = itemService.getItemById(itemId);
//
//        // then
//        assertThat(result.getItemName()).isEqualTo(item.getItemName());
//        assertThat(result.getPrice()).isEqualTo(item.getPrice());
//    }
//
//    @Test
//    @DisplayName("아이템 ID로 조회 실패 - 존재하지 않음")
//    void getItemById_notFound() {
//        // given
//        Long invalidId = 99L;
//        given(itemRepository.findById(invalidId)).willReturn(Optional.empty());
//
//        // expect
//        assertThatThrownBy(() -> itemService.getItemById(invalidId))
//                .isInstanceOf(AppException.class)
//                .hasMessage(ITEM_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("조건 검색으로 아이템 리스트 조회 성공")
//    void searchItem_success() {
//        // given
//        ItemSearchCond cond = ItemSearchCond
//                .builder()
//                .itemName(item.getItemName())
//                .brandName(brand.getName())
//                .build();
//        Pageable pageable = PageRequest.of(0, 10);
//
//        Page<ItemDto> page = new PageImpl<>(List.of(item.toItemDto(item)), pageable, 1);
//        given(itemRepository.search(cond, pageable)).willReturn(page);
//
//        // when
//        Page<ItemDto> result = itemService.searchItem(cond, pageable);
//
//        // then
//        assertThat(result.getContent()).hasSize(1);
//        assertThat(result.getContent().get(0).getItemName()).isEqualTo(item.getItemName());
//    }
//
//    @Test
//    @DisplayName("품목 등록 성공")
//    void save_item_success() {
//        // given
//        ItemCreateRequest request = ItemFixture.createRequest();
//
//        given(itemRepository.findItemByItemName(request.getItemName())).willReturn(Optional.empty());
//        given(brandRepository.findBrandByName(request.getBrandName())).willReturn(Optional.of(brand));
//        given(itemRepository.save(any(Item.class))).willReturn(item);
//        given(awsS3Service.uploadItemOriginImage(any(MultipartFile.class))).willReturn(imageFile.getImageUrl());
//        given(imageFileRepository.save(any(ImageFile.class))).willReturn(imageFile);
//
//        // when
//        ItemDto result = itemService.createItem(request, List.of(multipartFile));
//
//        // then
//        assertThat(result).isNotNull();
//        assertThat(result.getItemName()).isEqualTo(item.getItemName());
//        assertThat(result.getPrice()).isEqualTo(item.getPrice());
//        assertThat(result.getStock()).isEqualTo(item.getStock());
//
//        then(itemRepository).should().save(any(Item.class));
//        then(imageFileRepository).should().save(any(ImageFile.class));
//    }
//
//    @Test
//    @DisplayName("품목 등록 실패 - 중복 이름")
//    void save_item_fail_duplicate_item() {
//        // given
//        ItemCreateRequest request = ItemFixture.createRequest();
//
//        given(itemRepository.findItemByItemName(anyString()))
//                .willReturn(Optional.of(item));
//
//        // when & then
//        assertThatThrownBy(() -> itemService.createItem(request, List.of(multipartFile)))
//                .isInstanceOf(AppException.class)
//                .hasMessage(DUPLICATE_ITEM.getMessage());
//    }
//
//    @Test
//    @DisplayName("품목 수정 성공")
//    void update_item_success() {
//        // given
//        ItemUpdateRequest request = ItemFixture.updateRequest();
//
//        given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
//        given(brandRepository.findBrandByName(any(String.class))).willReturn(Optional.of(brand));
//        given(awsS3Service.uploadItemOriginImage(any(MultipartFile.class))).willReturn(imageFile.getImageUrl());
//        given(messageUtil.get(MessageCode.ITEM_MODIFIED)).willReturn("해당 품목이 수정되었습니다.");
//
//        // when
//        MessageResponse response = itemService.updateItem(1L, request, List.of(multipartFile));
//
//        // then
//        assertThat(response.getMessage()).isEqualTo("해당 품목이 수정되었습니다.");
//    }
//
//    @Test
//    @DisplayName("품목 수정 실패 - 존재하지 않는 품목")
//    void update_item_fail_not_found() {
//        // given
//        ItemUpdateRequest request = ItemFixture.updateRequest();
//        given(itemRepository.findById(any(Long.class))).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> itemService.updateItem(1L, request, List.of(multipartFile)))
//                .isInstanceOf(AppException.class)
//                .hasMessageContaining(ErrorCode.ITEM_NOT_FOUND.getMessage());  // 또는 ErrorCode.ITEM_NOT_FOUND.getMessage()
//    }
//
//    @Test
//    @DisplayName("품목 수정 실패 - 존재하지 않는 브랜드")
//    void update_item_fail_brand_not_found() {
//        // given
//        ItemUpdateRequest request = ItemFixture.updateRequest();
//        given(itemRepository.findById(any(Long.class))).willReturn(Optional.of(item));
//        given(brandRepository.findBrandByName(any(String.class))).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> itemService.updateItem(1L, request, List.of(multipartFile)))
//                .isInstanceOf(AppException.class)
//                .hasMessageContaining(ErrorCode.BRAND_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("품목 삭제 성공")
//    void deleteItem_success() {
//        // given
//        Long itemId = 1L;
//        Item item = mock(Item.class);
//        Long imageFileId = 1L;
//        ImageFile imageFile = mock(ImageFile.class);
//
//        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
//        given(item.getId()).willReturn(itemId);
//        given(item.getImageFileList()).willReturn(List.of(imageFile));
//        given(imageFile.getImageUrl()).willReturn("https://s3.url/delete.jpg");
//        given(imageFile.getId()).willReturn(imageFileId);
//        given(messageUtil.get(MessageCode.ITEM_DELETED)).willReturn("해당 품목이 삭제되었습니다.");
//
//        // when
//        MessageResponse response = itemService.deleteItem(itemId);
//
//        // then
//        assertThat(response.getMessage()).isEqualTo("해당 품목이 삭제되었습니다.");
//        then(imageFileRepository).should().deleteById(imageFileId);
//        then(itemRepository).should().deleteById(itemId);
//    }
//
//    @Test
//    @DisplayName("품목 삭제 실패 - 존재하지 않는 품목")
//    void delete_item_fail_not_found() {
//        // given
//        given(itemRepository.findById(any(Long.class))).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> itemService.deleteItem(1L))
//                .isInstanceOf(AppException.class)
//                .hasMessageContaining(ErrorCode.ITEM_NOT_FOUND.getMessage());
//    }
//}
