package store.myproject.onlineshop.domain.order.dto;

import lombok.*;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PreparationResponse {

    private String merchantUid;

}
