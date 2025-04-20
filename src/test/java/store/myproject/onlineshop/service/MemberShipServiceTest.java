package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.repository.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.fixture.MemberShipFixture;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class MemberShipServiceTest {

    @Mock
    private MemberShipRepository memberShipRepository;

    @InjectMocks
    private MemberShipService memberShipService;

    @Test
    @DisplayName("멤버십 등록 성공")
    void save_success() {

        // given
        MemberShipCreateRequest request = MemberShipFixture.createBronzeRequest();
        MemberShip savedEntity = request.toEntity();

        given(memberShipRepository.findMemberShipByLevel(request.getLevel())).willReturn(Optional.empty());
        given(memberShipRepository.save(any(MemberShip.class))).willReturn(savedEntity);

        // when
        MemberShipDto result = memberShipService.saveMemberShip(request);

        // then
        assertThat(result.getLevel()).isEqualTo(savedEntity.getLevel());
        then(memberShipRepository).should().save(any());
    }

    @Test
    @DisplayName("멤버십 등록 실패 - 중복 레벨")
    void save_fail_duplicate() {
        // given
        MemberShipCreateRequest request = MemberShipFixture.createBronzeRequest();
        given(memberShipRepository.findMemberShipByLevel(request.getLevel()))
                .willReturn(Optional.of(MemberShipFixture.createBronzeMembership()));

        // when & then
        assertThatThrownBy(() -> memberShipService.saveMemberShip(request))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.DUPLICATE_MEMBERSHIP.getMessage());

        then(memberShipRepository).should(never()).save(any());
    }

    @Test
    @DisplayName("멤버십 삭제 성공")
    void delete_success() {
        // given
        MemberShip entity = MemberShipFixture.createBronzeMembership();
        given(memberShipRepository.findById(1L)).willReturn(Optional.of(entity));

        // when
        MessageResponse result = memberShipService.deleteMemberShip(1L);

        // then
        assertThat(result.getMsg()).contains("삭제");
        then(memberShipRepository).should().deleteById(entity.getId());
    }

    @Test
    @DisplayName("멤버십 삭제 실패")
    void delete_fail_not_found() {
        // given
        given(memberShipRepository.findById(any(Long.class))).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> memberShipService.deleteMemberShip(1L))
                .isInstanceOf(AppException.class)
                .hasMessageContaining(ErrorCode.MEMBERSHIP_NOT_FOUND.getMessage());
    }
}
