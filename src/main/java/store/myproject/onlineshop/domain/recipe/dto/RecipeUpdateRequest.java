package store.myproject.onlineshop.domain.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레시피 수정 요청 DTO")
public class RecipeUpdateRequest {

    @NotBlank
    @Schema(description = "레시피 제목", example = "간단한 된장찌개")
    private String recipeTitle;

    @NotBlank
    @Schema(description = "레시피 설명", example = "된장과 채소로 만든 한국 전통 국물 요리")
    private String recipeDescription;

    @NotBlank
    @Schema(description = "조리 시간", example = "30분")
    private String recipeCookingTime;

    @NotBlank
    @Schema(description = "인분 수", example = "2")
    private String recipeServings;

    @Schema(description = "썸네일 이미지 URL", example = "https://s3.bucket/thumbnail.jpg")
    private String thumbnailUrl;

    @NotEmpty
    @Schema(description = "사용할 식재료 UUID 목록")
    private List<UUID> itemUuidList;

    @NotEmpty
    @Schema(description = "조리 단계 목록")
    private List<RecipeStepRequest> steps;
}
