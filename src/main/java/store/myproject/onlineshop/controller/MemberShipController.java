package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.membership.dto.MemberShipCreateRequest;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;
import store.myproject.onlineshop.service.MemberShipService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memberships")
@Tag(name = "MemberShip", description = "멤버쉽 API")
public class MemberShipController {

    private final MemberShipService memberShipService;

    @Operation(summary = "단건 조회")
    @GetMapping("/{id}")
    public Response<MemberShipDto> findOneMemberShip(@PathVariable Long id) {
        MemberShipDto memberShipDto = memberShipService.selectOne(id);

        return Response.success(memberShipDto);
    }

    @Operation(summary = "전체 조회")
    @GetMapping
    public Response<List<MemberShipDto>> findAllMemberShip() {

        List<MemberShipDto> memberShipDtoList = memberShipService.selectAll();

        return Response.success(memberShipDtoList);
    }


    @Operation(summary = "멤버쉽 추가")
    @PostMapping
    public Response<MemberShipDto> createMemberShip(@Valid @RequestBody MemberShipCreateRequest request, Authentication authentication) {

        MemberShipDto response = memberShipService.saveMemberShip(request);

        return Response.success(response);
    }

    @Operation(summary = "멤버쉽 수정")
    @PutMapping("/{id}")
    public Response<MemberShipDto> changeMemberShip(@PathVariable Long id, @Valid @RequestBody MemberShipUpdateRequest request, Authentication authentication) {

        MemberShipDto response = memberShipService.updateMemberShip(id, request);

        return Response.success(response);
    }

    @Operation(summary = "멤버쉽 삭제")
    @DeleteMapping("/{id}")
    public Response<MessageResponse> removeMemberShip(@PathVariable Long id, Authentication authentication) {

        MessageResponse response = memberShipService.deleteMemberShip(id);

        return Response.success(response);
    }
}
