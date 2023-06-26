package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import store.myproject.onlineshop.custom.WithMockCustomUser;
import store.myproject.onlineshop.domain.dto.brand.BrandCreateRequest;
import store.myproject.onlineshop.domain.dto.brand.BrandCreateResponse;
import store.myproject.onlineshop.domain.dto.brand.BrandInfo;
import store.myproject.onlineshop.domain.enums.CustomerRole;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.service.BrandService;

import java.nio.charset.StandardCharsets;

import static org.springframework.http.MediaType.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static store.myproject.onlineshop.exception.ErrorCode.*;

@WebMvcTest(BrandController.class)
class BrandControllerTest {

    @MockBean
    BrandService brandService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;


    @Nested
    @DisplayName("브랜드 단건 조회")
    class find_brand {

        @Nested
        @DisplayName("성공")
        class success {

            @Test
            @DisplayName("성공")
            @WithMockCustomUser
            public void findOne_success() throws Exception {

                // given
                BrandInfo response = BrandInfo.builder()
                        .id(1L)
                        .name("test")
                        .originImagePath("testImage")
                        .build();

                given(brandService.getBrandInfo(any(Long.class)))
                        .willReturn(response);

                // when & then
                mockMvc.perform(get("/api/v1/brands/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
                        .andExpect(jsonPath("$.result.id").value(1L))
                        .andExpect(jsonPath("$.result.name").value("test"))
                        .andExpect(jsonPath("$.result.originImagePath").value("testImage"))
                        .andDo(print());

            }
        }

        @Nested
        @DisplayName("실패")
        class fail {

            @Test
            @DisplayName("존재하지 않는 브랜드")
            @WithMockCustomUser
            public void findOne_fail_not_found_brand() throws Exception {

                // given
                given(brandService.getBrandInfo(any(Long.class)))
                        .willThrow(new AppException(BRAND_NOT_FOUND, BRAND_NOT_FOUND.getMessage()));

                // when & then
                mockMvc.perform(get("/api/v1/brands/1"))
                        .andExpect(status().isNotFound())
                        .andExpect(jsonPath("$.resultCode").value("ERROR"))
                        .andExpect(jsonPath("$.result.errorCode").value(BRAND_NOT_FOUND.name()))
                        .andExpect(jsonPath("$.result.message").value(BRAND_NOT_FOUND.getMessage()))
                        .andDo(print());

            }
        }

    }


    @Test
    @DisplayName("브랜드 등록 성공")
    @WithMockCustomUser(role = CustomerRole.ROLE_CUSTOMER)
    public void create_brand_success() throws Exception {

        // given
        BrandCreateRequest brandCreateRequest = BrandCreateRequest.builder()
                .name("test")
                .originImagePath("string")
                .build();

        String request = objectMapper.writeValueAsString(brandCreateRequest);

        final String fileName = "testImage1"; //파일명
        final String contentType = "png"; //파일타입
        MockMultipartFile multipartFile = setMockMultipartFile(fileName, contentType);

        BrandCreateResponse response = BrandCreateResponse.builder()
                .name("test")
                .originImagePath("test")
                .build();

        given(brandService.saveBrand(any(BrandCreateRequest.class), any(MockMultipartFile.class)))
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
                .andDo(print());
    }


    private MockMultipartFile setMockMultipartFile(String fileName, String contentType) {
        return new MockMultipartFile("multipartFile", fileName + "." + contentType, contentType, "<<data>>".getBytes());
    }


}