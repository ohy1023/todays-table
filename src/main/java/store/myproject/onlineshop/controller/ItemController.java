package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
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

    @Operation(summary = "품목 단건 조회")
    @GetMapping("/{uuid}")
    public Response<ItemDto> findItem(@PathVariable UUID uuid) {

        ItemDto response = itemService.getItem(uuid);

        return Response.success(response);
    }

    @Operation(summary = "품목 검색")
    @GetMapping
    public Response<Page<SimpleItemDto>> searchItem(ItemSearchCond itemSearchCond, Pageable pageable) {

        Page<SimpleItemDto> response = itemService.searchItem(itemSearchCond, pageable);

        return Response.success(response);
    }

    @Operation(summary = "품목 추가")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<MessageResponse> createItem(@RequestPart ItemCreateRequest request, @RequestPart List<MultipartFile> multipartFileList) {

        return Response.success(itemService.createItem(request, multipartFileList));
    }

    @Operation(summary = "품목 수정")
    @PutMapping(value = "/{uuid}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<MessageResponse> changeItem(@PathVariable UUID uuid, @RequestPart ItemUpdateRequest request, @RequestPart(required = false) List<MultipartFile> multipartFileList) {
        return Response.success(itemService.updateItem(uuid, request, multipartFileList));
    }

    @Operation(summary = "품목 삭제")
    @DeleteMapping("/{uuid}")
    public Response<MessageResponse> removeItem(@PathVariable UUID uuid) {
        return Response.success(itemService.deleteItem(uuid));
    }

//    @Operation(summary = "해당 아이템 사용하는 레시피 목록 조회")
//    @GetMapping("/{uuid}/recipes")
//    Response<Page<SimpleRecipeDto>> findRecipesByItem(@PathVariable UUID uuid, Pageable pageable) {
//        return Response.success(recipeService.getRecipesByItem(uuid, pageable));
//    }
}
