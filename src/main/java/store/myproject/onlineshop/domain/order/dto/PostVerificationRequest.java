package store.myproject.onlineshop.domain.order.dto;

import lombok.*;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostVerificationRequest {

    private UUID merchantUid;

    private String impUid;
}
