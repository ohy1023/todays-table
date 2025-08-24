package store.myproject.onlineshop.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Schema(description = "준비 상태 응답 DTO")
public class PreparationResponse {

    @Schema(description = "상점 고유 ID", example = "a9dc96bf-2b1b-11f0-b1f0-5b9e0b864120")
    private String merchantUid;

    public static PreparationResponse of(String merchantUid) {
        return new PreparationResponse(merchantUid);
    }
}
