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
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.repository.imagefile.ImageFileRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.FileUtils;
import store.myproject.onlineshop.repository.brand.BrandRepository;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.UUID;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BrandService {

    private final ImageFileRepository imageFileRepository;
    private final BrandRepository brandRepository;
    private final AwsS3Service awsS3Service;
    private final MessageUtil messageUtil;

    /**
     * 브랜드 단건 조회 (캐싱 적용)
     */
    @Transactional(readOnly = true)
    @Cacheable(value = "brands", key = "#uuid")
    public BrandInfo findBrandInfoById(UUID uuid) {
        return findBrandOrThrow(uuid).toBrandInfo();
    }

    /**
     * 브랜드 전체 조회 (검색어 + 페이징)
     */
    @Transactional(readOnly = true)
    public Page<BrandInfo> searchBrands(String brandName, Pageable pageable) {
        return brandRepository.search(brandName, pageable);
    }

    /**
     * 브랜드 등록
     */
    public MessageResponse createBrand(BrandCreateRequest request, MultipartFile multipartFile) {
        validateBrandNameUniqueness(request.getName());

        String imageUrl = awsS3Service.uploadBrandOriginImage(multipartFile);
        Brand savedBrand = brandRepository.save(request.toEntity());

        ImageFile image = ImageFile.createImage(imageUrl, savedBrand);
        image.addBrand(savedBrand);
        imageFileRepository.save(image);

        return new MessageResponse(savedBrand.getUuid(), messageUtil.get(MessageCode.BRAND_ADDED));
    }

    /**
     * 브랜드 수정 (이미지 변경 포함, 캐시 초기화)
     */
    @CacheEvict(value = "brands", allEntries = true)
    public MessageResponse updateBrand(UUID uuid, BrandUpdateRequest request, MultipartFile multipartFile) {
        Brand brand = findBrandOrThrow(uuid);

        if (multipartFile != null) {
            String oldFileName = FileUtils.extractFileName(brand.getImageFile().getImageUrl());
            awsS3Service.deleteBrandImage(oldFileName);

            ImageFile oldImage = brand.getImageFile();
            oldImage.removeBrand();
            imageFileRepository.deleteById(oldImage.getId());

            String newImageUrl = awsS3Service.uploadBrandOriginImage(multipartFile);
            ImageFile newImage = ImageFile.createImage(newImageUrl, brand);
            newImage.addBrand(brand);
            imageFileRepository.save(newImage);
        }

        brand.update(request);

        return new MessageResponse(brand.getUuid(), messageUtil.get(MessageCode.BRAND_MODIFIED));
    }

    /**
     * 브랜드 삭제 (캐시 초기화 및 이미지 삭제 포함)
     */
    @CacheEvict(value = "brands", allEntries = true)
    public MessageResponse deleteBrand(UUID uuid) {
        Brand brand = findBrandOrThrow(uuid);

        ImageFile image = brand.getImageFile();
        String fileName = FileUtils.extractFileName(image.getImageUrl());
        awsS3Service.deleteBrandImage(fileName);

        brandRepository.deleteById(brand.getId());
        return new MessageResponse(brand.getUuid(), messageUtil.get(MessageCode.BRAND_DELETED));
    }

    /**
     * 브랜드 UUID로 조회 (없으면 예외 발생)
     */
    private Brand findBrandOrThrow(UUID uuid) {
        return brandRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));
    }

    /**
     * 브랜드 이름 중복 여부 검증 (중복이면 예외)
     */
    private void validateBrandNameUniqueness(String brandName) {
        if (brandRepository.existsByName(brandName)) {
            throw new AppException(DUPLICATE_BRAND);
        }
    }
}