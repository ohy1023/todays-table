package store.myproject.onlineshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.dto.membership.MemberShipDto;
import store.myproject.onlineshop.dto.membership.MemberShipUpdateRequest;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.fixture.MemberShipFixture;
import store.myproject.onlineshop.service.MemberShipService;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static store.myproject.onlineshop.domain.customer.Level.*;
import static store.myproject.onlineshop.exception.ErrorCode.*;
import static store.myproject.onlineshop.fixture.ResultCode.ERROR;
import static store.myproject.onlineshop.fixture.ResultCode.SUCCESS;


@WebMvcTest(MemberShipController.class)
@AutoConfigureMockMvc
@WithMockUser
class MemberShipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberShipService memberShipService;

    @Test
    @DisplayName("단건 멤버쉽 조회 성공")
    void get_membership_success() throws Exception {

        // given: 멤버쉽 서비스가 반환할 DTO 정의 및 Mock 설정
        UUID membershipUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        MemberShipDto response = MemberShipFixture.createBronzeDto();

        given(memberShipService.getMemberShip(any(UUID.class)))
                .willReturn(response);

        // when & then: GET 요청을 수행하고 응답을 검증
        mockMvc.perform(get("/api/v1/memberships/{membershipUuid}", membershipUuid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.baseline").value(response.getBaseline()))
                .andExpect(jsonPath("$.result.discountRate").value(response.getDiscountRate()))
                .andDo(print());
    }

    @Test
    @DisplayName("단건 멤버쉽 조회 실패 - 존재하지 않는 멤버쉽")
    void get_membership_fail_membership_not_found() throws Exception {

        // given: 멤버쉽 서비스가 반환할 DTO 정의 및 Mock 설정
        UUID membershipUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(memberShipService.getMemberShip(any(UUID.class)))
                .willThrow(new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));

        // when & then: GET 요청을 수행하고 응답을 검증
        mockMvc.perform(get("/api/v1/memberships/{membershipUuid}",membershipUuid))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(MEMBERSHIP_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(MEMBERSHIP_NOT_FOUND.getMessage()))
                .andDo(print());

    }

    @Test
    @DisplayName("멤버쉽 전체 조회")
    void get_all_memberships() throws Exception {
        // given
        List<MemberShipDto> response = List.of(
                MemberShipFixture.createBronzeDto(),
                MemberShipFixture.createSilverDto()
        );

        given(memberShipService.getAllMemberShips()).willReturn(response);

        // when & then: GET 요청을 수행하고 응답을 검증
        mockMvc.perform(get("/api/v1/memberships"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result[0].level").value(BRONZE.name()))
                .andExpect(jsonPath("$.result[1].level").value(SILVER.name()))
                .andExpect(jsonPath("$.result.size()").value(response.size()))
                .andDo(print());
    }

    @Test
    @DisplayName("멤버쉽 수정 성공")
    @WithMockUser(roles = "ADMIN")
    void modify_membership_success() throws Exception {
        // given
        UUID membershipUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        MemberShipUpdateRequest request = MemberShipFixture.updateToBronzeRequest();

        MessageResponse response = MessageResponse.of("멤버쉽 삭제 성공");

        given(memberShipService.updateMemberShip(membershipUuid, request)).willReturn(response);

        // when & then
        mockMvc.perform(put("/api/v1/memberships/{membershipUuid}",membershipUuid)
                        .with(csrf())
                        .content(objectMapper.writeValueAsBytes(request))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value(response.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("멤버십 수정 실패 - 존재하지 않는 멤버십")
    @WithMockUser(roles = "ADMIN")
    void modify_membership_fail_not_found() throws Exception {
        // given
        UUID membershipUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        MemberShipUpdateRequest request = MemberShipFixture.updateToBronzeRequest();

        given(memberShipService.updateMemberShip(membershipUuid, request))
                .willThrow(new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(put("/api/v1/memberships/{membershipUuid}",membershipUuid)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(MEMBERSHIP_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(MEMBERSHIP_NOT_FOUND.getMessage()))
                .andDo(print());
    }

    @Test
    @DisplayName("멤버십 삭제")
    @WithMockUser(roles = "ADMIN")
    void removeMemberShip() throws Exception {
        UUID membershipUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(memberShipService.deleteMemberShip(membershipUuid)).willReturn(MessageResponse.of("삭제 완료"));

        mockMvc.perform(delete("/api/v1/memberships/{membershipUuid}", membershipUuid)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.resultCode").value(SUCCESS))
                .andExpect(jsonPath("$.result.message").value("삭제 완료"));
    }


    @Test
    @DisplayName("멤버십 삭제 실패 - 존재하지 않는 멤버십")
    @WithMockUser(roles = "ADMIN")
    void remove_membership_fail_not_found() throws Exception {
        // given
        UUID membershipUuid = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        given(memberShipService.deleteMemberShip(membershipUuid))
                .willThrow(new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));

        // when & then
        mockMvc.perform(delete("/api/v1/memberships/{membershipUuid}", membershipUuid)
                        .with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.resultCode").value(ERROR))
                .andExpect(jsonPath("$.result.errorCode").value(MEMBERSHIP_NOT_FOUND.name()))
                .andExpect(jsonPath("$.result.message").value(MEMBERSHIP_NOT_FOUND.getMessage()))
                .andDo(print());
    }

}