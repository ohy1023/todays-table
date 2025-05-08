package store.myproject.onlineshop.domain.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import store.myproject.onlineshop.domain.recipeitem.dto.RecipeItemDto;
import store.myproject.onlineshop.domain.recipestep.dto.RecipeStepDto;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "레시피 정보 DTO")
public class RecipeDto {

    @Schema(description = "레시피 고유 ID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID recipeUuid;

    @Schema(description = "레시피 제목", example = "맛있는 떡볶이")
    private String recipeTitle;

    @Schema(description = "레시피 설명", example = "매운 떡볶이를 만드는 방법")
    private String recipeDescription;

    @Schema(description = "조리 시간", example = "30분")
    private String recipeCookingTime;

    @Schema(description = "레시피 인분", example = "2인분")
    private String recipeServings;

    @Schema(description = "레시피 작성자", example = "홍길동")
    private String recipeWriter;

    @Schema(description = "레시피 썸네일 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnailUrl;

    @Schema(description = "레시피 조회 수", example = "1200")
    private Long recipeView;

    @Schema(description = "레시피 댓글 수", example = "50")
    private Long reviewCnt;

    @Schema(description = "레시피 좋아요 수", example = "300")
    private Long likeCnt;

    @Schema(description = "레시피에 포함된 아이템들")
    private List<RecipeItemDto> items;

    @Schema(description = "레시피 단계들")
    private List<RecipeStepDto> steps;

    public RecipeDto(UUID recipeUuid, String recipeTitle, String recipeDescription, String recipeCookingTime, String recipeServings, String recipeWriter, String thumbnailUrl, Long recipeView, Long reviewCnt, Long likeCnt) {
        this.recipeUuid = recipeUuid;
        this.recipeTitle = recipeTitle;
        this.recipeDescription = recipeDescription;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeServings = recipeServings;
        this.recipeWriter = recipeWriter;
        this.thumbnailUrl = thumbnailUrl;
        this.recipeView = recipeView;
        this.reviewCnt = reviewCnt;
        this.likeCnt = likeCnt;
    }
}
