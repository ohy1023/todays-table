package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.service.ItemService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    @Tag(name = "Item", description = "품목 API")
    @Operation(summary = "품목 단건 조회")
    @GetMapping("/{itemId}")
    public Response<ItemDto> findItem(@PathVariable Long itemId) {

        ItemDto response = itemService.selectOne(itemId);

        return Response.success(response);
    }

    @Tag(name = "Item", description = "품목 API")
    @Operation(summary = "품목 검색")
    @GetMapping
    public Response<Page<ItemDto>> createItem(ItemSearchCond itemSearchCond, Pageable pageable) {

        Page<ItemDto> response = itemService.searchItem(itemSearchCond, pageable);

        return Response.success(response);
    }

    @Tag(name = "Item", description = "품목 API")
    @Operation(summary = "품목 추가")
    @PostMapping
    public Response<ItemDto> createItem(@RequestPart ItemCreateRequest request, @RequestPart MultipartFile multipartFile, Authentication authentication) {
        ItemDto response = itemService.saveItem(request, multipartFile);

        return Response.success(response);
    }

    @Tag(name = "Item", description = "품목 API")
    @Operation(summary = "품목 수정")
    @PatchMapping("/{itemId}")
    public Response<ItemDto> changeItem(@PathVariable Long itemId, @RequestPart ItemUpdateRequest request, @RequestPart MultipartFile multipartFile, Authentication authentication) {
        ItemDto response = itemService.updateItem(itemId, request, multipartFile);

        return Response.success(response);
    }

    @Tag(name = "Item", description = "품목 API")
    @Operation(summary = "품목 삭제")
    @DeleteMapping("/{itemId}")
    public Response<MessageResponse> removeItem(@PathVariable Long itemId, Authentication authentication) {
        MessageResponse response = itemService.deleteItem(itemId);

        return Response.success(response);
    }
}
