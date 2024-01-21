package store.myproject.onlineshop.global.event;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;
import store.myproject.onlineshop.domain.alert.dto.AlertRequestDto;
import store.myproject.onlineshop.service.AlertService;

@Component
@RequiredArgsConstructor
public class AlertEmailListener {

    private final AlertService alertService;

    @Async("alertExecutor")
    @TransactionalEventListener
    public void handleNotification(AlertRequestDto requestDto) {
        alertService.send(requestDto.getReceiver(), requestDto.getAlertType(),
                requestDto.getContent(), requestDto.getUrl());
    }
}
