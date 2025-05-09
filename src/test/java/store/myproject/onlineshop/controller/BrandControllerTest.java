package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.brand.dto.*;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.BrandFixture;
import store.myproject.onlineshop.service.BrandService;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.myproject.onlineshop.exception.ErrorCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.*;

@WebMvcTest(BrandController.class)
@AutoConfigureMockMvc
@WithMockUser
class BrandControllerTest {

    @MockBean
    BrandService brandService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Test
    @DisplayName("브랜드 단건 조회 성공")
    public void get_brand_success() throws Exception {

        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        BrandInfo response = BrandFixture.createBrandInfo(brandUuid);

        given(brandService.findBrandInfoById(any(UUID.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/brands/{brandUuid}", brandUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.uuid").value(brandUuid.toString()))
                .andExpect(jsonPath("$.result.name").exists())
                .andDo(print());

    }

    @Test
    @DisplayName("브랜드 단건 조회 실패 - 존재하지 않는 브랜드")
    public void get_brand_fail_not_found() throws Exception {

        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(brandService.findBrandInfoById(any(UUID.class)))
                .willThrow(new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(get("/api/v1/brands/{brandUuid}", brandUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(BRAND_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(BRAND_NOT_FOUND.getMessage()))
                .andDo(print());

    }


    @Test
    @DisplayName("브랜드 등록 성공")
    @WithMockUser(username = "admin", roles = "ADMIN")
    public void create_brand_success() throws Exception {

        // given
        BrandCreateRequest brandCreateRequest = BrandFixture.createRequest();

        String request = objectMapper.writeValueAsString(brandCreateRequest);

        MockMultipartFile multipartFile = setMockMultipartFile();

        MessageResponse response = new MessageResponse("브랜드 등록 성공");

        given(brandService.createBrand(any(BrandCreateRequest.class), any(MockMultipartFile.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(multipart("/api/v1/brands")
                        .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                        .file(multipartFile)
                        .contentType(MULTIPART_FORM_DATA)
                        .accept(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("브랜드 수정 성공")
    public void update_brand_success() throws Exception {

        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        BrandUpdateRequest brandUpdateRequest = BrandFixture.updateRequest();

        String request = objectMapper.writeValueAsString(brandUpdateRequest);

        MockMultipartFile multipartFile = setMockMultipartFile();

        MessageResponse response = new MessageResponse("브랜드 수정 성공");

        given(brandService.updateBrand(any(UUID.class), any(BrandUpdateRequest.class), any(MockMultipartFile.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/brands/{brandId}", brandUuid)
                        .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                        .file(multipartFile)
                        .contentType(MULTIPART_FORM_DATA)
                        .accept(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("브랜드 수정 실패 - 해당 브랜드를 찾지 못한 경우")
    public void update_brand_fail_not_found() throws Exception {

        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        BrandUpdateRequest brandUpdateRequest = BrandFixture.updateRequest();

        String request = objectMapper.writeValueAsString(brandUpdateRequest);

        MockMultipartFile multipartFile = setMockMultipartFile();

        given(brandService.updateBrand(any(UUID.class), any(BrandUpdateRequest.class), any(MockMultipartFile.class)))
                .willThrow(new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(multipart(HttpMethod.PUT, "/api/v1/brands/{brandUuid}", brandUuid)
                        .file(new MockMultipartFile("request", "", "application/json", request.getBytes(StandardCharsets.UTF_8)))
                        .file(multipartFile)
                        .contentType(MULTIPART_FORM_DATA)
                        .accept(APPLICATION_JSON)
                        .characterEncoding("UTF-8")
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(BRAND_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(BRAND_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("브랜드 삭제 성공")
    public void delete_brand_success() throws Exception {

        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");


        MessageResponse response = new MessageResponse("브랜드 삭제 성공");

        given(brandService.deleteBrand(any(UUID.class)))
                .willReturn(response);

        // when & then
        mockMvc.perform(delete("/api/v1/brands/{brandUuid}", brandUuid)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("브랜드 삭제 실패 - 해당 브랜드를 찾을 수 없는 경우")
    public void delete_brand_fail_not_found() throws Exception {

        // given
        UUID brandUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(brandService.deleteBrand(any(UUID.class)))
                .willThrow(new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(delete("/api/v1/brands/{brandUuid}", brandUuid)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(BRAND_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(BRAND_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("브랜드 검색 성공")
    public void search_brand_success() throws Exception {

        // given
        String brandName = "brand";
        Pageable pageable = PageRequest.of(0, 10); // 0번째 페이지, 한 페이지당 10개의 아이템

        UUID brand1Uuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID brand2Uuid = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");

        BrandInfo brand1 = BrandFixture.createBrandInfo(brand1Uuid);
        BrandInfo brand2 = BrandFixture.createBrandInfo(brand2Uuid);

        List<BrandInfo> mockBrandList = Arrays.asList(brand1, brand2);

        Page<BrandInfo> mockBrandPage = new PageImpl<>(mockBrandList, pageable, mockBrandList.size());

        given(brandService.searchBrands(any(), any(Pageable.class)))
                .willReturn(mockBrandPage);

        // when & then
        mockMvc.perform(get("/api/v1/brands/search")
                        .param("brandName", brandName)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.content[0].uuid").value(brand1.getUuid().toString()))
                .andExpect(jsonPath("$.result.content[0].name").value(brand1.getName()))
                .andExpect(jsonPath("$.result.content[1].uuid").value(brand2.getUuid().toString()))
                .andExpect(jsonPath("$.result.content[1].name").value(brand2.getName()))
                .andDo(print());
    }

    private MockMultipartFile setMockMultipartFile() {
        return new MockMultipartFile("multipartFile", "testImage1" + "." + "png", "png", "<<data>>".getBytes());
    }


}