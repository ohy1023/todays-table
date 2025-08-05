package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.service.CartService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart", description = "장바구니 관련 API")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 품목 추가",
            description = "장바구니에 새로운 품목을 추가합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "장바구니에 품목 추가 성공"),
            @ApiResponse(responseCode = "409", description = "재고 부족")
    })
    @PostMapping
    public ResponseEntity<Response<MessageResponse>> addItemToCart(
            @Valid @RequestBody CartAddRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = cartService.addItemToCart(request, email);
        URI location = URI.create("/api/v1/carts");
        return ResponseEntity
                .created(location)
                .body(Response.success(response));
    }

    @Operation(
            summary = "장바구니 전체 비우기",
            description = "장바구니의 모든 품목을 삭제합니다."
    )
    @ApiResponse(responseCode = "204", description = "장바구니 비우기 성공")
    @DeleteMapping
    public ResponseEntity<Void> clearCart(Authentication authentication) {
        String email = authentication.getName();
        cartService.clearCart(email);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "장바구니 품목 삭제",
            description = "장바구니에서 특정 품목을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "품목 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 고객 또는 품목이 존재하지 않음")
    })
    @DeleteMapping("/{itemUuid}")
    public ResponseEntity<Void> deleteItemFromCart(
            @Parameter(description = "삭제할 품목 UUID", example = "cffb8f4d-2be3-11f0-bff7-453261748c60")
            @PathVariable UUID itemUuid,
            Authentication authentication) {
        String email = authentication.getName();
        cartService.deleteItemFromCart(itemUuid, email);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "장바구니 품목 전체 조회",
            description = "장바구니에 담긴 모든 품목을 페이징으로 조회합니다."
    )
    @ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
    @GetMapping
    public ResponseEntity<Response<Page<CartItemResponse>>> getCartItems(
            Authentication authentication,
            @ParameterObject Pageable pageable) {
        String email = authentication.getName();
        Page<CartItemResponse> response = cartService.getCartItems(email, pageable);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "장바구니 초기 품목 3개 삽입", description = "테스트용 초기 장바구니 품목 3개를 삽입합니다.")
    @ApiResponse(responseCode = "201", description = "초기 품목 삽입 성공")
    @PostMapping("/init-cart-items")
    public ResponseEntity<Response<MessageResponse>> insertInitialCartItems(Authentication authentication) {
        String email = authentication.getName();
        cartService.insertInitialCartItems(email);
        return ResponseEntity
                .status(201)
                .body(Response.success(new MessageResponse("장바구니에 초기 아이템 3개가 삽입되었습니다.")));
    }
}
