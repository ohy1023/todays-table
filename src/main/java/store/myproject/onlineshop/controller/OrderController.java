package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
import store.myproject.onlineshop.service.OrderService;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;


    @Operation(summary = "단건 주문")
    @PostMapping
    public Response<OrderInfo> order(@RequestBody OrderInfoRequest request, Authentication authentication) {

        String email = authentication.getName();

        OrderInfo response = orderService.orderByOne(request, email);

        return Response.success(response);
    }

    @Operation(summary = "주문 취소")
    @DeleteMapping("/{orderId}")
    public Response<MessageResponse> cancel(@PathVariable Long orderId, Authentication authentication) {

        String email = authentication.getName();

        MessageResponse response = orderService.cancelForOrder(orderId);

        return Response.success(response);
    }
}
