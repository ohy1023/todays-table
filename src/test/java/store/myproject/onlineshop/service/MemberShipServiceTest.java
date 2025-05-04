//package store.myproject.onlineshop.service;
//
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.transaction.annotation.Transactional;
//import store.myproject.onlineshop.domain.MessageCode;
//import store.myproject.onlineshop.domain.MessageResponse;
//import store.myproject.onlineshop.domain.customer.Level;
//import store.myproject.onlineshop.domain.membership.MemberShip;
//import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
//import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
//import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;
//import store.myproject.onlineshop.repository.membership.MemberShipRepository;
//import store.myproject.onlineshop.exception.AppException;
//import store.myproject.onlineshop.exception.ErrorCode;
//import store.myproject.onlineshop.fixture.MemberShipFixture;
//import store.myproject.onlineshop.global.utils.MessageUtil;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.BDDMockito.then;
//import static org.mockito.Mockito.never;
//
//@ExtendWith(MockitoExtension.class)
//class MemberShipServiceTest {
//
//    @Mock
//    private MemberShipRepository memberShipRepository;
//
//    @Mock
//    private MessageUtil messageUtil;
//
//    @InjectMocks
//    private MemberShipService memberShipService;
//
//    MemberShip memberShip = MemberShipFixture.createBronzeMembership();
//
//    @Test
//    @DisplayName("단건 조회 성공")
//    void get_membership_by_id_success() {
//        given(memberShipRepository.findById(1L)).willReturn(Optional.of(memberShip));
//
//        MemberShipDto response = memberShipService.getMemberShipById(1L);
//
//        assertThat(response.getLevel()).isEqualTo(Level.BRONZE);
//    }
//
//    @Test
//    @DisplayName("단건 조회 실패 - 존재하지 않음")
//    void get_membership_by_id_fail_not_found() {
//        given(memberShipRepository.findById(1L)).willReturn(Optional.empty());
//
//        assertThatThrownBy(() -> memberShipService.getMemberShipById(1L))
//                .isInstanceOf(AppException.class);
//    }
//
//    @Test
//    @DisplayName("전체 조회 성공")
//    void get_all_memberships_success() {
//        given(memberShipRepository.findAll()).willReturn(List.of(memberShip));
//
//        List<MemberShipDto> response = memberShipService.getAllMemberShips();
//
//        assertThat(response.get(0).getLevel()).isEqualTo(Level.BRONZE);
//        assertThat(response.get(0).getBaseline()).isEqualTo(memberShip.getBaseline());
//        assertThat(response.get(0).getDiscountRate()).isEqualTo(memberShip.getDiscountRate());
//    }
//
//
//    @Test
//    @DisplayName("멤버십 등록 성공")
//    void save_success() {
//
//        // given
//        MemberShipCreateRequest request = MemberShipFixture.createBronzeRequest();
//        MemberShip savedEntity = request.toEntity();
//
//        given(memberShipRepository.findMemberShipByLevel(request.getLevel())).willReturn(Optional.empty());
//        given(memberShipRepository.save(any(MemberShip.class))).willReturn(savedEntity);
//        given(messageUtil.get(MessageCode.MEMBERSHIP_ADDED)).willReturn("멤버십이 등록되었습니다.");
//
//        // when
//        MessageResponse response = memberShipService.createMemberShip(request);
//
//        // then
//        assertThat(response).isNotNull();
//        assertThat(response.getMessage()).isEqualTo("멤버십이 등록되었습니다.");
//        then(memberShipRepository).should().save(any());
//    }
//
//    @Test
//    @DisplayName("멤버십 등록 실패 - 중복 레벨")
//    void save_fail_duplicate() {
//        // given
//        MemberShipCreateRequest request = MemberShipFixture.createBronzeRequest();
//        given(memberShipRepository.findMemberShipByLevel(request.getLevel()))
//                .willReturn(Optional.of(MemberShipFixture.createBronzeMembership()));
//
//        // when & then
//        assertThatThrownBy(() -> memberShipService.createMemberShip(request))
//                .isInstanceOf(AppException.class)
//                .hasMessageContaining(ErrorCode.DUPLICATE_MEMBERSHIP.getMessage());
//
//        then(memberShipRepository).should(never()).save(any());
//    }
//
//    @Test
//    @DisplayName("멤버십 수정 성공")
//    void update_membership_success() {
//        MemberShipUpdateRequest request = MemberShipFixture.updateToBronzeRequest();
//        given(memberShipRepository.findById(1L)).willReturn(Optional.of(memberShip));
//        given(messageUtil.get(MessageCode.MEMBERSHIP_MODIFIED)).willReturn("해당 멤버쉽이 수정되었습니다.");
//
//        MessageResponse response = memberShipService.updateMemberShip(1L, request);
//
//        assertThat(response.getMessage()).isEqualTo("해당 멤버쉽이 수정되었습니다.");
//    }
//
//    @Test
//    @DisplayName("멤버십 수정 실패 - 존재 하지 않는 멤버쉽")
//    void update_membership_fail_not_found() {
//        // given
//        MemberShipUpdateRequest request = MemberShipFixture.updateToBronzeRequest();
//
//        given(memberShipRepository.findById(any(Long.class))).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> memberShipService.updateMemberShip(1L, request))
//                .isInstanceOf(AppException.class)
//                .hasMessageContaining(ErrorCode.MEMBERSHIP_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("멤버십 삭제 성공")
//    void delete_success() {
//        // given
//        MemberShip entity = MemberShipFixture.createBronzeMembership();
//
//        given(memberShipRepository.findById(1L)).willReturn(Optional.of(entity));
//        given(messageUtil.get(MessageCode.MEMBERSHIP_DELETED)).willReturn("해당 멤버십이 삭제되었습니다.");
//
//        // when
//        MessageResponse response = memberShipService.deleteMemberShip(1L);
//
//        // then
//        assertThat(response.getMessage()).isEqualTo("해당 멤버십이 삭제되었습니다.");
//        then(memberShipRepository).should().deleteById(entity.getId());
//    }
//
//    @Test
//    @DisplayName("멤버십 삭제 실패")
//    void delete_fail_not_found() {
//        // given
//        given(memberShipRepository.findById(any(Long.class))).willReturn(Optional.empty());
//
//        // when & then
//        assertThatThrownBy(() -> memberShipService.deleteMemberShip(1L))
//                .isInstanceOf(AppException.class)
//                .hasMessageContaining(ErrorCode.MEMBERSHIP_NOT_FOUND.getMessage());
//    }
//
//    @Test
//    @DisplayName("해당 레벨의 멤버쉽 존재 여부")
//    void exist_membership_true() {
//        // given
//        Level level = Level.GOLD;
//        given(memberShipRepository.existsByLevel(level)).willReturn(true);
//
//        // when
//        boolean exists = memberShipService.existsByLevel(level);
//
//        // then
//        assertThat(exists).isTrue();
//    }
//}
