package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
@Tag(name = "Cart", description = "장바구니")
public class CartController {

    private final CartService cartService;

    @Operation(summary = "장바구니에 해당 품목 넣기")
    @PostMapping
    public Response<MessageResponse> appendItemByCart(@Valid @RequestBody CartAddRequest request, Authentication authentication) {

        String email = authentication.getName();

        MessageResponse response = cartService.addCart(request, email);

        return Response.success(response);
    }

    @Operation(summary = "장바구니에 모든 품목 삭제")
    @DeleteMapping
    public Response<MessageResponse> removeAllCart(Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = cartService.deleteCarts(email);
        return Response.success(response);
    }

    @Operation(summary = "장바구니에 해당 품목 삭제")
    @DeleteMapping("/{itemId}")
    public Response<MessageResponse> removeItem(@PathVariable Long itemId, Authentication authentication) {
        String email = authentication.getName();
        MessageResponse response = cartService.deleteItem(itemId, email);
        return Response.success(response);
    }

    @Operation(summary = "장바구니 품목 전체 조회")
    @GetMapping
    public Response<Page<CartItemResponse>> lookupCartItems(Authentication authentication, Pageable pageable) {
        String email = authentication.getName();
        Page<CartItemResponse> response = cartService.selectAllCartItem(email, pageable);
        return Response.success(response);
    }

    @Operation(summary = "장바구니에서 구매할 물건 체크 변경")
    @PutMapping("/{cartItemId}")
    public Response<MessageResponse> changeCheck(@PathVariable Long cartItemId, Authentication authentication) {
        String email = authentication.getName();

        MessageResponse response = cartService.updateCheck(cartItemId, email);

        return Response.success(response);
    }


}
