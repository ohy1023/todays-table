package store.myproject.onlineshop.dto.recipe;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레시피 단계 요청 DTO")
public class RecipeStepRequest {

    @Schema(description = "조리 순서", example = "1")
    private int order;

    @NotBlank
    @Schema(description = "조리 단계 설명", example = "냄비에 물을 붓고 끓입니다.")
    private String content;

    @Schema(description = "조리 단계 이미지 URL", example = "https://s3.bucket/step1.jpg")
    private String imageUrl;
}
