package store.myproject.onlineshop.domain.recipemeta;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecipeMeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_meta_id")
    private Long id;

    // 좋아요 수
    @Column(nullable = false, name = "like_cnt")
    private Long likeCnt;

    // 리뷰 수
    @Column(nullable = false, name = "review_cnt")
    private Long reviewCnt;

    // 조회수
    @Column(nullable = false, name = "recipe_view")
    private Long viewCnt;
}
