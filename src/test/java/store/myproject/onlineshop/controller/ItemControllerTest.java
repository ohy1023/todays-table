package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.custom.WithMockCustomUser;
import store.myproject.onlineshop.domain.brand.Brand;
import store.myproject.onlineshop.domain.brand.dto.BrandCreateRequest;
import store.myproject.onlineshop.domain.brand.dto.BrandCreateResponse;
import store.myproject.onlineshop.domain.customer.CustomerRole;
import store.myproject.onlineshop.domain.item.dto.ItemCreateRequest;
import store.myproject.onlineshop.domain.item.dto.ItemDto;
import store.myproject.onlineshop.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @MockBean
    ItemService itemService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("브랜드 등록 성공")
    @WithMockCustomUser(role = CustomerRole.ROLE_ADMIN)
    public void create_brand_success() throws Exception {

        // given
        ItemCreateRequest request = ItemCreateRequest.builder()
                .itemName("testItem")
                .price(21000L)
                .stock(1000L)
                .brandName("testBrand")
                .itemPhotoUrl("test")
                .build();

        String valueAsString = objectMapper.writeValueAsString(request);

        final String fileName = "testImage1"; //파일명
        final String contentType = "png"; //파일타입
        MockMultipartFile multipartFile = setMockMultipartFile(fileName, contentType);

        Brand findBrand = Brand.builder()
                .id(1L)
                .name("testBrand")
                .originImagePath("s3/brand/url")
                .build();

        findBrand.setCreatedDate(LocalDateTime.now());
        findBrand.setLastModifiedDate(LocalDateTime.now());

        ItemDto response = ItemDto.builder()
                .itemName("testItem")
                .stock(1000L)
                .price(21000L)
                .itemPhotoUrl("s3/item/url")
                .brand(findBrand)
                .build();


        given(itemService.saveItem(any(ItemCreateRequest.class), any(MockMultipartFile.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/v1/items")
                        .file(new MockMultipartFile("request", "", "application/json", valueAsString.getBytes(StandardCharsets.UTF_8)))
                        .file(multipartFile)
                        .contentType(MULTIPART_FORM_DATA)
                        .accept(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                .andExpect(jsonPath("$.result.itemName").value(response.getItemName()))
                .andExpect(jsonPath("$.result.price").value(response.getPrice()))
                .andExpect(jsonPath("$.result.stock").value(response.getStock()))
                .andExpect(jsonPath("$.result.itemPhotoUrl").value(response.getItemPhotoUrl()))
                .andExpect(jsonPath("$.result.brand.createdDate").exists())
                .andExpect(jsonPath("$.result.brand.deletedDate").doesNotExist())
                .andExpect(jsonPath("$.result.brand.lastModifiedDate").exists())
                .andExpect(jsonPath("$.result.brand.id").value(response.getBrand().getId()))
                .andExpect(jsonPath("$.result.brand.name").value(response.getBrand().getName()))
                .andExpect(jsonPath("$.result.brand.originImagePath").value(response.getBrand().getOriginImagePath()))
                .andDo(print());
    }

    private MockMultipartFile setMockMultipartFile(String fileName, String contentType) {
        return new MockMultipartFile("multipartFile", fileName + "." + contentType, contentType, "<<data>>".getBytes());
    }

}