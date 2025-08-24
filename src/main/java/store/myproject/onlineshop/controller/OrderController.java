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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.dto.common.Response;
import store.myproject.onlineshop.dto.cart.CartOrderRequest;
import store.myproject.onlineshop.dto.delivery.DeliveryUpdateRequest;
import store.myproject.onlineshop.dto.order.*;
import store.myproject.onlineshop.service.OrderService;

import java.io.IOException;
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
    public ResponseEntity<Response<OrderInfo>> findOneOrder(
            @Parameter(description = "주문을 조회할 고유 식별자", example = "91d7bee8-5880-11f0-8b90-718284fea868", required = true)
            @PathVariable UUID merchantUid,
            Authentication authentication) {

        String email = authentication.getName();
        OrderInfo response = orderService.getOrderByUuid(merchantUid, email);
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(
            summary = "나의 주문 내역",
            description = """
                        로그인한 사용자의 주문 내역을 페이지 단위로 조회합니다.
                    
                        우선순위:
                        1. brandName이 있으면 brandName으로 검색합니다.
                        2. brandName이 없고 itemName이 있으면 itemName으로 검색합니다.
                        3. 둘 다 없으면 전체 주문 내역을 조회합니다.
                    """
    )
    @GetMapping
    public ResponseEntity<Response<MyOrderSliceResponse>> myOrder(@ModelAttribute OrderSearchCond orderSearchCond, Authentication authentication) {
        MyOrderSliceResponse response = orderService.getMyOrders(orderSearchCond, authentication.getName());
        return ResponseEntity.ok(Response.success(response));
    }

    @Operation(summary = "단건 주문", description = "사용자가 단일 주문을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문이 성공적으로 생성되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping
    public ResponseEntity<Response<MessageResponse>> order(@Valid @RequestBody OrderInfoRequest request, Authentication authentication) {
        String email = authentication.getName();
        MessageResponse msg = orderService.placeSingleOrder(request, email);
        return ResponseEntity.ok(Response.success(msg));
    }

    @Operation(summary = "장바구니 내 품목 구매", description = "장바구니에 담긴 품목들을 구매합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "장바구니 내 품목 구매 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/cart")
    public ResponseEntity<Response<MessageResponse>> order(@Valid @RequestBody CartOrderRequest request, Authentication authentication) {
        String email = authentication.getName();
        MessageResponse msg = orderService.placeCartOrder(request, email);
        return ResponseEntity.ok(Response.success(msg));
    }

    @Operation(summary = "주문 정보 롤백 (orderstatus 변경 및 재고 정합성)", description = "아임포트 결제 창 닫기 등의 이유로 결제 실패시 주문 롤백")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문이 성공적으로 롤백되었습니다."),
            @ApiResponse(responseCode = "400", description = "잘못된 요청입니다.")
    })
    @PostMapping("/rollback")
    public ResponseEntity<Response<MessageResponse>> order(@Valid @RequestBody OrderRollbackRequest request, Authentication authentication) {
        MessageResponse msg = orderService.rollbackOrder(authentication.getName(), request);
        return ResponseEntity.ok(Response.success(msg));
    }

    @Operation(summary = "주문 취소", description = "특정 주문을 취소합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "주문 취소 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @DeleteMapping("/{merchantUid}")
    public ResponseEntity<Response<MessageResponse>> cancel(
            @Parameter(description = "주문 고유 식별자", example = "9115f8f7-2b3f-11f0-82bf-3b1848bfb7af", required = true)
            @PathVariable UUID merchantUid,
            @RequestBody CancelItemRequest request
    ) throws IamportResponseException, IOException {
        MessageResponse msg = orderService.cancelOrder(merchantUid, request);
        return ResponseEntity.ok(Response.success(msg));
    }

    @Operation(summary = "사전 검증", description = "주문 결제를 위한 사전 검증을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사전 검증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/preparation")
    public ResponseEntity<Response<PreparationResponse>> prepareValid(@Valid @RequestBody PreparationRequest preparationRequest) throws IamportResponseException, IOException {
        PreparationResponse resp = orderService.validatePrePayment(preparationRequest);
        return ResponseEntity.ok(Response.success(resp));
    }

    @Operation(summary = "사후 검증", description = "결제 후 검증을 수행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사후 검증 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/verification")
    public ResponseEntity<Response<MessageResponse>> postVerification(@Valid @RequestBody PostVerificationRequest postVerificationRequest) throws IamportResponseException, IOException {
        log.info("imp_uid:{}", postVerificationRequest.getImpUid());
        MessageResponse msg = orderService.verifyPostPayment(postVerificationRequest);
        return ResponseEntity.ok(Response.success(msg));
    }

    @Operation(summary = "해당 주문의 배송지 변경", description = "주문의 배송지 정보를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "배송지 변경 성공"),
            @ApiResponse(responseCode = "404", description = "주문을 찾을 수 없음"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PutMapping("/{merchantUid}")
    public ResponseEntity<Response<MessageResponse>> changeDeliveryInfo(
            @Parameter(description = "주문 고유 식별자", example = "9115f8f7-2b3f-11f0-82bf-3b1848bfb7af", required = true)
            @PathVariable UUID merchantUid,
            @RequestBody DeliveryUpdateRequest request
    ) {
        MessageResponse msg = orderService.updateDeliveryAddress(merchantUid, request);
        return ResponseEntity.ok(Response.success(msg));
    }
}
