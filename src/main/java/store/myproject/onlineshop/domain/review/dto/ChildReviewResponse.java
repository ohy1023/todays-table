package store.myproject.onlineshop.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildReviewResponse {

    private Long id;
    private String content;
    private String writer;

}
