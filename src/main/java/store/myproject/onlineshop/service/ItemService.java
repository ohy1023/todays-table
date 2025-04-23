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
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.repository.imagefile.ImageFileRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.s3.service.AwsS3Service;
import store.myproject.onlineshop.global.utils.FileUtils;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {
    private final ImageFileRepository imageFileRepository;

    private final ItemRepository itemRepository;

    private final BrandRepository brandRepository;

    private final AwsS3Service awsS3Service;

    private final MessageUtil messageUtil;

    @Transactional(readOnly = true)
    @Cacheable(value = "items", key = "#id")
    public ItemDto getItemById(Long id) {

        Item item = getItem(id);

        return item.toItemDto(getItem(id));
    }

    @Transactional(readOnly = true)
    public Page<ItemDto> searchItem(ItemSearchCond itemSearchCond, Pageable pageable) {
        return itemRepository.search(itemSearchCond, pageable);
    }

    public ItemDto createItem(ItemCreateRequest request, List<MultipartFile> multipartFileList) {

        itemRepository.findItemByItemName(request.getItemName())
                .ifPresent((item -> {
                    throw new AppException(DUPLICATE_ITEM, DUPLICATE_ITEM.getMessage());
                }));

        Brand findBrand = getBrand(request.getBrandName());

        Item savedItem = itemRepository.save(request.toEntity(findBrand));

        for (MultipartFile multipartFile : multipartFileList) {
            String originImageUrl = awsS3Service.uploadItemOriginImage(multipartFile);

            ImageFile image = ImageFile.createImage(originImageUrl, savedItem);

            // 연관관계 정의
            image.addItem(savedItem);

            imageFileRepository.save(image);
        }

        return savedItem.toItemDto(savedItem);
    }

    @CacheEvict(value = "items", allEntries = true)
    public MessageResponse updateItem(Long id, ItemUpdateRequest request, List<MultipartFile> multipartFileList) {

        Item findItem = getItem(id);

        Brand findBrand = getBrand(request.getBrandName());

        findItem.updateItem(request, findBrand);

        if (multipartFileList != null) {
            for (MultipartFile multipartFile : multipartFileList) {
                for (ImageFile imageFile : findItem.getImageFileList()) {

                    String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());
                    // 연관관계 제거
                    imageFile.removeItem(findItem);

                    awsS3Service.deleteBrandImage(extractFileName);
                }

                String newUrl = awsS3Service.uploadItemOriginImage(multipartFile);

                ImageFile image = ImageFile.createImage(newUrl, findItem);

                image.addItem(findItem);
            }

        }

        return new MessageResponse(messageUtil.get(MessageCode.ITEM_MODIFIED));

    }

    @CacheEvict(value = "items", allEntries = true)
    public MessageResponse deleteItem(Long id) {

        Item findItem = getItem(id);

        for (ImageFile imageFile : findItem.getImageFileList()) {
            String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());

            awsS3Service.deleteBrandImage(extractFileName);

            imageFileRepository.deleteById(imageFile.getId());
        }

        itemRepository.deleteById(findItem.getId());

        return new MessageResponse(messageUtil.get(MessageCode.ITEM_DELETED));
    }

    private Item getItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND, ITEM_NOT_FOUND.getMessage()));
    }

    private Brand getBrand(String brandName) {
        return brandRepository.findBrandByName(brandName)
                .orElseThrow(() -> new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));
    }
}
