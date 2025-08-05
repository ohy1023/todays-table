package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import store.myproject.onlineshop.domain.MessageResponse;
import store.myproject.onlineshop.domain.Response;
import store.myproject.onlineshop.service.RecipeService;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/recipes")
@Tag(name = "Recipe", description = "레시피 API")
public class LikeController {

    private final RecipeService recipeService;

    @Operation(
            summary = "해당 레시피 좋아요/취소 누르기",
            description = "사용자가 레시피에 좋아요를 누르거나, 이미 좋아요를 눌렀을 경우 취소합니다."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요/취소 성공"),
            @ApiResponse(responseCode = "404", description = "레시피를 찾을 수 없음"),
            @ApiResponse(responseCode = "401", description = "인증 실패")
    })
    @PostMapping("/{recipeUuid}/likes")
    public ResponseEntity<Response<MessageResponse>> pushLike(
            @Parameter(name = "recipeUuid", description = "좋아요를 누를 레시피의 UUID", example = "6516f24e-2be4-11f0-bff7-453261748c60", required = true)
            @PathVariable UUID recipeUuid,
            Authentication authentication) {

        String email = authentication.getName();
        MessageResponse messageResponse = recipeService.toggleLike(recipeUuid, email);

        return ResponseEntity.ok(Response.success(messageResponse));
    }
}