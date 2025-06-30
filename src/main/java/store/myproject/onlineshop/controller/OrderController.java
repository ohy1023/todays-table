package store.myproject.onlineshop.controller;

import com.siot.IamportRestClient.exception.IamportResponseException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.domain.delivery.dto.DeliveryUpdateRequest;
import store.myproject.onlineshop.domain.order.dto.*;
import store.myproject.onlineshop.service.OrderService;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
@Tag(name = "Order", description = "주문 API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "단건 주문 조회", description = "특정 주문을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "성공적으로 주문을 조회했습니다."),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다."),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없습니다.")
    })
    @GetMapping("/{merchantUid}")
    public Response<OrderInfo> findOneOrder(
            @Parameter(description = "주문을 조회할 고유 식별자", example = "9115f8f7-2b3f-11f0-82bf-3b1848bfb7af", required = true)
            @PathVariable UUID merchantUid, Authentication authentication) {

        String email = authentication.getName();

        OrderInfo response = orderService.getOrderByUuid(merchantUid, email);

        return Response.success(response);
    }

    @Operation(summary = "나의 주문 내역 검색", description = "로그인한 사용자의 주문 내역을 페이지로 검색합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 내역 검색 결과를 반환합니다."),
            @ApiResponse(responseCode = "401", description = "사용자가 인증되지 않았습니다.")
    })
    @GetMapping("/search")
    public Response<Page<OrderInfo>> searchOrder(
            @Parameter(description = "주문 검색 조건") OrderSearchCond orderSearchCond,
            Authentication authentication,
            @ParameterObject @PageableDefault(size = 5) Pageable pageable
    ) {

        String email = authentication.getName();

        Page<OrderInfo> response = orderService.getMyOrders(orderSearchCond, email, pageable);

        return Response.success(response);
    }

    @Operation(summary = "단건 주문", description = "사용자가 단일 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문이 성공적으로 생성되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping
    public Response<OrderInfo> order(@Valid @RequestBody OrderInfoRequest request, Authentication authentication) {

        String email = authentication.getName();

        OrderInfo response = orderService.placeSingleOrder(request, email);

        return Response.success(response);
    }

    @Operation(summary = "해당 주문의 배송지 변경", description = "주문의 배송지 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송지 변경 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PutMapping("/{merchantUid}")
    public Response<MessageResponse> changeDeliveryInfo(
            @Parameter(description = "주문 고유 식별자", example = "9115f8f7-2b3f-11f0-82bf-3b1848bfb7af", required = true)
            @PathVariable UUID merchantUid,
            @RequestBody DeliveryUpdateRequest request
    ) {
        return Response.success(orderService.updateDeliveryAddress(merchantUid, request));
    }

    @Operation(summary = "장바구니 내 품목 구매", description = "장바구니에 담긴 품목들을 구매합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 내 품목 구매 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/cart")
    public Response<List<OrderInfo>> order(@Valid @RequestBody CartOrderRequest request, Authentication authentication) {

        String email = authentication.getName();

        List<OrderInfo> response = orderService.placeCartOrder(request, email);

        return Response.success(response);
    }

    @Operation(summary = "주문 취소", description = "특정 주문을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @DeleteMapping("/{merchantUid}")
    public Response<MessageResponse> cancel(
            @Parameter(description = "주문 고유 식별자", example = "9115f8f7-2b3f-11f0-82bf-3b1848bfb7af", required = true)
            @PathVariable UUID merchantUid,
            @RequestBody CancelItemRequest request
    ) throws IamportResponseException, IOException {
        return Response.success(orderService.cancelOrder(merchantUid, request));
    }

    @Operation(summary = "사전 검증", description = "주문 결제를 위한 사전 검증을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사전 검증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/preparation")
    public Response<PreparationResponse> prepareValid(@Valid @RequestBody PreparationRequest preparationRequest) throws IamportResponseException, IOException {
        return Response.success(orderService.validatePrePayment(preparationRequest));
    }

    @Operation(summary = "사후 검증", description = "결제 후 검증을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사후 검증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/verification")
    public Response<MessageResponse> postVerification(@Valid @RequestBody PostVerificationRequest postVerificationRequest) throws IamportResponseException, IOException {
        log.info("imp_uid:{}", postVerificationRequest.getImpUid());
        return Response.success(orderService.verifyPostPayment(postVerificationRequest));
    }
}
