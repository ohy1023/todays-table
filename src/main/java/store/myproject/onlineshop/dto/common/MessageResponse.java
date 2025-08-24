package store.myproject.onlineshop.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드 제외
public class MessageResponse {
    private UUID uuid;
    private String message;

    private MessageResponse(String message) {
        this.message = message;
    }

    // 정적 팩토리 메서드
    public static MessageResponse of(final String message) {
        return new MessageResponse(message);
    }

    public static MessageResponse of(final UUID uuid, final String message) {
        return new MessageResponse(uuid, message);
    }
}
