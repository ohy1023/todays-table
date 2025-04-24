package store.myproject.onlineshop.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private Long id;
    private String content;
    private String writer;
    private List<ChildReviewResponse> childReviews;
    private boolean hasMoreChildReviews;
}