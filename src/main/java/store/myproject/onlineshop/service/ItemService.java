package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.dto.common.MessageCode;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.dto.item.*;
import store.myproject.onlineshop.global.utils.RedisKeyHelper;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.brand.BrandRepository;
import store.myproject.onlineshop.domain.imagefile.ImageFileRepository;
import store.myproject.onlineshop.domain.item.Item;
import store.myproject.onlineshop.domain.item.ItemRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.FileUtils;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
    private final RedisTemplate<String, Object> cacheRedisTemplate;
    private final RedissonClient redisson;

    @Transactional(readOnly = true)
    public ItemDto getItem(UUID uuid) {
        String itemCacheKey = RedisKeyHelper.getItemCacheKey(uuid);
        ItemDto cachedItemDto = (ItemDto) cacheRedisTemplate.opsForValue().get(itemCacheKey);

        if (cachedItemDto != null) {
            return cachedItemDto;
        }

        String itemLockKey = RedisKeyHelper.getItemLockKey(uuid);
        RLock lock = redisson.getLock(itemLockKey);

        try {
            boolean isLocked = lock.tryLock(300, 2000, TimeUnit.MILLISECONDS);

            if (isLocked) {
                try {
                    ItemDto doubleCheckCache = (ItemDto) cacheRedisTemplate.opsForValue().get(itemCacheKey);
                    if (doubleCheckCache != null) {
                        return doubleCheckCache;
                    }

                    Item item = getItemByUuid(uuid);

                    ItemDto itemDto = ItemDto.from(item);

                    cacheRedisTemplate.opsForValue().set(itemCacheKey, itemDto, Duration.ofDays(1L));

                    return itemDto;


                } finally {
                    lock.unlock();
                }
            } else {
                for (int i = 0; i < 3; i++) {
                    Thread.sleep(100);
                    ItemDto retryCache = (ItemDto) cacheRedisTemplate.opsForValue().get(itemCacheKey);
                    if (retryCache != null) return retryCache;
                }
                throw new AppException(ITEM_NOT_FOUND);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AppException(FAIL_ROCK_ACQUIRE);
        }
    }

    @Transactional(readOnly = true)
    public Page<SimpleItemDto> searchItem(ItemSearchCond itemSearchCond, Pageable pageable) {
        return itemRepository.searchItem(itemSearchCond, pageable);
    }

    public MessageResponse createItem(ItemCreateRequest request, List<MultipartFile> multipartFileList) {

        itemRepository.findItemByItemName(request.getItemName())
                .ifPresent((item -> {
                    throw new AppException(DUPLICATE_ITEM);
                }));

        Brand findBrand = getBrandByName(request.getBrandName());

        Item savedItem = itemRepository.save(request.toEntity(findBrand));

        for (int i = 0; i < multipartFileList.size(); i++) {
            MultipartFile multipartFile = multipartFileList.get(i);

            String originImageUrl = awsS3Service.uploadItemOriginImage(multipartFile);

            ImageFile image = ImageFile.createImage(originImageUrl);

            // 연관관계 정의
            image.addItem(savedItem);

            imageFileRepository.save(image);

            if (i == 0) savedItem.setThumbnail(originImageUrl);
        }

        return MessageResponse.of(savedItem.getUuid(), messageUtil.get(MessageCode.ITEM_ADDED));
    }

    public MessageResponse updateItem(UUID uuid, ItemUpdateRequest request, List<MultipartFile> multipartFileList) {

        Item findItem = getItemByUuid(uuid);

        Brand findBrand = null;
        if (request.getBrandName() != null) {
            findBrand = getBrandByName(request.getBrandName());
        }

        findItem.updateItem(request, findBrand);

        if (multipartFileList != null && !multipartFileList.isEmpty()) {

            // 1. 기존 이미지 삭제
            for (ImageFile imageFile : findItem.getImageFileList()) {
                String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());

                imageFile.removeItem();  // 연관관계 제거
                awsS3Service.deleteBrandImage(extractFileName);
            }

            // 2. 새로운 이미지 업로드 및 저장
            for (int i = 0; i < multipartFileList.size(); i++) {
                MultipartFile multipartFile = multipartFileList.get(i);
                String newUrl = awsS3Service.uploadItemOriginImage(multipartFile);

                ImageFile image = ImageFile.createImage(newUrl);
                image.addItem(findItem);
                imageFileRepository.save(image); // 저장 누락되어 있던 부분

                // 3. 첫 번째 이미지면 썸네일로 지정
                if (i == 0) findItem.setThumbnail(newUrl);
            }
        }

        String itemCacheKey = RedisKeyHelper.getItemCacheKey(uuid);
        cacheRedisTemplate.delete(itemCacheKey);

        return MessageResponse.of(findItem.getUuid(), messageUtil.get(MessageCode.ITEM_MODIFIED));

    }

    public MessageResponse deleteItem(UUID uuid) {

        Item findItem = getItemByUuid(uuid);

        for (ImageFile imageFile : findItem.getImageFileList()) {
            String extractFileName = FileUtils.extractFileName(imageFile.getImageUrl());

            awsS3Service.deleteItemImage(extractFileName);

            imageFileRepository.deleteById(imageFile.getId());
        }

        itemRepository.deleteById(findItem.getId());

        String itemCacheKey = RedisKeyHelper.getItemCacheKey(uuid);
        cacheRedisTemplate.delete(itemCacheKey);

        return MessageResponse.of(findItem.getUuid(), messageUtil.get(MessageCode.ITEM_DELETED));
    }

    private Item getItemByUuid(UUID uuid) {
        return itemRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(ITEM_NOT_FOUND));
    }

    private Brand getBrandByName(String brandName) {
        return brandRepository.findBrandByBrandName(brandName)
                .orElseThrow(() -> new AppException(BRAND_NOT_FOUND));
    }
}
