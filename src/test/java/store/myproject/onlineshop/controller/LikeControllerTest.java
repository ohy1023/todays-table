package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.ResultCode;
import store.myproject.onlineshop.service.RecipeService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static store.myproject.onlineshop.exception.ErrorCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.SUCCESS;

@WebMvcTest(LikeController.class)
@WithMockUser
class LikeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    RecipeService recipeService;

    @Test
    @DisplayName("좋아요 성공")
    void push_like_success() throws Exception {
        // given
        Long recipeId = 1L;
        MessageResponse response = new MessageResponse("좋아요 완료");

        given(recipeService.toggleLike(any(Long.class), any(String.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/recipes/{recipeId}/likes", recipeId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 실패 - 회원 없음")
    void push_like_fail_member_not_found() throws Exception {
        // given
        Long recipeId = 1L;

        given(recipeService.toggleLike(any(Long.class), any(String.class)))
                .willThrow(new AppException(CUSTOMER_NOT_FOUND, CUSTOMER_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/recipes/{recipeId}/likes", recipeId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(CUSTOMER_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("좋아요 실패 - 레시피 없음")
    void push_like_fail_recipe_not_found() throws Exception {
        // given
        Long recipeId = 1L;

        given(recipeService.toggleLike(any(Long.class), any(String.class)))
                .willThrow(new AppException(RECIPE_NOT_FOUND, RECIPE_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(post("/api/v1/recipes/{recipeId}/likes", recipeId)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(RECIPE_NOT_FOUND.getMessage()))
                .andDo(print());
    }

}
