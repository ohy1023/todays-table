package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.service.BrandService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/brands")
@Tag(name = "Brand", description = "브랜드 관리 API")
public class BrandController {

    private final BrandService brandService;

    @Operation(summary = "브랜드 단건 조회", description = "브랜드 ID를 이용해 특정 브랜드 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "정상적으로 조회됨"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 브랜드 ID")
    })
    @GetMapping("/{brandId}")
    public Response<BrandInfo> getBrandById(
            @Parameter(description = "브랜드 ID", example = "1")
            @PathVariable Long brandId) {
        BrandInfo brandInfo = brandService.findBrandInfoById(brandId);
        return Response.success(brandInfo);
    }

    @Operation(summary = "브랜드 검색", description = "브랜드 이름(부분 일치)으로 브랜드 목록을 검색합니다. 페이징이 적용됩니다.")
    @ApiResponse(responseCode = "200", description = "브랜드 검색 성공")
    @GetMapping("/search")
    public Response<Page<BrandInfo>> searchBrands(
            @Parameter(description = "브랜드 이름", example = "풀무원")
            @RequestParam(required = false) String brandName,
            @ParameterObject Pageable pageable) {
        Page<BrandInfo> brands = brandService.searchBrands(brandName, pageable);
        return Response.success(brands);
    }

    @Operation(
            summary = "브랜드 등록",
            description = "브랜드 정보와 이미지 파일을 함께 등록합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "브랜드 등록 성공"),
            @ApiResponse(responseCode = "409", description = "중복된 브랜드 이름")
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<MessageResponse> createBrand(
            @Valid @RequestPart BrandCreateRequest request,
            @RequestPart MultipartFile multipartFile) {

        return Response.success(brandService.createBrand(request, multipartFile));
    }

    @Operation(
            summary = "브랜드 수정",
            description = "브랜드 정보를 수정합니다. 이미지 파일을 선택적으로 포함할 수 있습니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "브랜드 수정 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 브랜드 ID")
    })
    @PutMapping(value = "/{brandId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Response<MessageResponse> updateBrand(
            @Parameter(description = "브랜드 ID", example = "1")
            @PathVariable Long brandId,
            @Valid @RequestPart BrandUpdateRequest request,
            @RequestPart(required = false) MultipartFile multipartFile) {
        return Response.success(brandService.updateBrand(brandId, request, multipartFile));
    }

    @Operation(
            summary = "브랜드 삭제",
            description = "특정 브랜드를 삭제합니다. 연관 이미지도 함께 제거됩니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "브랜드 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 브랜드 ID")
    })
    @DeleteMapping("/{brandId}")
    public Response<MessageResponse> deleteBrand(
            @Parameter(description = "브랜드 ID", example = "1")
            @PathVariable Long brandId) {
        return Response.success(brandService.deleteBrand(brandId));
    }
}
