package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/items")
@Tag(name = "Item", description = "품목 API")
public class ItemController {

    private final ItemService itemService;


    @Operation(summary = "품목 단건 조회")
    @GetMapping("/{itemId}")
    public Response<ItemDto> findItem(@PathVariable Long itemId) {

        ItemDto response = itemService.getItemById(itemId);

        return Response.success(response);
    }

    @Operation(summary = "품목 검색")
    @GetMapping
    public Response<Page<ItemDto>> searchItem(ItemSearchCond itemSearchCond, Pageable pageable) {

        Page<ItemDto> response = itemService.searchItem(itemSearchCond, pageable);

        return Response.success(response);
    }

    @Operation(summary = "품목 추가")
    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<ItemDto> createItem(@RequestPart ItemCreateRequest request, @RequestPart List<MultipartFile> multipartFileList) {

        ItemDto response = itemService.createItem(request, multipartFileList);

        return Response.success(response);
    }

    @Operation(summary = "품목 수정")
    @PutMapping(value = "/{itemId}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public Response<MessageResponse> changeItem(@PathVariable Long itemId, @RequestPart ItemUpdateRequest request, @RequestPart(required = false) List<MultipartFile> multipartFileList) {
        return Response.success(itemService.updateItem(itemId, request, multipartFileList));
    }

    @Operation(summary = "품목 삭제")
    @DeleteMapping("/{itemId}")
    public Response<MessageResponse> removeItem(@PathVariable Long itemId) {
        return Response.success(itemService.deleteItem(itemId));
    }
}
