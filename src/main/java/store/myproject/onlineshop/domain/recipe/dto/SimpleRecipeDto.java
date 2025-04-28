package store.myproject.onlineshop.domain.recipe.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder
public class SimpleRecipeDto {
    private Long recipeId;
    private String title; // 제목
    private String recipeDescription; // 소개
    private String thumbnail; // 썸네일
    private String writer; // 작성자
    private String recipeCookingTime; //조리 시간
    private String recipeServings; // 몇인분
    private Long recipeView; // 조회 수
    private Long reviewCnt; // 댓글 수
    private Long likeCnt; // 좋아요  수

    @QueryProjection
    public SimpleRecipeDto(Long recipeId, String title, String recipeDescription, String thumbnail,
                           String writer, String recipeCookingTime, String recipeServings,
                           Long recipeView, Long reviewCnt, Long likeCnt) {
        this.recipeId = recipeId;
        this.title = title;
        this.recipeDescription = recipeDescription;
        this.thumbnail = thumbnail;
        this.writer = writer;
        this.recipeCookingTime = recipeCookingTime;
        this.recipeServings = recipeServings;
        this.recipeView = recipeView;
        this.reviewCnt = reviewCnt;
        this.likeCnt = likeCnt;
    }
}
