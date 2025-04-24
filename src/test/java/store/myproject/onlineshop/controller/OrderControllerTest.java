package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.order.dto.OrderInfo;
import store.myproject.onlineshop.domain.order.dto.OrderInfoRequest;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.OrderFixture;
import store.myproject.onlineshop.service.OrderService;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;

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
            OrderInfo orderInfo = OrderFixture.createOrderInfo();
            given(orderService.getOrderById(anyLong(), anyString())).willReturn(orderInfo);

            mockMvc.perform(get("/api/v1/orders/1"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 단건 조회 실패 - 고객 없음")
        void findOneOrder_fail_customer_not_found() throws Exception {
            given(orderService.getOrderById(anyLong(), anyString()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/orders/1"))
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
            List<OrderInfo> results = List.of(OrderFixture.createOrderInfo());
            given(orderService.getMyOrders(any(), anyString(), any())).willReturn(new PageImpl<>(results, PageRequest.of(0, 10), 1));

            mockMvc.perform(get("/api/v1/orders/search"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 내역 검색 실패 - 고객 없음")
        void searchOrders_fail_customer_not_found() throws Exception {
            given(orderService.getMyOrders(any(), anyString(), any()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(get("/api/v1/orders/search"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("주문 취소")
    class CancelOrder {

        @Test
        @DisplayName("주문 취소 성공")
        void cancel_success() throws Exception {
            given(orderService.cancelOrder(anyLong())).willReturn(new MessageResponse("주문이 취소되었습니다."));

            mockMvc.perform(delete("/api/v1/orders/1")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("주문이 취소되었습니다."))
                    .andDo(print());
        }

        @Test
        @DisplayName("주문 취소 실패 - 주문 없음")
        void cancel_fail_order_not_found() throws Exception {
            given(orderService.cancelOrder(anyLong()))
                    .willThrow(new AppException(ORDER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/orders/999")
                            .with(csrf()))
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
            MessageResponse response = new MessageResponse("배송지 수정 완료");
            given(orderService.updateDeliveryAddress(anyLong(), any())).willReturn(response);

            mockMvc.perform(put("/api/v1/orders/1")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("배송지 수정 완료"))
                    .andDo(print());
        }

        @Test
        @DisplayName("배송지 변경 실패 - 주문 없음")
        void updateDelivery_fail_order_not_found() throws Exception {
            given(orderService.updateDeliveryAddress(anyLong(), any()))
                    .willThrow(new AppException(ORDER_NOT_FOUND));

            mockMvc.perform(put("/api/v1/orders/999")
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
            List<OrderInfo> response = List.of(OrderFixture.createOrderInfo());
            given(orderService.placeCartOrder(any(), anyString())).willReturn(response);

            mockMvc.perform(post("/api/v1/orders/cart")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("장바구니 주문 실패 - 고객 없음")
        void orderByCart_fail_customer_not_found() throws Exception {
            given(orderService.placeCartOrder(any(), anyString()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/orders/cart")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content("{}"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }
}
