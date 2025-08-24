package store.myproject.onlineshop.dto.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {

    private UUID uuid;
    private String content;
    private String writer;
    private List<ChildReviewResponse> childReviews;
    private boolean hasMoreChildReviews;
}