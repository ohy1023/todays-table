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
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.ErrorResponse;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.item.dto.*;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.service.ItemService;
import store.myproject.onlineshop.service.RecipeService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
@Tag(name = "Item", description = "품목 API")
public class ItemController {

    private final ItemService itemService;
    private final RecipeService recipeService;

    @Operation(summary = "품목 단건 조회", description = "UUID를 기준으로 품목 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "해당 품목 없음", content = @Content(mediaType = "application/json", schema = @Schema(implementation = Response.class)))
    })
    @GetMapping("/{itemUuid}")
    public Response<ItemDto> findItem(
            @Parameter(description = "조회할 품목의 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
            @PathVariable UUID itemUuid
    ) {

        ItemDto response = itemService.getItem(itemUuid);

        return Response.success(response);
    }

    @Operation(summary = "품목 검색", description = "조건을 기준으로 품목을 검색합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "검색 성공")
    })
    @GetMapping
    public Response<Page<SimpleItemDto>> searchItem(
            ItemSearchCond itemSearchCond,
            @Parameter(description = "페이지 정보 (예: page=0&size=10&sort=name,asc)")
            @ParameterObject Pageable pageable
            ) {

        Page<SimpleItemDto> response = itemService.searchItem(itemSearchCond, pageable);

        return Response.success(response);
    }

    @Operation(summary = "품목 추가", description = "새로운 품목을 등록합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "등록 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<MessageResponse> createItem(@Valid @RequestPart ItemCreateRequest request, @RequestPart List<MultipartFile> multipartFileList) {

        return Response.success(itemService.createItem(request, multipartFileList));
    }

    @Operation(summary = "품목 수정", description = "기존 품목 정보를 수정합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "해당 품목 없음")
    })
    @PutMapping(value = "/{itemUuid}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<MessageResponse> changeItem(
            @Parameter(description = "수정할 품목의 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
            @PathVariable UUID itemUuid,
            @Valid @RequestPart ItemUpdateRequest request,
            @RequestPart(required = false) List<MultipartFile> multipartFileList
    ) {
        return Response.success(itemService.updateItem(itemUuid, request, multipartFileList));
    }

    @Operation(summary = "품목 삭제", description = "UUID를 기준으로 품목을 삭제합니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 품목 없음")
    })
    @DeleteMapping("/{itemUuid}")
    public Response<MessageResponse> removeItem(
            @Parameter(description = "삭제할 품목의 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
            @PathVariable UUID itemUuid
    ) {
        return Response.success(itemService.deleteItem(itemUuid));
    }

    @Operation(
            summary = "해당 아이템 사용하는 레시피 목록 조회",
            description = "아이템을 사용하는 레시피 목록을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "레시피 목록 조회 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음")
    })
    @GetMapping("/{itemUuid}/recipes")
    Response<Page<SimpleRecipeDto>> findRecipesByItem(
            @Parameter(name = "itemUuid", description = "조회할 품목의 UUID", example = "a497a803-2b32-11f0-9178-1583b134d536", required = true)
            @PathVariable UUID itemUuid,
            @Parameter(description = "페이지 정보 (size, page, sort)")
            @PageableDefault(size = 5, sort = "created_date,desc") Pageable pageable
    ) {
        return Response.success(recipeService.getRecipesByItem(itemUuid, pageable));
    }
}
