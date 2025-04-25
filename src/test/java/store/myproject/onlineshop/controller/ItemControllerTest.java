package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.web.multipart.MultipartFile;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;

import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import store.myproject.onlineshop.domain.item.dto.ItemSearchCond;
import store.myproject.onlineshop.domain.item.dto.ItemUpdateRequest;
import store.myproject.onlineshop.domain.recipe.dto.SimpleRecipeDto;
import store.myproject.onlineshop.fixture.CommonFixture;
import store.myproject.onlineshop.fixture.ItemFixture;
import store.myproject.onlineshop.fixture.RecipeFixture;
import store.myproject.onlineshop.service.ItemService;
import store.myproject.onlineshop.service.RecipeService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.myproject.onlineshop.fixture.ResultCode.SUCCESS;


@WebMvcTest(ItemController.class)
@WithMockUser
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @MockBean
    RecipeService recipeService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("품목 추가 성공")
    void create_item_success() throws Exception {
        // given
        ItemCreateRequest request = ItemFixture.createRequest();
        String json = objectMapper.writeValueAsString(request);
        List<MockMultipartFile> files = CommonFixture.mockMultipartFileList();
        ItemDto response = ItemFixture.createItemDto();

        given(itemService.createItem(any(), ArgumentMatchers.<List<MultipartFile>>any()))
                .willReturn(response);

        MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart("/api/v1/items")
                .file(new MockMultipartFile(
                        "request", "", "application/json", json.getBytes(StandardCharsets.UTF_8))) // JSON DTO
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .characterEncoding("UTF-8")
                .accept(MediaType.APPLICATION_JSON)
                .with(csrf());

        for (MockMultipartFile file : files) {
            builder.file(file);
        }

        // when & then
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.itemName").value(response.getItemName()))
                .andExpect(jsonPath("$.result.price").value(response.getPrice()))
                .andExpect(jsonPath("$.result.stock").value(response.getStock()))
                .andExpect(jsonPath("$.result.brandName").value(response.getBrandName()))
                .andDo(print());
    }

    @Test
    @DisplayName("품목 단건 조회 성공")
    void find_item_success() throws Exception {
        // given
        ItemDto itemDto = ItemFixture.createItemDto();
        given(itemService.getItemById(1L)).willReturn(itemDto);

        // when & then
        mockMvc.perform(get("/api/v1/items/{itemId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.itemName").value(itemDto.getItemName()))
                .andExpect(jsonPath("$.result.price").value(itemDto.getPrice()))
                .andExpect(jsonPath("$.result.stock").value(itemDto.getStock()))
                .andDo(print());
    }

    @Test
    @DisplayName("품목 검색 성공")
    void searchItem_success() throws Exception {
        // given
        ItemDto item = ItemFixture.createItemDto();
        Page<ItemDto> page = new PageImpl<>(List.of(item));

        given(itemService.searchItem(any(ItemSearchCond.class), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/items")
                        .param("itemName", "샘플")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].itemName").value(item.getItemName()))
                .andExpect(jsonPath("$.result.content[0].price").value(item.getPrice().intValue()))
                .andExpect(jsonPath("$.result.content[0].stock").value(item.getStock()))
                .andDo(print());
    }

    @Test
    @DisplayName("품목 수정 성공")
    void changeItem_success() throws Exception {
        // given
        Long itemId = 1L;
        ItemUpdateRequest request = ItemFixture.updateRequest();
        String json = objectMapper.writeValueAsString(request);
        List<MockMultipartFile> files = CommonFixture.mockMultipartFileList();

        MessageResponse response = new MessageResponse("수정 성공");

        given(itemService.updateItem(eq(itemId), any(ItemUpdateRequest.class), ArgumentMatchers.<List<MultipartFile>>any()))
                .willReturn(response);

        MockMultipartHttpServletRequestBuilder builder = (MockMultipartHttpServletRequestBuilder) multipart(HttpMethod.PUT, "/api/v1/items/{itemId}", itemId)
                .file(new MockMultipartFile("request", "", "application/json", json.getBytes(StandardCharsets.UTF_8)))
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
                .with(csrf());

        for (MockMultipartFile file : files) {
            builder.file(file);
        }

        // when & then
        mockMvc.perform(builder)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("품목 삭제 성공")
    void remove_item_success() throws Exception {
        // given
        MessageResponse response = new MessageResponse("삭제 완료");
        given(itemService.deleteItem(1L)).willReturn(response);

        // when & then
        mockMvc.perform(delete("/api/v1/items/{itemId}", 1L)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());
    }


    @Test
    @DisplayName("해당 아이템 사용하는 레시피 목록 조회 성공")
    void findRecipesByItem_success() throws Exception {
        // given
        SimpleRecipeDto recipe = RecipeFixture.createSimpleRecipeDto(); // 테스트용 fixture
        Page<SimpleRecipeDto> page = new PageImpl<>(List.of(recipe));

        given(recipeService.getRecipesByItem(eq(1L), any(Pageable.class)))
                .willReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/items/{itemId}/recipes", 1L)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.content[0].recipeId").value(recipe.getRecipeId()))
                .andExpect(jsonPath("$.result.content[0].title").value(recipe.getTitle()))
                .andExpect(jsonPath("$.result.content[0].recipeDescription").value(recipe.getRecipeDescription()))
                .andDo(print());
    }
}