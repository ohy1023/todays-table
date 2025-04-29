package store.myproject.onlineshop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class RedisServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @InjectMocks
    private RedisService redisService;

    @Test
    @DisplayName("Redis 값 저장 성공")
    void set_values_success() {
        // given
        String key = "testKey";
        String value = "testValue";
        long timeout = 10L;
        TimeUnit unit = TimeUnit.SECONDS;

        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);

        // when
        redisService.setValues(key, value, timeout, unit);

        // then
        then(valueOperations).should(times(1)).set(key, value, timeout, unit);
    }

    @Test
    @DisplayName("Redis 값 조회 성공")
    void get_values_success() {
        // given
        String key = "testKey";
        String expectedValue = "testValue";

        given(redisTemplate.opsForValue())
                .willReturn(valueOperations);
        given(valueOperations.get(key))
                .willReturn(expectedValue);

        // when
        String result = redisService.getValues(key);

        // then
        then(valueOperations).should(times(1)).get(key);
        // 추가로 결과 값 검증 (optional)
        org.assertj.core.api.Assertions.assertThat(result).isEqualTo(expectedValue);
    }

    @Test
    @DisplayName("Redis 값 삭제 성공")
    void delete_values_success() {
        // given
        String key = "testKey";

        // when
        redisService.deleteValues(key);

        // then
        then(redisTemplate).should(times(1)).delete(key);
    }
}
