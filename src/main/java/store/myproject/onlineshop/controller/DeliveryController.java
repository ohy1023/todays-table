package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryInfoRequest;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.service.DeliveryService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
@Tag(name = "Delivery", description = "배송")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @Operation(summary = "배송지 변경")
    @PutMapping("/{orderId}")
    public Response<OrderInfo> changeDeliveryInfo(@PathVariable Long orderId, @RequestBody DeliveryUpdateRequest request, Authentication authentication) {

        OrderInfo response = deliveryService.updateDeliveryInfo(orderId, request);

        return Response.success(response);
    }
}
