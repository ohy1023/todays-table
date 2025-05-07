package store.myproject.onlineshop.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "해당 레시피 좋아요/취소 누르기")
    @PostMapping("/{uuid}/likes")
    public Response<MessageResponse> pushLike(@PathVariable UUID uuid, Authentication authentication) {
        String email = authentication.getName();

        return Response.success(recipeService.toggleLike(uuid, email));
    }
}
