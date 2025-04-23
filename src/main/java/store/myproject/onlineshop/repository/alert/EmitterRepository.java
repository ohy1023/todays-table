package store.myproject.onlineshop.repository.alert;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

public interface EmitterRepository {
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    void saveEventCache(String emitterId, Object event);

    Map<String, SseEmitter> findAllEmitterStartWithByCustomerId(String customerId);

    Map<String, Object> findAllEventCacheStartWithByCustomerId(String customerId);

    void deleteById(String id);
}