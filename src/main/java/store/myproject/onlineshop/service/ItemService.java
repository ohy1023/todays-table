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
import store.myproject.onlineshop.domain.item.dto.*;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.repository.imagefile.ImageFileRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.repository.item.ItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.FileUtils;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;
import java.util.UUID;

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
    @Cacheable(value = "items", key = "#uuid")
    public ItemDto getItem(UUID uuid) {

        Item item = getItemByUuid(uuid);

        return item.toItemDto();
    }

    @Transactional(readOnly = true)
    public Page<SimpleItemDto> searchItem(ItemSearchCond itemSearchCond, Pageable pageable) {
        return itemRepository.search(itemSearchCond, pageable);
    }

    public MessageResponse createItem(ItemCreateRequest request, List<MultipartFile> multipartFileList) {

        itemRepository.findItemByItemName(request.getItemName())
                .ifPresent((item -> {
                    throw new AppException(DUPLICATE_ITEM);
                }));

        Brand findBrand = getBrand(request.getBrandName());

        Item savedItem = itemRepository.save(request.toEntity(findBrand));

        for (int i = 0; i < multipartFileList.size(); i++) {
            MultipartFile multipartFile = multipartFileList.get(i);

            String originImageUrl = awsS3Service.uploadItemOriginImage(multipartFile);

            ImageFile image = ImageFile.createImage(originImageUrl, savedItem);

            // 연관관계 정의
            image.addItem(savedItem);

            imageFileRepository.save(image);

            if (i == 0) savedItem.setThumbnail(originImageUrl);
        }

        return new MessageResponse(savedItem.getUuid(), messageUtil.get(MessageCode.ITEM_ADDED));
    }

    @CacheEvict(value = "items", allEntries = true)
    public MessageResponse updateItem(UUID uuid, ItemUpdateRequest request, List<MultipartFile> multipartFileList) {

        Item findItem = getItemByUuid(uuid);

        Brand findBrand = getBrand(request.getBrandName());

        findItem.updateItem(request, findBrand);

        if (multipartFileList != null && !multipartFileList.isEmpty()) {

            // 1. 기존 이미지 삭제
            for (ImageFile imageFile : findItem.getImageFileList()) {
                String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());

                imageFile.removeItem(findItem);  // 연관관계 제거
                awsS3Service.deleteBrandImage(extractFileName);
            }

            // 2. 새로운 이미지 업로드 및 저장
            for (int i = 0; i < multipartFileList.size(); i++) {
                MultipartFile multipartFile = multipartFileList.get(i);
                String newUrl = awsS3Service.uploadItemOriginImage(multipartFile);

                ImageFile image = ImageFile.createImage(newUrl, findItem);
                image.addItem(findItem);
                imageFileRepository.save(image); // 저장 누락되어 있던 부분

                // 3. 첫 번째 이미지면 썸네일로 지정
                if (i == 0) findItem.setThumbnail(newUrl);
            }
        }


        return new MessageResponse(findItem.getUuid(), messageUtil.get(MessageCode.ITEM_MODIFIED));

    }

    @CacheEvict(value = "items", allEntries = true)
    public MessageResponse deleteItem(UUID uuid) {

        Item findItem = getItemByUuid(uuid);

        for (ImageFile imageFile : findItem.getImageFileList()) {
            String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());

            awsS3Service.deleteItemImage(extractFileName);

            imageFileRepository.deleteById(imageFile.getId());
        }

        itemRepository.deleteById(findItem.getId());

        return new MessageResponse(findItem.getUuid(), messageUtil.get(MessageCode.ITEM_DELETED));
    }

    private Item getItemByUuid(UUID uuid) {
        return itemRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
    }

    private Brand getBrand(String brandName) {
        return brandRepository.findBrandByName(brandName)
                .orElseThrow(() -> new AppException(BRAND_NOT_FOUND));
    }
}
