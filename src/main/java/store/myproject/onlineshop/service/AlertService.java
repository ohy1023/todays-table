package store.myproject.onlineshop.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import store.myproject.onlineshop.domain.alert.Alert;
import store.myproject.onlineshop.domain.alert.AlertType;
import store.myproject.onlineshop.domain.alert.dto.AlertResponseDto;
import store.myproject.onlineshop.exception.AppException;
import store.myproject.onlineshop.exception.ErrorCode;
import store.myproject.onlineshop.repository.alert.AlertRepository;
import store.myproject.onlineshop.repository.alert.EmitterRepository;
import store.myproject.onlineshop.domain.customer.Customer;
import store.myproject.onlineshop.repository.customer.CustomerRepository;

import java.io.IOException;
import java.util.Map;

/**
 * 실시간 알림 서비스를 담당하는 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class AlertService {

    // 기본적인 타임아웃 설정 (60분)
    private final static long TIMEOUT = 60L * 1000 * 60;

    // 의존성 주입을 통해 사용할 레포지토리들
    private final CustomerRepository customerRepository;
    private final EmitterRepository emitterRepository;
    private final AlertRepository alertRepository;

    /**
     * 클라이언트가 알림을 구독할 때 호출되는 메서드입니다.
     *
     * @param email       클라이언트의 이메일
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트의 ID
     * @return SseEmitter 객체
     */
    public SseEmitter subscribe(String email, String lastEventId) {
        // 회원 조회
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.CUSTOMER_NOT_FOUND));
        Long customerId = customer.getId();

        // 고유한 emitterId를 생성합니다.
        String emitterId = makeTimeIncludeId(customerId);

        // 새로운 SseEmitter를 생성하고 emitterRepository에 저장합니다.
        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(TIMEOUT));

        // 알림이 종료되거나 타임아웃될 때 해당 emitter를 삭제합니다.
        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = makeTimeIncludeId(customerId);
        sendNotification(emitter, eventId, emitterId, "EventStream Created. [userId=" + customerId + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, customerId, emitterId, emitter);
        }

        return emitter;
    }

    /**
     * 현재 시간을 포함한 고유한 ID를 생성합니다.
     *
     * @param customerId 클라이언트의 고유 ID
     * @return 고유한 ID 문자열
     */
    private String makeTimeIncludeId(Long customerId) {
        return customerId + "_" + System.currentTimeMillis();
    }

    /**
     * SseEmitter에 알림을 전송합니다.
     *
     * @param emitter   SseEmitter 객체
     * @param eventId   이벤트 ID
     * @param emitterId SseEmitter의 고유 ID
     * @param data      전송할 데이터
     */
    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .data(data));
        } catch (IOException exception) {
            // 전송 중 오류가 발생하면 해당 emitter를 삭제합니다.
            emitterRepository.deleteById(emitterId);
        }
    }

    /**
     * 클라이언트가 미수신한 Event 목록이 존재하는지 확인합니다.
     *
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트의 ID
     * @return 이벤트가 누락된 경우 true, 그렇지 않은 경우 false
     */
    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    /**
     * 클라이언트에게 누락된 Event를 전송합니다.
     *
     * @param lastEventId 클라이언트가 마지막으로 수신한 이벤트의 ID
     * @param customerId  클라이언트의 고유 ID
     * @param emitterId   SseEmitter의 고유 ID
     * @param emitter     SseEmitter 객체
     */
    private void sendLostData(String lastEventId, Long customerId, String emitterId, SseEmitter emitter) {
        // 고객의 미수신 Event 목록을 가져와 필터링하고, 해당 이벤트를 클라이언트에게 전송합니다.
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByCustomerId(String.valueOf(customerId));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    /**
     * 알림을 생성하고 저장한 후, 해당 알림을 구독 중인 클라이언트에게 전송합니다.
     *
     * @param receiver  알림을 받을 고객 객체
     * @param alertType 알림 유형
     * @param content   알림 내용
     * @param url       관련 URL
     */
    public void send(Customer receiver, AlertType alertType, String content, String url) {
        // 알림 생성 및 저장
        Alert alert = alertRepository.save(createNotification(receiver, alertType, content, url));

        // 알림을 받을 고객의 ID를 문자열로 변환
        String receiverId = String.valueOf(receiver.getId());

        // 현재 시간을 포함한 고유한 이벤트 ID 생성
        String eventId = receiverId + "_" + System.currentTimeMillis();

        // 알림을 받을 클라이언트의 SseEmitter 목록을 가져옴
        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByCustomerId(receiverId);

        // 각 SseEmitter에 알림을 전송하고, 이벤트 캐시에 저장
        emitters.forEach(
                (key, emitter) -> {
                    emitterRepository.saveEventCache(key, alert);
                    sendNotification(emitter, eventId, key, AlertResponseDto.create(alert));
                }
        );
    }

    /**
     * 알림을 생성합니다.
     *
     * @param receiver  알림을 받을 고객 객체
     * @param alertType 알림 유형
     * @param content   알림 내용
     * @param url       관련 URL
     * @return 생성된 알림 객체
     */
    private Alert createNotification(Customer receiver, AlertType alertType, String content, String url) {
        return Alert.builder()
                .receiver(receiver)
                .alertType(alertType)
                .content(content)
                .relatedUrl(url)
                .isRead(false)
                .build();
    }
}


