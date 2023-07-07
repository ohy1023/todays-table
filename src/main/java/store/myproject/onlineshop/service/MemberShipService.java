package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.membership.MemberShip;
import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;
import store.myproject.onlineshop.domain.membership.repository.MemberShipRepository;
import store.myproject.onlineshop.exception.AppException;

import java.util.List;

import static store.myproject.onlineshop.exception.ErrorCode.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberShipService {

    private final MemberShipRepository memberShipRepository;

    @Cacheable(value = "memberships", key = "#id")
    public MemberShipDto selectOne(Long id) {

        MemberShip findMemberShip = getMemberShip(id);

        return findMemberShip.toDto();
    }

    public List<MemberShipDto> selectAll() {

        return memberShipRepository.findAll()
                .stream()
                .map(MemberShip::toDto)
                .toList();
    }

    public MemberShipDto saveMemberShip(MemberShipCreateRequest request) {

        memberShipRepository.findMemberShipByLevel(request.getLevel())
                .ifPresent((memberShip) -> {
                    throw new AppException(DUPLICATE_MEMBERSHIP, DUPLICATE_MEMBERSHIP.getMessage());
                });

        MemberShip savedMemberShip = memberShipRepository.save(request.toEntity());

        return savedMemberShip.toDto();

    }

    @CacheEvict(value = "memberships", allEntries = true)
    public MemberShipDto updateMemberShip(Long id, MemberShipUpdateRequest request) {
        MemberShip findMemberShip = getMemberShip(id);

        findMemberShip.updateMemberShip(request);

        return findMemberShip.toDto();
    }

    @CacheEvict(value = "memberships", allEntries = true)
    public MessageResponse deleteMemberShip(Long id) {
        MemberShip findMemberShip = getMemberShip(id);

        memberShipRepository.deleteById(findMemberShip.getId());

        return new MessageResponse("해당 멤버십 삭제가 완료되었습니다.");

    }

    private MemberShip getMemberShip(Long id) {
        return memberShipRepository.findById(id)
                .orElseThrow(() -> new AppException(MEMBERSHIP_NOT_FOUND, MEMBERSHIP_NOT_FOUND.getMessage()));
    }

}
