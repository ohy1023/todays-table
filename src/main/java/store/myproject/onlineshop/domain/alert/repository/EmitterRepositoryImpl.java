package store.myproject.onlineshop.domain.alert.repository;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * {@link EmitterRepository}의 구현체로 {@link ConcurrentHashMap}을 사용합니다.
 */
@Repository
@NoArgsConstructor
public class EmitterRepositoryImpl implements EmitterRepository {

    // 고유한 ID를 가진 SseEmitter를 저장하는 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 고유한 ID를 가진 이벤트 캐시를 저장하는 맵
    private final Map<String, Object> eventCache = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {
        emitters.put(emitterId, sseEmitter);
        return sseEmitter;
    }

    @Override
    public void saveEventCache(String eventCacheId, Object event) {
        eventCache.put(eventCacheId, event);
    }

    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByCustomerId(String customerId) {
        // customerId로 시작하는 ID를 가진 emitters를 필터링하고 수집합니다.
        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(customerId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Map<String, Object> findAllEventCacheStartWithByCustomerId(String customerId) {
        // customerId로 시작하는 ID를 가진 event cache를 필터링하고 수집합니다.
        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(customerId))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void deleteById(String id) {
        emitters.remove(id);
    }

    @Override
    public void deleteAllEmitterStartWithId(String memberId) {
        // memberId로 시작하는 ID를 가진 emitters를 제거합니다.
        emitters.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        emitters.remove(key);
                    }
                }
        );
    }

    @Override
    public void deleteAllEventCacheStartWithId(String memberId) {
        // memberId로 시작하는 ID를 가진 event cache를 제거합니다.
        eventCache.forEach(
                (key, emitter) -> {
                    if (key.startsWith(memberId)) {
                        eventCache.remove(key);
                    }
                }
        );
    }
}
