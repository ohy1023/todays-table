package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.repository.BrandRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.item.repository.ItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.s3.service.AwsS3Service;
import store.myproject.onlineshop.global.utils.FileUtils;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;

    private final BrandRepository brandRepository;

    private final AwsS3Service awsS3Service;

    @Transactional(readOnly = true)
    @Cacheable(value = "items", key = "#id")
    public ItemDto selectOne(Long id) {
        return getItem(id).toItemDto();
    }

    @Transactional(readOnly = true)
    public Page<ItemDto> searchItem(ItemSearchCond itemSearchCond, Pageable pageable) {
        return itemRepository.search(itemSearchCond, pageable);
    }

    public ItemDto saveItem(ItemCreateRequest request, MultipartFile multipartFile) {

        itemRepository.findItemByItemName(request.getItemName())
                .ifPresent((item -> {
                    throw new AppException(DUPLICATE_ITEM, DUPLICATE_ITEM.getMessage());
                }));

        Brand findBrand = getBrand(request.getBrandName());

        String originImageUrl = awsS3Service.uploadBrandOriginImage(multipartFile);

        request.setItemPhotoUrl(originImageUrl);

        Item savedItem = itemRepository.save(request.toEntity(findBrand));

        return savedItem.toItemDto();

    }

    @CacheEvict(value = "items", allEntries = true)
    public ItemDto updateItem(Long id, ItemUpdateRequest request, MultipartFile multipartFile) {

        Item findItem = getItem(id);

        Brand findBrand = getBrand(request.getBrandName());

        if (!multipartFile.isEmpty()) {
            String extractFileName = FileUtils.extractFileName(findItem.getItemPhotoUrl());

            awsS3Service.deleteBrandImage(extractFileName);

            String newUrl = awsS3Service.uploadBrandOriginImage(multipartFile);

            request.setItemPhotoUrl(newUrl);
        } else {
            request.setItemPhotoUrl(findItem.getItemPhotoUrl());
        }

        findItem.updateItem(request, findBrand);

        return findItem.toItemDto();

    }

    @CacheEvict(value = "items", allEntries = true)
    public MessageResponse deleteItem(Long id) {

        Item findItem = getItem(id);

        String extractFileName = FileUtils.extractFileName(findItem.getItemPhotoUrl());

        awsS3Service.deleteBrandImage(extractFileName);

        itemRepository.deleteById(findItem.getId());

        return new MessageResponse("해당 품목이 삭제되었습니다.");
    }

    private Item getItem(Long id) {
        Item findItem = itemRepository.findById(id)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));
        return findItem;
    }

    private Brand getBrand(String brandName) {
        Brand findBrand = brandRepository.findBrandByName(brandName)
                .orElseThrow(() -> new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));
        return findBrand;
    }
}
