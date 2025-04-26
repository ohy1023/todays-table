package store.myproject.onlineshop.repository.alert;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class EmitterRepositoryImplTest {

    private final EmitterRepositoryImpl emitterRepository = new EmitterRepositoryImpl();

    @Nested
    @DisplayName("Emitter 저장 테스트")
    class Save {

        @Test
        @DisplayName("Emitter 저장 성공")
        void save_emitter_success() {
            // given
            String emitterId = "customer123";
            SseEmitter emitter = new SseEmitter();

            // when
            SseEmitter savedEmitter = emitterRepository.save(emitterId, emitter);

            // then
            assertThat(savedEmitter).isNotNull();
            assertThat(emitterRepository.findAllEmitterStartWithByCustomerId("customer123")).containsKey(emitterId);
        }

        @Test
        @DisplayName("EventCache 저장 성공")
        void save_event_cache_success() {
            // given
            String eventCacheId = "customer123_event";
            String event = "test-event";

            // when
            emitterRepository.saveEventCache(eventCacheId, event);

            // then
            assertThat(emitterRepository.findAllEventCacheStartWithByCustomerId("customer123")).containsKey(eventCacheId);
        }
    }

    @Nested
    @DisplayName("Emitter 저장 테스트")
    class FindAll {

        @Test
        @DisplayName("CustomerId로 Emitter 조회 성공")
        void find_all_emitter_start_with_by_customer_id_success() {
            // given
            String emitterId1 = "customer123-1";
            String emitterId2 = "customer123-2";
            emitterRepository.save(emitterId1, new SseEmitter());
            emitterRepository.save(emitterId2, new SseEmitter());

            // when
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByCustomerId("customer123");

            // then
            assertThat(emitters).hasSize(2);
            assertThat(emitters).containsKeys(emitterId1, emitterId2);
        }

        @Test
        @DisplayName("CustomerId로 EventCache 조회 성공")
        void find_all_event_cache_start_with_by_customer_id_success() {
            // given
            String eventCacheId1 = "customer123-1";
            String eventCacheId2 = "customer123-2";
            emitterRepository.saveEventCache(eventCacheId1, "event1");
            emitterRepository.saveEventCache(eventCacheId2, "event2");

            // when
            Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByCustomerId("customer123");

            // then
            assertThat(eventCaches).hasSize(2);
            assertThat(eventCaches).containsKeys(eventCacheId1, eventCacheId2);
        }
    }

    @Nested
    @DisplayName("Emitter 삭제 테스트")
    class Delete {

        @Test
        @DisplayName("Emitter 삭제 성공")
        void delete_emitter_by_id_success() {
            // given
            String emitterId = "customer123-1";
            emitterRepository.save(emitterId, new SseEmitter());

            // when
            emitterRepository.deleteById(emitterId);

            // then
            Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByCustomerId("customer123");
            assertThat(emitters).doesNotContainKey(emitterId);
        }
    }
}
