package store.myproject.onlineshop.domain.recipe.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeStepRequest {

    private int order;  // 순서 필드

    @NotBlank
    private String content;  // 텍스트는 필수

    private String imageUrl; // 이미지 URL은 선택 (null 가능)

}
