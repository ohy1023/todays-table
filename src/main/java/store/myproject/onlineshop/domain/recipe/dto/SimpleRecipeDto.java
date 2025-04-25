package store.myproject.onlineshop.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimpleRecipeDto {
    private Long recipeId;
    private String title; // 제목
    private String recipeDescription; // 소개
    private String thumbnail; // 썸네일
    private String writer; // 작성자
    private String recipeCookingTime; //조리 시간
    private String recipeServings; // 몇인분
    private int recipeView; // 조회 수
    private Long reviewCnt; // 댓글 수
    private Long likeCnt; // 좋아요  수
}
