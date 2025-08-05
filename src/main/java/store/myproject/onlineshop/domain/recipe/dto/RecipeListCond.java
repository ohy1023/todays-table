package store.myproject.onlineshop.domain.recipe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeListCond {

    @Schema(description = "몇 인분")
    private Integer servings;

    @Schema(description = "조리 시간 범위 시작 (분)")
    private Integer cookingTimeFrom;

    @Schema(description = "조리 시간 범위 끝 (분)")
    private Integer cookingTimeTo;

    @Schema(description = "다음 페이지 커서 - UUID", example = "")
    private String nextUuid;

    @Schema(description = "다음 페이지 커서 - 조회수")
    private Long nextViewCount;

    @Schema(description = "다음 페이지 커서 - 추천수")
    private Long nextLikeCount;

    @Schema(description = "페이지 사이즈", example = "10")
    private int size = 10;

    @JsonIgnore
    private int sizePlusOne;

    @Schema(description = "정렬 기준 (recent, view, like)", example = "recent")
    private String sortBy = "recent";
}
