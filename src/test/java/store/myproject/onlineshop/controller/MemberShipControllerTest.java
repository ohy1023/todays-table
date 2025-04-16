package store.myproject.onlineshop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.service.MemberShipService;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.ERROR;
import static store.myproject.onlineshop.fixture.ResultCode.SUCCESS;


@WebMvcTest(MemberShipController.class)
@AutoConfigureMockMvc
@WithMockUser
class MemberShipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MemberShipService memberShipService;

    @Test
    @DisplayName("단건 멤버쉽 조회 성공")

    void get_membership_success() throws Exception {

        // given: 멤버쉽 서비스가 반환할 DTO 정의 및 Mock 설정
        MemberShipDto response = MemberShipDto.builder()
                .baseline(BigDecimal.ZERO)
                .discountRate(BigDecimal.ZERO)
                .level(Level.BRONZE)
                .build();

        given(memberShipService.selectOne(any(Long.class)))
                .willReturn(response);

        // when & then: GET 요청을 수행하고 응답을 검증
        mockMvc.perform(get("/api/v1/memberships/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.baseline").value(BigDecimal.ZERO))
                .andExpect(jsonPath("$.result.discountRate").value(BigDecimal.ZERO))
                .andDo(print());
    }

    @Test
    @DisplayName("단건 멤버쉽 조회 실패")
    void get_membership_fail_membership_not_found() throws Exception {

        // given: 멤버쉽 서비스가 반환할 DTO 정의 및 Mock 설정
        given(memberShipService.selectOne(any(Long.class)))
                .willThrow(new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));

        // when & then: GET 요청을 수행하고 응답을 검증
        mockMvc.perform(get("/api/v1/memberships/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(MEMBERSHIP_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(MEMBERSHIP_NOT_FOUND.getMessage()))
                .andDo(print());

    }
}