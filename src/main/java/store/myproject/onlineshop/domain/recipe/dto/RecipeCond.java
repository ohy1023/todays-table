package store.myproject.onlineshop.domain.recipe.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCond {

    @Schema(description = "커서", example = "0196b547-d56b-72bf-977e-4c5865b1489c", required = false)
    private String nextCursor;

    @Builder.Default
    @Schema(description = "페이지 사이즈 (기본값 10)", example = "10", required = false)
    private int size = 10;

    @JsonIgnore
    private int sizePlusOne;
}
