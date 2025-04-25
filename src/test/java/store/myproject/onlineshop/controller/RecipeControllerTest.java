package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.recipe.dto.RecipeCreateRequest;
import store.myproject.onlineshop.domain.recipe.dto.RecipeDto;
import store.myproject.onlineshop.domain.recipe.dto.RecipeUpdateRequest;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.RecipeFixture;
import store.myproject.onlineshop.service.RecipeService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;
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
        void getRecipe_success() throws Exception {
            RecipeDto response = RecipeFixture.createRecipeDto();

            given(recipeService.getRecipe(anyLong())).willReturn(response);

            mockMvc.perform(get("/api/v1/recipes/1"))
                    .andExpect(status().isOk())
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 레시피 없음")
        void getRecipe_not_found() throws Exception {
            given(recipeService.getRecipe(anyLong())).willThrow(new AppException(RECIPE_NOT_FOUND));

            mockMvc.perform(get("/api/v1/recipes/1"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.result.errorCode").value(RECIPE_NOT_FOUND.name()))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("레시피 전체 조회")
    class GetAllRecipes {

        @Test
        @DisplayName("성공")
        void getAllRecipes_success() throws Exception {
            List<SimpleRecipeDto> response = List.of(RecipeFixture.createSimpleRecipeDto());
            given(recipeService.getAllRecipe(any())).willReturn(new PageImpl<>(response));

            mockMvc.perform(get("/api/v1/recipes"))
                    .andExpect(status().isOk())
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

            MockMultipartFile multipartFile = setMockMultipartFile();

            given(recipeService.createRecipe(any(), any(), anyString()))
                    .willReturn(new MessageResponse("작성 성공"));

            mockMvc.perform(multipart(HttpMethod.POST, "/api/v1/recipes")
                            .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                            .file(multipartFile)
                            .contentType(MULTIPART_FORM_DATA)
                            .accept(APPLICATION_JSON)
                            .characterEncoding("UTF-8")
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
        void updateRecipe_success() throws Exception {
            RecipeUpdateRequest recipeUpdateRequest = RecipeFixture.createRecipeUpdateRequest();

            String request = objectMapper.writeValueAsString(recipeUpdateRequest);

            MockMultipartFile multipartFile = setMockMultipartFile();

            given(recipeService.updateRecipe(anyLong(), any(), any(), anyString()))
                    .willReturn(new MessageResponse("수정 성공"));

            mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/recipes/{recipeId}",1L)
                            .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                            .file(multipartFile)
                            .contentType(MULTIPART_FORM_DATA)
                            .accept(APPLICATION_JSON)
                            .characterEncoding("UTF-8")
                            .with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                    .andExpect(jsonPath("$.result.message").value("수정 성공"))
                    .andDo(print());
        }
    }

    @Nested
    @DisplayName("레시피 삭제")
    class DeleteRecipe {

        @Test
        @DisplayName("성공")
        void deleteRecipe_success() throws Exception {
            given(recipeService.deleteRecipe(anyLong(), anyString()))
                    .willReturn(new MessageResponse("삭제 성공"));

            mockMvc.perform(delete("/api/v1/recipes/1").with(csrf()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.result.message").value("삭제 성공"))
                    .andDo(print());
        }

        @Test
        @DisplayName("실패 - 권한 없음")
        void deleteRecipe_forbidden() throws Exception {
            given(recipeService.deleteRecipe(anyLong(), anyString()))
                    .willThrow(new AppException(FORBIDDEN_ACCESS));

            mockMvc.perform(delete("/api/v1/recipes/1").with(csrf()))
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("$.result.errorCode").value(FORBIDDEN_ACCESS.name()))
                    .andDo(print());
        }
    }


    private MockMultipartFile setMockMultipartFile() {
        return new MockMultipartFile("multipartFile", "testImage1" + "." + "png", "png", "<<data>>".getBytes());
    }
}
