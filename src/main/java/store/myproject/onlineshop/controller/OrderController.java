package store.myproject.onlineshop.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.domain.order.dto.*;
import store.myproject.onlineshop.service.OrderService;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "단건 주문 조회")
    @GetMapping("/{orderId}")
    public Response<OrderInfo> findOneOrder(@PathVariable Long orderId, Authentication authentication) {

        String email = authentication.getName();

        OrderInfo response = orderService.findOrder(orderId, email);

        return Response.success(response);
    }

    @Operation(summary = "나의 주문 내역 검색")
    @GetMapping("/search")
    public Response<Page<OrderInfo>> searchOrder(OrderSearchCond orderSearchCond, Authentication authentication, Pageable pageable) {

        String email = authentication.getName();

        Page<OrderInfo> response = orderService.searchMyOrders(orderSearchCond, email, pageable);

        return Response.success(response);
    }

    @Operation(summary = "단건 주문")
    @PostMapping
    public Response<OrderInfo> order(@RequestBody OrderInfoRequest request, Authentication authentication) {

        String email = authentication.getName();

        OrderInfo response = orderService.orderByOne(request, email);

        return Response.success(response);
    }

    @Operation(summary = "장바구니 내 품목 구매")
    @PostMapping("/cart")
    public Response<List<OrderInfo>> order(@RequestBody CartOrderRequest request, Authentication authentication) {

        String email = authentication.getName();

        List<OrderInfo> response = orderService.orderByCart(request, email);

        return Response.success(response);
    }

    @Operation(summary = "주문 취소")
    @DeleteMapping("/{orderItemId}")
    public Response<MessageResponse> cancel(@PathVariable Long orderItemId, Authentication authentication) throws IamportResponseException, IOException {
        return Response.success(orderService.cancelForOrder(orderItemId));
    }

    @Operation(summary = "사전 검증")
    @PostMapping("/preparation")
    public Response<PreparationResponse> prepareValid(@RequestBody PreparationRequest preparationRequest, Authentication authentication) throws IamportResponseException, IOException {
        return Response.success(orderService.prepareValid(preparationRequest));
    }

    @Operation(summary = "사후 검증")
    @PostMapping("/verification")
    public Response<MessageResponse> postVerification(@RequestBody PostVerificationRequest postVerificationRequest, Authentication authentication) throws IamportResponseException, IOException {
        log.info("imp_uid:{}", postVerificationRequest.getImpUid());
        return Response.success(orderService.postVerification(postVerificationRequest));
    }
}
