package store.myproject.onlineshop.global.config.db.redis;

import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import tools.jackson.databind.ObjectMapper;

/**
 * Jackson 3 (tools.jackson) 전용 Redis Serializer
 */
public class Jackson3RedisSerializer<T> implements RedisSerializer<T> {

    private final ObjectMapper objectMapper;

    public Jackson3RedisSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] serialize(T t) throws SerializationException {
        if (t == null) {
            return new byte[0];
        }
        try {
            return objectMapper.writeValueAsBytes(t);
        } catch (Exception ex) {
            throw new SerializationException("Jackson 3 직렬화 에러: " + ex.getMessage(), ex);
        }
    }

    @Override
    public T deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            // Object.class로 읽어오면 objectMapper 설정(DefaultTyping)에 따라 
            // JSON 내 @class 정보를 보고 원래 타입으로 변환됩니다.
            return (T) objectMapper.readValue(bytes, Object.class);
        } catch (Exception ex) {
            throw new SerializationException("Jackson 3 역직렬화 에러: " + ex.getMessage(), ex);
        }
    }
}