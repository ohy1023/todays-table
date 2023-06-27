package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.service.BrandService;

import java.util.List;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
public class BrandController {

    private final BrandService brandService;

    @Tag(name = "Brand", description = "브랜드 API")
    @Operation(summary = "브랜드 단건 조회")
    @GetMapping("/{brandId}")
    public Response<BrandInfo> getBrand(@PathVariable Long brandId) {

        BrandInfo brandInfo = brandService.getBrandInfo(brandId);

        return Response.success(brandInfo);
    }

    @Tag(name = "Brand", description = "브랜드 API")
    @Operation(summary = "브랜드 전체 조회")
    @GetMapping("/search")
    public Response<List<BrandInfo>> getBrands() {

        List<BrandInfo> brands = brandService.getBrandInfos();

        return Response.success(brands);
    }

    @Tag(name = "Brand", description = "브랜드 API")
    @Operation(summary = "브랜드 등록")
    @PostMapping
    public Response<BrandCreateResponse> createBrand(@Valid @RequestPart BrandCreateRequest request, @RequestPart MultipartFile multipartFile) {

        BrandCreateResponse response = brandService.saveBrand(request, multipartFile);

        return Response.success(response);
    }

    @Tag(name = "Brand", description = "브랜드 API")
    @Operation(summary = "브랜드 수정")
    @PatchMapping("/{brandId}")
    public Response<BrandUpdateResponse> changeBrand(@PathVariable Long brandId, @RequestPart BrandUpdateRequest request, @RequestParam(required = false) MultipartFile multipartFile) {
        BrandUpdateResponse response = brandService.updateBrand(brandId, request, multipartFile);

        return Response.success(response);
    }

    @Tag(name = "Brand", description = "브랜드 API")
    @Operation(summary = "브랜드 삭제")
    @DeleteMapping("/{brandId}")
    public Response<BrandDeleteResponse> removeBrand(@PathVariable Long brandId) {
        BrandDeleteResponse response = brandService.deleteBrand(brandId);

        return Response.success(response);
    }
}
