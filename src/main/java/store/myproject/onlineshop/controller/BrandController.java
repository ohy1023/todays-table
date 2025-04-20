package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.service.BrandService;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
@Tag(name = "Brand", description = "브랜드 API")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "브랜드 단건 조회")
    @GetMapping("/{brandId}")
    public Response<BrandInfo> getBrand(@PathVariable Long brandId) {

        BrandInfo brandInfo = brandService.getBrandInfo(brandId);

        return Response.success(brandInfo);
    }

    @Operation(summary = "브랜드 조회")
    @GetMapping("/search")
    public Response<Page<BrandInfo>> getBrands(@RequestParam(required = false) String brandName, Pageable pageable) {

        Page<BrandInfo> brands = brandService.getBrandInfos(brandName, pageable);

        return Response.success(brands);
    }


    @Operation(summary = "브랜드 등록")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<BrandCreateResponse> createBrand(@Valid @RequestPart BrandCreateRequest request, @RequestPart MultipartFile multipartFile, Authentication authentication) {

        BrandCreateResponse response = brandService.saveBrand(request, multipartFile);

        return Response.success(response);
    }

    @Operation(summary = "브랜드 수정")
    @PutMapping(value = "/{brandId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<BrandUpdateResponse> changeBrand(@PathVariable Long brandId, @RequestPart BrandUpdateRequest request, @RequestPart(required = false) MultipartFile multipartFile, Authentication authentication) {
        BrandUpdateResponse response = brandService.updateBrand(brandId, request, multipartFile);

        return Response.success(response);
    }

    @Operation(summary = "브랜드 삭제")
    @DeleteMapping("/{brandId}")
    public Response<BrandDeleteResponse> removeBrand(@PathVariable Long brandId, Authentication authentication) {
        BrandDeleteResponse response = brandService.deleteBrand(brandId);

        return Response.success(response);
    }
}
