package store.myproject.onlineshop.dto.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSearchCond {


    @Schema(description = "검색 시작일 (yyyy-MM-dd)", example = "2025-01-01", type = "string", format = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate fromDate;

    @Schema(description = "검색 종료일 (yyyy-MM-dd)", example = "2025-12-31", type = "string", format = "date", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private LocalDate toDate;

    @Schema(description = "상품 이름으로 검색", example = "Onion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String itemName;

    @Schema(description = "브랜드 이름으로 검색", example = "풀무원", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String brandName;

    @Schema(description = "커서 기준 merchantUid(UUID 문자열)", example = "ca125af9-5587-11f0-b3df-03453729bcd1", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private String merchantUid;

    @Builder.Default
    @Schema(description = "페이지 사이즈 (기본값 10)", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    private int size = 10;

    @JsonIgnore
    private int sizePlusOne;
}
