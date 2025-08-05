package store.myproject.onlineshop.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCursorResponse {
    private List<SimpleRecipeDto> content;
    private UUID nextUuid;
    private Long nextViewCount;
    private Long nextLikeCount;
}
