package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.dto.review.ChildReviewResponse;
import store.myproject.onlineshop.dto.review.ReviewResponse;
import store.myproject.onlineshop.dto.review.ReviewUpdateRequest;
import store.myproject.onlineshop.dto.review.ReviewWriteRequest;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.ReviewFixture;
import store.myproject.onlineshop.service.RecipeService;

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

@WebMvcTest(ReviewController.class)
@WithMockUser
class ReviewControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecipeService recipeService;

    @Nested
    @DisplayName("댓글 조회")
    class GetParentReviews {

        @Test
        @DisplayName("성공")
        void get_parent_reviews_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            List<ReviewResponse> responses = List.of(
                    ReviewResponse.builder()
                            .uuid(UUID.randomUUID())
                            .writer("작성자")
                            .content("내용")
                            .childReviews(List.of())
                            .hasMoreChildReviews(false)
                            .build()
            );

            given(recipeService.getRecipeReviews(any(), any())).willReturn(new PageImpl<>(responses));

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}/reviews", recipeUuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.content[0].writer").value("작성자"))
                    .andExpect(jsonPath("$.result.content[0].content").value("내용"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void get_parent_reviews_recipe_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            given(recipeService.getRecipeReviews(any(), any())).willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}/reviews", recipeUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("대댓글 더보기")
    class GetChildReviews {

        @Test
        @DisplayName("성공")
        void get_child_reviews_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            List<ChildReviewResponse> childResponses = List.of(
                    ChildReviewResponse.builder().uuid(UUID.randomUUID()).writer("답글작성자").content("답글 내용").build()
            );

            given(recipeService.getChildReviews(any(), any(), any())).willReturn(new PageImpl<>(childResponses));

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}/replies", recipeUuid, reviewUuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.content[0].writer").value("답글작성자"))
                    .andExpect(jsonPath("$.result.content[0].content").value("답글 내용"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void get_child_reviews_recipe_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            given(recipeService.getChildReviews(any(), any(), any())).willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}/replies", recipeUuid, reviewUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 불일치")
        void get_child_reviews_customer_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            given(recipeService.getChildReviews(any(), any(), any())).willThrow(new AppException(INVALID_REVIEW));

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}/replies", recipeUuid, reviewUuid))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.result.errorCode").value(INVALID_REVIEW.name()))
                    .andDo(print());
        }

    }

    @Nested
    @DisplayName("댓글 작성")
    class WriteReview {

        @Test
        @DisplayName("성공")
        void write_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            ReviewWriteRequest request = ReviewFixture.createReviewWriteRequest();
            MessageResponse response = new MessageResponse(UUID.randomUUID(), "작성 완료");

            given(recipeService.createReview(anyString(), any(), any())).willReturn(response);

            mockMvc.perform(post("/api/v1/recipes/{recipeUuid}/reviews", recipeUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(header().exists("Location"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void write_recipe_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            ReviewWriteRequest request = ReviewFixture.createReviewWriteRequest();

            given(recipeService.createReview(anyString(), any(), any()))
                    .willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(post("/api/v1/recipes/{recipeUuid}/reviews", recipeUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 고객 없음")
        void write_customer_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            ReviewWriteRequest request = ReviewFixture.createReviewWriteRequest();

            given(recipeService.createReview(anyString(), any(), any()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(post("/api/v1/recipes/{recipeUuid}/reviews", recipeUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        @WithMockUser(roles = "ADMIN")
        void write_forbidden() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            ReviewWriteRequest request = ReviewFixture.createReviewWriteRequest();

            given(recipeService.createReview(anyString(), any(), any()))
                    .willThrow(new AppException(FORBIDDEN_ACCESS));

            mockMvc.perform(post("/api/v1/recipes/{recipeUuid}/reviews", recipeUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.result.errorCode").value(FORBIDDEN_ACCESS.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 수정")
    class UpdateReview {

        @Test
        @DisplayName("성공")
        void update_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            ReviewUpdateRequest request = ReviewFixture.createReviewUpdateRequest();
            MessageResponse response = MessageResponse.of("수정 완료");

            given(recipeService.updateReview(anyString(), any(), any(), any())).willReturn(response);

            mockMvc.perform(put("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("수정 완료"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void update_recipe_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            ReviewUpdateRequest request = ReviewFixture.createReviewUpdateRequest();

            given(recipeService.updateReview(anyString(), any(), any(), any()))
                    .willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(put("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        @WithMockUser(roles = "ADMIN")
        void update_forbidden() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            ReviewUpdateRequest request = ReviewFixture.createReviewUpdateRequest();

            given(recipeService.updateReview(anyString(), any(), any(), any()))
                    .willThrow(new AppException(FORBIDDEN_ACCESS));

            mockMvc.perform(put("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.result.errorCode").value(FORBIDDEN_ACCESS.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 고객 없음")
        void update_customer_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();

            ReviewUpdateRequest request = ReviewFixture.createReviewUpdateRequest();

            given(recipeService.updateReview(anyString(), any(), any(), any()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(put("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf())
                            .contentType(APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("댓글 삭제")
    class DeleteReview {

        @Test
        @DisplayName("성공")
        void delete_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();
            MessageResponse response = MessageResponse.of("삭제 완료");

            given(recipeService.deleteReview(anyString(), any(), any())).willReturn(response);

            mockMvc.perform(delete("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf()))
                    .andExpect(status().isNoContent())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void delete_recipe_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();
            given(recipeService.deleteReview(anyString(), any(), any()))
                    .willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        @WithMockUser(roles = "ADMIN")
        void delete_forbidden() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();
            given(recipeService.deleteReview(anyString(), any(), any()))
                    .willThrow(new AppException(FORBIDDEN_ACCESS));

            mockMvc.perform(delete("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.result.errorCode").value(FORBIDDEN_ACCESS.name()))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 고객 없음")
        void delete_customer_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();
            UUID reviewUuid = UUID.randomUUID();
            given(recipeService.deleteReview(anyString(), any(), any()))
                    .willThrow(new AppException(CUSTOMER_NOT_FOUND));

            mockMvc.perform(delete("/api/v1/recipes/{recipeUuid}/reviews/{reviewUuid}", recipeUuid, reviewUuid)
                            .with(csrf()))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(CUSTOMER_NOT_FOUND.name()))
                    .andDo(print());
        }
    }
}
