package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.item.dto.*;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.service.ItemService;
import store.myproject.onlineshop.service.RecipeService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
@Tag(name = "Item", description = "상품 API")
public class ItemController {

    private final ItemService itemService;
    private final RecipeService recipeService;

    @Operation(summary = "상품 단건 조회", description = "UUID를 기준으로 상품 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상품 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{itemUuid}")
    public ResponseEntity<Response<ItemDto>> findItem(
            @Parameter(description = "조회할 상품 UUID", example = "cffb8f4d-2be3-11f0-bff7-453261748c60")
            @PathVariable UUID itemUuid
    ) {
        ItemDto response = itemService.getItem(itemUuid);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(
            summary = "상품 목록 (조건) 조회",
            description = """
                    조건을 기준으로 상품 검색합니다.
                    우선순위:
                    1. itemName이 있으면 itemName으로 검색합니다.
                    2. itemName이 없고 brandName이 있으면 brandName으로 검색합니다.
                    3. itemName과 brandName이 모두 없으면 전체 상품 조회합니다.
                    """
    )
    @GetMapping
    public ResponseEntity<Response<Page<SimpleItemDto>>> searchItem(
            ItemSearchCond itemSearchCond,
            @ParameterObject Pageable pageable
    ) {
        Page<SimpleItemDto> response = itemService.searchItem(itemSearchCond, pageable);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "상품 추가", description = "새로운 상품 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response<MessageResponse>> createItem(
            @Valid @RequestPart ItemCreateRequest request,
            @RequestPart List<MultipartFile> multipartFileList
    ) {
        MessageResponse response = itemService.createItem(request, multipartFileList);

        String createdItemUuid = response.getUuid().toString();

        return ResponseEntity.created(
                        URI.create("/api/v1/items/" + createdItemUuid))
                .body(Response.success(response));
    }

    @Operation(summary = "상품 수정", description = "기존 상품 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상품 없음")
    })
    @PutMapping(value = "/{itemUuid}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Response<MessageResponse>> changeItem(
            @Parameter(description = "수정할 상품 UUID", example = "cffb8f4d-2be3-11f0-bff7-453261748c60")
            @PathVariable UUID itemUuid,
            @Valid @RequestPart ItemUpdateRequest request,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    ) {
        MessageResponse response = itemService.updateItem(itemUuid, request, multipartFileList);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "상품 삭제", description = "UUID를 기준으로 상품 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 상품 없음")
    })
    @DeleteMapping("/{itemUuid}")
    public ResponseEntity<Void> removeItem(
            @Parameter(description = "삭제할 상품 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
            @PathVariable UUID itemUuid
    ) {
        itemService.deleteItem(itemUuid);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "해당 상품 사용하는 레시피 목록 조회",
            description = "해당 상품을 사용하는 레시피 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "레시피 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음")
    })
    @GetMapping("/{itemUuid}/recipes")
    public ResponseEntity<Response<Page<SimpleRecipeDto>>> findRecipesByItem(
            @Parameter(name = "itemUuid", description = "조회할 상품 UUID", example = "cffb8f4d-2be3-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID itemUuid,
            @ParameterObject Pageable pageable
    ) {
        Page<SimpleRecipeDto> response = recipeService.getRecipesByItem(itemUuid, pageable);
        return ResponseEntity.ok(Response.success(response));
    }
}
