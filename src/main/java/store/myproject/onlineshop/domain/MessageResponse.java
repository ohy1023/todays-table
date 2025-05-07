package store.myproject.onlineshop.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 필드 제외
public class MessageResponse {
    private UUID uuid;
    private String message;

    public MessageResponse(String message) {
        this.message = message;
    }
}
