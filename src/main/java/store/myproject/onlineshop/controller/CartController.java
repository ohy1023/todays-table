package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
import store.myproject.onlineshop.domain.cartitem.dto.CartItemResponse;
import store.myproject.onlineshop.service.CartService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/carts")
@Tag(name = "Cart", description = "장바구니 관련 API")
public class CartController {

    private final CartService cartService;

    @Operation(
            summary = "장바구니 품목 추가",
            description = "장바구니에 새로운 품목을 추가합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "장바구니에 품목 추가 성공"),
            @ApiResponse(responseCode = "409", description = "재고 부족 또는 기타 충돌")
    })
    @PostMapping
    public Response<MessageResponse> addItemToCart(
            @Valid @RequestBody CartAddRequest request,
            Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = cartService.addItemToCart(request, email);
        return Response.success(response);
    }

    @Operation(
            summary = "장바구니 전체 비우기",
            description = "장바구니의 모든 품목을 삭제합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "200", description = "장바구니 비우기 성공")
    @DeleteMapping
    public Response<MessageResponse> clearCart(Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = cartService.clearCart(email);
        return Response.success(response);
    }

    @Operation(
            summary = "장바구니 품목 삭제",
            description = "장바구니에서 특정 품목을 삭제합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "품목 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "해당 품목이 존재하지 않음")
    })
    @DeleteMapping("/{itemId}")
    public Response<MessageResponse> deleteItemFromCart(
            @Parameter(description = "삭제할 품목 ID", example = "1")
            @PathVariable Long itemId,
            Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = cartService.deleteItemFromCart(itemId, email);
        return Response.success(response);
    }

    @Operation(
            summary = "장바구니 품목 전체 조회",
            description = "장바구니에 담긴 모든 품목을 페이징으로 조회합니다.",
            security = @SecurityRequirement(name = "JWT")
    )
    @ApiResponse(responseCode = "200", description = "장바구니 조회 성공")
    @GetMapping
    public Response<Page<CartItemResponse>> getCartItems(
            Authentication authentication,
            @ParameterObject Pageable pageable) {
        String email = authentication.getName();
        Page<CartItemResponse> response = cartService.getCartItems(email, pageable);
        return Response.success(response);
    }
}
