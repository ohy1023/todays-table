package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.cart.dto.CartAddRequest;
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

        MessageResponse response = cartService.addCart(request, authentication);

        return Response.success(response);
    }

    @Operation(summary = "장바구니에 무슨 품목 삭제")
    @DeleteMapping
    public Response<MessageResponse> removeAllCart(Authentication authentication) {
        MessageResponse response = cartService.deleteCarts(authentication);
        return Response.success(response);
    }

}
