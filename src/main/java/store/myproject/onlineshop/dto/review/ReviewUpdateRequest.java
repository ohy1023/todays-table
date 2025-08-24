package store.myproject.onlineshop.dto.review;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "댓글 수정 요청 DTO")
public class ReviewUpdateRequest {

    @NotBlank
    @Schema(description = "수정할 댓글 내용", example = "댓글 내용을 새로 작성합니다.")
    private String reviewContent;
}
