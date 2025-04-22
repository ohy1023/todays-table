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
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.imagefile.ImageFile;
import store.myproject.onlineshop.domain.imagefile.repository.ImageFileRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.s3.service.AwsS3Service;
import store.myproject.onlineshop.global.utils.FileUtils;
import store.myproject.onlineshop.domain.brand.repository.BrandRepository;


import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BrandService {
    private final ImageFileRepository imageFileRepository;

    private final BrandRepository brandRepository;

    private final AwsS3Service awsS3Service;

    // 브랜드 단건 조회
    @Transactional(readOnly = true)
    @Cacheable(value = "brands", key = "#id")
    public BrandInfo getBrandInfo(Long id) {
        return getBrandOrElseThrow(id).toBrandInfo();
    }

    // 브랜드 전체 조회
    @Transactional(readOnly = true)
    public Page<BrandInfo> getBrandInfos(String brandName, Pageable pageable) {
        return brandRepository.search(brandName, pageable);
    }

    // 브랜드 추가
    public BrandCreateResponse saveBrand(BrandCreateRequest request, MultipartFile multipartFile) {

        checkDuplicatedBrand(request.getName());

        String originImageUrl = awsS3Service.uploadBrandOriginImage(multipartFile);

        Brand savedBrand = brandRepository.save(request.toEntity());

        ImageFile image = ImageFile.createImage(originImageUrl, savedBrand);

        image.addBrand(savedBrand);

        imageFileRepository.save(image);

        return savedBrand.toBrandCreateResponse(originImageUrl);
    }

    // 브랜드 수정
    @CacheEvict(value = "brands", allEntries = true)
    public BrandUpdateResponse updateBrand(Long id, BrandUpdateRequest request, MultipartFile multipartFile) {

        Brand brand = getBrandOrElseThrow(id);

        if (multipartFile != null) {
            String extractFileName = FileUtils.extractFileName(brand.getImageFile().getImageUrl());

            awsS3Service.deleteBrandImage(extractFileName);

            ImageFile curImageFile = brand.getImageFile();

            curImageFile.removeBrand();

            imageFileRepository.deleteById(curImageFile.getId());

            String newUrl = awsS3Service.uploadBrandOriginImage(multipartFile);

            ImageFile image = ImageFile.createImage(newUrl, brand);

            image.addBrand(brand);

            imageFileRepository.save(image);
        }

        brand.update(request);

        return brand.toBrandUpdateResponse();
    }

    // 브랜드 삭제
    @CacheEvict(value = "brands", allEntries = true)
    public BrandDeleteResponse deleteBrand(Long id) {

        Brand brand = getBrandOrElseThrow(id);

        ImageFile curImageFile = brand.getImageFile();

        String extractFileName = FileUtils.extractFileName(curImageFile.getImageUrl());

        awsS3Service.deleteBrandImage(extractFileName);

        brandRepository.deleteById(id);

        return brand.toBrandDeleteResponse();
    }


    // 브랜드 찾기
    @Transactional(readOnly = true)
    public Brand getBrandOrElseThrow(Long id) {
        return brandRepository.findById(id).orElseThrow(() -> new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));
    }

    // 브랜드 명 중복 확인
    @Transactional(readOnly = true)
    public void checkDuplicatedBrand(String brandName) {
        if (brandRepository.existsByName(brandName)) {
            throw new AppException(DUPLICATE_BRAND);
        }
    }


}
