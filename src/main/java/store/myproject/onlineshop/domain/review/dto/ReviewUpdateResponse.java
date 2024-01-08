package store.myproject.onlineshop.domain.review.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewUpdateResponse {


    // 리뷰 종류 (댓글 ,대댓글)
    private String reviewType;

    // 리뷰 내용
    private String reviewContent;

    // 리뷰를 작성한 고객 이메일
    private String email;

    // 리뷰를 단 레시피 ID
    private Long recipeId;
}
