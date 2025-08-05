package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.recipe.dto.*;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.RecipeFixture;
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
import static store.myproject.onlineshop.fixture.ResultCode.ERROR;
import static store.myproject.onlineshop.fixture.ResultCode.SUCCESS;

@WebMvcTest(RecipeController.class)
@WithMockUser
class RecipeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    RecipeService recipeService;

    @Nested
    @DisplayName("레시피 단건 조회")
    class GetRecipe {

        @Test
        @DisplayName("성공")
        void get_recipe_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            RecipeDto response = RecipeFixture.createRecipeDto(recipeUuid);

            given(recipeService.getRecipeDetail(any())).willReturn(response);

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}", recipeUuid))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void get_recipe_fail_not_found() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            given(recipeService.getRecipeDetail(any())).willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(get("/api/v1/recipes/{recipeUuid}", recipeUuid))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.resultCode").value(ERROR))
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("레시피 전체 조회")
    class GetAllRecipes {

        @Test
        @DisplayName("성공")
        void get_all_recipes_success() throws Exception {
            List<SimpleRecipeDto> response = List.of(RecipeFixture.createSimpleRecipeDto());
            given(recipeService.getRecipes(any())).willReturn(new RecipeCursorResponse(response, null, null, null));

            mockMvc.perform(get("/api/v1/recipes"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("레시피 작성")
    class CreateRecipe {
        @Test
        @DisplayName("성공")
        void createRecipe_success() throws Exception {
            RecipeCreateRequest recipeCreateRequest = RecipeFixture.createRecipeCreateRequest();

            String request = objectMapper.writeValueAsString(recipeCreateRequest);

            given(recipeService.createRecipe(any(), anyString()))
                    .willReturn(new MessageResponse("작성 성공"));

            mockMvc.perform(post("/api/v1/recipes")
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andExpect(jsonPath("$.result.message").value("작성 성공"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("레시피 수정")
    class UpdateRecipe {
        @Test
        @DisplayName("성공")
        void update_recipe_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            RecipeUpdateRequest recipeUpdateRequest = RecipeFixture.createRecipeUpdateRequest();

            String request = objectMapper.writeValueAsString(recipeUpdateRequest);

            given(recipeService.updateRecipe(any(), any(), anyString()))
                    .willReturn(new MessageResponse("수정 성공"));

            mockMvc.perform(put("/api/v1/recipes/{recipeUuid}", recipeUuid)
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andExpect(jsonPath("$.result.message").value("수정 성공"))
                    .andDo(print());
        }


        @Test
        @DisplayName("실패 - 권한 없음")
        void update_recipe_fail_forbidden() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            RecipeUpdateRequest recipeUpdateRequest = RecipeFixture.createRecipeUpdateRequest();

            String request = objectMapper.writeValueAsString(recipeUpdateRequest);

            given(recipeService.updateRecipe(any(), any(), anyString()))
                    .willThrow(new AppException(FORBIDDEN_ACCESS));

            mockMvc.perform(put("/api/v1/recipes/{recipeUuid}", recipeUuid)
                            .contentType(APPLICATION_JSON)
                            .content(request)
                            .with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.resultCode").value(ERROR))
                    .andExpect(jsonPath("$.result.errorCode").value(FORBIDDEN_ACCESS.name()))
                    .andDo(print());
        }

    }

    @Nested
    @DisplayName("레시피 삭제")
    class DeleteRecipe {

        @Test
        @DisplayName("성공")
        void delete_recipe_success() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            given(recipeService.deleteRecipe(any(), anyString()))
                    .willReturn(new MessageResponse("삭제 성공"));

            mockMvc.perform(delete("/api/v1/recipes/{recipeUuid}", recipeUuid).with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andExpect(jsonPath("$.result.message").value("삭제 성공"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void delete_recipe_fail_forbidden() throws Exception {
            UUID recipeUuid = UUID.randomUUID();

            given(recipeService.deleteRecipe(any(), anyString()))
                    .willThrow(new AppException(FORBIDDEN_ACCESS));

            mockMvc.perform(delete("/api/v1/recipes/{recipeUuid}",recipeUuid).with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.resultCode").value(ERROR))
                    .andExpect(jsonPath("$.result.errorCode").value(FORBIDDEN_ACCESS.name()))
                    .andDo(print());
        }
    }

    @Test
    @DisplayName("이미지 업로드 성공")
    void upload_image_success() throws Exception {
        // given
        MockMultipartFile file = setMockMultipartFile("recipeStepImage");

        given(recipeService.uploadImage(any()))
                .willReturn(new MessageResponse("이미지 업로드 완료"));

        // when & then
        mockMvc.perform(multipart("/api/v1/recipes/image")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value("이미지 업로드 완료"));
    }

    @Test
    @DisplayName("이미지 업로드 실패")
    void upload_image_fail_invalid_name() throws Exception {
        // given
        MockMultipartFile file = setMockMultipartFile("file");

        // when & then
        mockMvc.perform(multipart("/api/v1/recipes/image")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    private MockMultipartFile setMockMultipartFile(String name) {
        return new MockMultipartFile(name, "testImage1" + "." + "png", "png", "<<data>>".getBytes());
    }
}
