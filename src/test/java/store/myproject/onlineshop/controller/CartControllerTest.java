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
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.dto.cart.CartAddRequest;
import store.myproject.onlineshop.dto.cart.CartItemResponse;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.CartFixture;
import store.myproject.onlineshop.service.CartService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@WithMockUser
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    CartService cartService;

    @Nested
    @DisplayName("장바구니 품목 추가")
    class AddItemToCart {

        @Test
        @DisplayName("성공")
        void add_success() throws Exception {
            UUID itemUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            CartAddRequest request = CartFixture.createAddRequest(itemUuid, 10L);
            MessageResponse response = MessageResponse.of("장바구니에 품목 추가 완료");

            given(cartService.addItemToCart(any(CartAddRequest.class), anyString()))
                    .willReturn(response);

            mockMvc.perform(post("/api/v1/carts")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 고객 없음")
        void fail_customer_not_found() throws Exception {
            UUID itemUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            CartAddRequest request = CartFixture.createAddRequest(itemUuid, 10L);

            given(cartService.addItemToCart(any(), anyString()))
                    .willThrow(new AppException(ErrorCode.CUSTOMER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/carts")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 재고 부족")
        void fail_stock_not_enough() throws Exception {
            UUID itemUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

            CartAddRequest request = CartFixture.createAddRequest(itemUuid, 10L);
            given(cartService.addItemToCart(any(), anyString()))
                    .willThrow(new AppException(ErrorCode.NOT_ENOUGH_STOCK));

            mockMvc.perform(post("/api/v1/carts")
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorCode").value(ErrorCode.NOT_ENOUGH_STOCK.name()))
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("장바구니 품목 조회 성공")
    void getCartItems_success() throws Exception {
        List<CartItemResponse> items = CartFixture.createCartItemResponses();
        given(cartService.getCartItems(anyString(), any()))
                .willReturn(new PageImpl<>(items, PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/api/v1/carts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].itemName").value(items.get(0).getItemName()))
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 품목 삭제 성공")
    void deleteItem_success() throws Exception {
        UUID itemUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(cartService.deleteItemFromCart(any(UUID.class), anyString()))
                .willReturn(MessageResponse.of("삭제 완료"));

        mockMvc.perform(delete("/api/v1/carts/{itemUuid}", itemUuid)
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("장바구니 전체 비우기 성공")
    void clearCart_success() throws Exception {
        given(cartService.clearCart(anyString()))
                .willReturn(MessageResponse.of("비우기 완료"));

        mockMvc.perform(delete("/api/v1/carts")
                        .with(csrf()))
                .andExpect(status().isNoContent())
                .andDo(print());
    }
}
