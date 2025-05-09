package store.myproject.onlineshop.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChildReviewResponse {

    private UUID uuid;
    private String content;
    private String writer;

}
