package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.dto.common.MessageCode;
import store.myproject.onlineshop.dto.common.MessageResponse;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.dto.membership.MemberShipCreateRequest;
import store.myproject.onlineshop.dto.membership.MemberShipDto;
import store.myproject.onlineshop.dto.membership.MemberShipUpdateRequest;
import store.myproject.onlineshop.domain.membership.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;
import java.util.UUID;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberShipService {

    private final MemberShipRepository memberShipRepository;
    private final MessageUtil messageUtil;

    /**
     * 멤버십 단건 조회 (캐시 적용)
     */
    @Transactional(readOnly = true)
    public MemberShipDto getMemberShip(UUID uuid) {
        MemberShip memberShip = findMemberShipByUuid(uuid);
        return MemberShipDto.from(memberShip);
    }

    /**
     * 해당 레벨의 멤버쉽 존재 여부 체크
     */
    @Transactional(readOnly = true)
    public boolean existsByLevel(Level level) {
        return memberShipRepository.existsByLevel(level);
    }

    /**
     * 전체 멤버십 목록 조회
     */
    @Transactional(readOnly = true)
    public List<MemberShipDto> getAllMemberShips() {
        return memberShipRepository.findAll()
                .stream()
                .map(MemberShipDto::from)
                .toList();
    }

    /**
     * 멤버십 등록
     */
    public MessageResponse createMemberShip(MemberShipCreateRequest request) {
        memberShipRepository.findMemberShipByLevel(request.getLevel())
                .ifPresent((memberShip) -> {
                    throw new AppException(DUPLICATE_MEMBERSHIP, DUPLICATE_MEMBERSHIP.getMessage());
                });

        MemberShip memberShip = memberShipRepository.save(request.toEntity());
        return MessageResponse.of(memberShip.getUuid(), messageUtil.get(MessageCode.MEMBERSHIP_ADDED));
    }

    /**
     * 멤버십 수정 (캐시 초기화)
     */
    @CacheEvict(value = "memberships", allEntries = true)
    public MessageResponse updateMemberShip(UUID uuid, MemberShipUpdateRequest request) {
        MemberShip memberShip = findMemberShipByUuid(uuid);
        memberShip.updateMemberShip(request);
        return MessageResponse.of(memberShip.getUuid(), messageUtil.get(MessageCode.MEMBERSHIP_MODIFIED));
    }

    /**
     * 멤버십 삭제 (캐시 초기화)
     */
    public MessageResponse deleteMemberShip(UUID uuid) {
        MemberShip memberShip = findMemberShipByUuid(uuid);
        memberShipRepository.deleteById(memberShip.getId());
        return MessageResponse.of(memberShip.getUuid(), messageUtil.get(MessageCode.MEMBERSHIP_DELETED));
    }

    /**
     * ID로 멤버십 조회 (없으면 예외 발생)
     */
    private MemberShip findMemberShipByUuid(UUID uuid) {
        return memberShipRepository.findByUuid(uuid)
                .orElseThrow(() -> new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));
    }
}
