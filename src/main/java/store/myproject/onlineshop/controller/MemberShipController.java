package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.domain.membership.dto.MemberShipDto;
import store.myproject.onlineshop.domain.membership.dto.MemberShipUpdateRequest;
import store.myproject.onlineshop.service.MemberShipService;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/memberships")
@Tag(name = "MemberShip", description = "멤버쉽 API")
public class MemberShipController {

    private final MemberShipService memberShipService;

    @Operation(
            summary = "단건 조회",
            description = "멤버쉽을 UUID로 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "멤버쉽 조회 성공"),
            @ApiResponse(responseCode = "404", description = "멤버쉽을 찾을 수 없음")
    })
    @GetMapping("/{membershipUuid}")
    public ResponseEntity<Response<MemberShipDto>> findOneMemberShip(
            @Parameter(name = "membershipUuid", description = "조회할 멤버쉽의 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120", required = true)
            @PathVariable UUID membershipUuid) {
        MemberShipDto dto = memberShipService.getMemberShip(membershipUuid);
        return ResponseEntity.ok(Response.success(dto));
    }

    @Operation(
            summary = "전체 조회",
            description = "모든 멤버쉽을 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "멤버쉽 목록 조회 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<Response<List<MemberShipDto>>> findAllMemberShip() {
        List<MemberShipDto> list = memberShipService.getAllMemberShips();
        return ResponseEntity.ok(Response.success(list));
    }

    @Operation(
            summary = "멤버쉽 수정",
            description = "UUID를 기준으로 멤버쉽 정보를 수정합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "멤버쉽 수정 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청"),
            @ApiResponse(responseCode = "404", description = "멤버쉽을 찾을 수 없음")
    })
    @PutMapping("/{membershipUuid}")
    public ResponseEntity<Response<MessageResponse>> changeMemberShip(
            @Parameter(name = "membershipUuid", description = "수정할 멤버쉽의 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120", required = true)
            @PathVariable UUID membershipUuid,
            @Valid @RequestBody MemberShipUpdateRequest request) {

        MessageResponse message = memberShipService.updateMemberShip(membershipUuid, request);
        return ResponseEntity.ok(Response.success(message));
    }

    @Operation(
            summary = "멤버쉽 삭제",
            description = "UUID를 기준으로 멤버쉽을 삭제합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "멤버쉽 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "멤버쉽을 찾을 수 없음")
    })
    @DeleteMapping("/{membershipUuid}")
    public ResponseEntity<Response<MessageResponse>> removeMemberShip(
            @Parameter(name = "membershipUuid", description = "삭제할 멤버쉽의 UUID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120", required = true)
            @PathVariable UUID membershipUuid) {

        MessageResponse message = memberShipService.deleteMemberShip(membershipUuid);
        return ResponseEntity.ok(Response.success(message));
    }
}
