package store.myproject.onlineshop.domain.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "레시피 간략 정보 DTO")
public class SimpleRecipeDto {

    @Schema(description = "레시피 고유 ID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private UUID recipeUuid;

    @Schema(description = "레시피 제목", example = "맛있는 떡볶이")
    private String title;

    @Schema(description = "레시피 소개", example = "매운 떡볶이를 만드는 방법")
    private String recipeDescription;

    @Schema(description = "레시피 썸네일 이미지 URL", example = "https://example.com/thumbnail.jpg")
    private String thumbnail;

    @Schema(description = "레시피 작성자", example = "홍길동")
    private String writer;

    @Schema(description = "레시피 조리 시간", example = "30")
    private Integer recipeCookingTime;

    @Schema(description = "레시피 인분", example = "2")
    private Integer recipeServings;

    @Schema(description = "레시피 조회 수", example = "1500")
    private Long recipeView;

    @Schema(description = "레시피 댓글 수", example = "20")
    private Long reviewCnt;

    @Schema(description = "레시피 좋아요 수", example = "100")
    private Long likeCnt;

}
