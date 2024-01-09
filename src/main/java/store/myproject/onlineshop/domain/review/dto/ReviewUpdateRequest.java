package store.myproject.onlineshop.domain.review.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewUpdateRequest {


    @NotBlank
    private String reviewContent;

}
