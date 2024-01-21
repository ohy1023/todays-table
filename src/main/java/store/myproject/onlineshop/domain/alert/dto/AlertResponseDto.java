package store.myproject.onlineshop.domain.alert.dto;

import lombok.Builder;
import lombok.Data;
import store.myproject.onlineshop.domain.alert.Alert;
import store.myproject.onlineshop.domain.alert.AlertType;

@Data
@Builder
public class AlertResponseDto {

    private Long id;
    private AlertType alertType;
    private String content;
    private String relatedUrl;
    private boolean isRead;

    public static AlertResponseDto create(Alert alert) {
        return AlertResponseDto.builder()
                .id(alert.getId())
                .alertType(alert.getAlertType())
                .content(alert.getContent())
                .relatedUrl(alert.getRelatedUrl())
                .isRead(alert.getIsRead())
                .build();
    }
}