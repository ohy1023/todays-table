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

    public static RecipeCursorResponse of(List<SimpleRecipeDto> content, UUID nextUuid, Long nextViewCount, Long nextLikeCount) {
        return RecipeCursorResponse.builder()
                .content(content)
                .nextUuid(nextUuid)
                .nextViewCount(nextViewCount)
                .nextLikeCount(nextLikeCount)
                .build();
    }
}
