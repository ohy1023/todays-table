package store.myproject.onlineshop.domain.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class MyOrderSliceResponse {

    private List<MyOrderResponse> content;
    private UUID nextCursor; // null이면 다음 없음
}
