package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.cart.dto.CartOrderRequest;
import store.myproject.onlineshop.domain.order.dto.*;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.OrderFixture;
import store.myproject.onlineshop.service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.*;

@WebMvcTest(OrderController.class)
@WithMockUser
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    OrderService orderService;

    @Nested
    @DisplayName("단건 주문")
    class OrderByOne {

        @Test
        @DisplayName("단건 주문 성공")
        void orderByOne_success() throws Exception {
            OrderInfoRequest request = OrderFixture.createOrderInfoRequest();
            OrderInfo response = OrderFixture.createOrderInfo();

            given(orderService.placeSingleOrder(any(), anyString())).willReturn(response);

            mockMvc.perform(post("/api/v1/orders")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("단건 주문 실패 - 고객 없음")
        void orderByOne_fail_customer_not_found() throws Exception {
            OrderInfoRequest request = OrderFixture.createOrderInfoRequest();

            given(orderService.placeSingleOrder(any(), anyString()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/orders")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("단건 주문 조회")
    class FindOneOrder {

        @Test
        @DisplayName("주문 단건 조회 성공")
        void findOneOrder_success() throws Exception {
            UUID orderUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            OrderInfo orderInfo = OrderFixture.createOrderInfo();
            given(orderService.getOrderByUuid(any(UUID.class), anyString())).willReturn(orderInfo);

            mockMvc.perform(get("/api/v1/orders/{orderUuid}", orderUuid))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 단건 조회 실패 - 고객 없음")
        void findOneOrder_fail_customer_not_found() throws Exception {
            UUID orderUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            given(orderService.getOrderByUuid(any(UUID.class), anyString()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/orders/{orderUuid}", orderUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("주문 내역 검색")
    class SearchMyOrders {

        @Test
        @DisplayName("주문 내역 검색 성공")
        void searchOrders_success() throws Exception {

            MyOrderSliceResponse results = MyOrderSliceResponse.builder().nextCursor(null).content(new ArrayList<>()).build();
            given(orderService.getMyOrders(any(), anyString())).willReturn(results);

            mockMvc.perform(get("/api/v1/orders"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 내역 검색 실패 - 고객 없음")
        void searchOrders_fail_customer_not_found() throws Exception {
            given(orderService.getMyOrders(any(), anyString()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/orders"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("주문 롤백")
    class RollbackOrder {

        @Test
        @DisplayName("주문 롤백 성공")
        void rollback_success() throws Exception {
            OrderRollbackRequest request = new OrderRollbackRequest(UUID.randomUUID());
            MessageResponse response = new MessageResponse("주문 롤백 완료");

            given(orderService.rollbackOrder(anyString(), any(OrderRollbackRequest.class)))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/orders/rollback")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("주문 롤백 완료"))
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 롤백 실패 - 주문 없음")
        void rollback_fail_order_not_found() throws Exception {
            OrderRollbackRequest request = new OrderRollbackRequest(UUID.randomUUID());

            given(orderService.rollbackOrder(anyString(), any(OrderRollbackRequest.class)))
                    .willThrow(new AppException(ORDER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/orders/rollback")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(ORDER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }


    @Nested
    @DisplayName("주문 취소")
    class CancelOrder {

        @Test
        @DisplayName("주문 취소 성공")
        void cancel_success() throws Exception {
            UUID orderUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            CancelItemRequest request = OrderFixture.createCancelItemRequest(orderUuid);

            given(orderService.cancelOrder(any(UUID.class), any(CancelItemRequest.class))).willReturn(new MessageResponse("주문이 취소되었습니다."));

            mockMvc.perform(delete("/api/v1/orders/{orderUuid}", orderUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("주문이 취소되었습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 취소 실패 - 주문 없음")
        void cancel_fail_order_not_found() throws Exception {
            UUID orderUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
            CancelItemRequest request = OrderFixture.createCancelItemRequest(orderUuid);

            given(orderService.cancelOrder(any(UUID.class), any(CancelItemRequest.class)))
                    .willThrow(new AppException(ORDER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/orders/{orderUuid}", orderUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request))
                    )
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(ORDER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("배송지 변경")
    class UpdateDeliveryInfo {

        @Test
        @DisplayName("배송지 변경 성공")
        void updateDelivery_success() throws Exception {
            UUID orderUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            MessageResponse response = new MessageResponse(orderUuid, "배송지 수정 완료");
            given(orderService.updateDeliveryAddress(any(UUID.class), any())).willReturn(response);

            mockMvc.perform(put("/api/v1/orders/{orderUuid}", orderUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.uuid").value(orderUuid.toString()))
                    .andExpect(jsonPath("$.result.message").value("배송지 수정 완료"))
                    .andDo(print());
        }

        @Test
        @DisplayName("배송지 변경 실패 - 주문 없음")
        void updateDelivery_fail_order_not_found() throws Exception {
            UUID wrongOrderUuid = UUID.randomUUID();

            given(orderService.updateDeliveryAddress(any(UUID.class), any()))
                    .willThrow(new AppException(ORDER_NOT_FOUND));

            mockMvc.perform(put("/api/v1/orders/{wrongOrderUuid}", wrongOrderUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(ORDER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("장바구니 주문")
    class OrderByCart {

        @Test
        @DisplayName("장바구니 주문 성공")
        void orderByCart_success() throws Exception {
            CartOrderRequest request = OrderFixture.createCartOrderRequest();
            List<OrderInfo> response = List.of(OrderFixture.createOrderInfo());

            given(orderService.placeCartOrder(any(), anyString())).willReturn(response);

            mockMvc.perform(post("/api/v1/orders/cart")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("장바구니 주문 실패 - 고객 없음")
        void orderByCart_fail_customer_not_found() throws Exception {
            CartOrderRequest request = OrderFixture.createCartOrderRequest();

            given(orderService.placeCartOrder(any(), anyString()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/orders/cart")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("사전 결제 검증")
    class PreparePayment {

        @Test
        @DisplayName("사전 검증 성공")
        void prepare_valid_success() throws Exception {
            PreparationRequest request = OrderFixture.createPreparationRequest();
            PreparationResponse response = new PreparationResponse("merchantUid-123");

            given(orderService.validatePrePayment(any())).willReturn(response);

            mockMvc.perform(post("/api/v1/orders/preparation")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andExpect(jsonPath("$.result.merchantUid").value("merchantUid-123"))
                    .andDo(print());
        }

        @Test
        @DisplayName("사전 검증 실패 - 결제 오류")
        void prepare_valid_fail() throws Exception {
            PreparationRequest request = OrderFixture.createPreparationRequest();

            given(orderService.validatePrePayment(any()))
                    .willThrow(new AppException(FAILED_PREPARE_VALID, "검증 실패"));

            mockMvc.perform(post("/api/v1/orders/preparation")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.resultCode").value(ERROR))
                    .andExpect(jsonPath("$.result.errorCode").value(FAILED_PREPARE_VALID.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("사후 결제 검증")
    class PostPayment {

        @Test
        @DisplayName("사후 검증 성공")
        void post_verification_success() throws Exception {
            PostVerificationRequest request = OrderFixture.createPostVerificationRequest();
            MessageResponse response = new MessageResponse("검증 완료");

            given(orderService.verifyPostPayment(any())).willReturn(response);

            mockMvc.perform(post("/api/v1/orders/verification")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andExpect(jsonPath("$.result.message").value("검증 완료"))
                    .andDo(print());
        }

        @Test
        @DisplayName("사후 검증 실패 - 금액 불일치")
        void post_verification_fail_wrong_amount() throws Exception {
            PostVerificationRequest request = OrderFixture.createPostVerificationRequest();

            given(orderService.verifyPostPayment(any()))
                    .willThrow(new AppException(WRONG_PAYMENT_AMOUNT));

            mockMvc.perform(post("/api/v1/orders/verification")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.resultCode").value(ERROR))
                    .andExpect(jsonPath("$.result.errorCode").value(WRONG_PAYMENT_AMOUNT.name()))
                    .andDo(print());
        }
    }
}
