package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageCode;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.customer.Level;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;
import store.myproject.onlineshop.repository.membership.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.global.utils.MessageUtil;

import java.util.List;

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
    @Cacheable(value = "memberships", key = "#id")
    @Transactional(readOnly = true)
    public MemberShipDto getMemberShipById(Long id) {
        MemberShip memberShip = findMemberShipById(id);
        return memberShip.toDto();
    }

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
                .map(MemberShip::toDto)
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

        memberShipRepository.save(request.toEntity());
        return new MessageResponse(messageUtil.get(MessageCode.MEMBERSHIP_ADDED));
    }

    /**
     * 멤버십 수정 (캐시 초기화)
     */
    @CacheEvict(value = "memberships", allEntries = true)
    public MessageResponse updateMemberShip(Long id, MemberShipUpdateRequest request) {
        MemberShip memberShip = findMemberShipById(id);
        memberShip.updateMemberShip(request);
        return new MessageResponse(messageUtil.get(MessageCode.MEMBERSHIP_MODIFIED));
    }

    /**
     * 멤버십 삭제 (캐시 초기화)
     */
    @CacheEvict(value = "memberships", allEntries = true)
    public MessageResponse deleteMemberShip(Long id) {
        MemberShip memberShip = findMemberShipById(id);
        memberShipRepository.deleteById(memberShip.getId());
        return new MessageResponse(messageUtil.get(MessageCode.MEMBERSHIP_DELETED));
    }

    /**
     * ID로 멤버십 조회 (없으면 예외 발생)
     */
    private MemberShip findMemberShipById(Long id) {
        return memberShipRepository.findById(id)
                .orElseThrow(() -> new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));
    }
}
