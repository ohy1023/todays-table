package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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

        OrderInfo response = orderService.orderByOne(request, authentication);

        return Response.success(response);
    }
}
