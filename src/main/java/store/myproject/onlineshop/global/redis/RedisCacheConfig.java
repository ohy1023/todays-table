package store.myproject.onlineshop.global.redis;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

import static org.springframework.data.redis.serializer.RedisSerializationContext.SerializationPair.fromSerializer;

@EnableCaching
@Configuration
public class RedisCacheConfig {

    @Value("${spring.data.redis.cache.host}")
    private String redisHost;

    @Value("${spring.data.redis.cache.port}")
    private int redisPort;

    // redis 연결 정보를 통해 캐싱 기능을 연결시킬 Bean
    @Bean(name = "redisCacheConnectionFactory")
    public RedisConnectionFactory redisCacheConnectionFactory() {

        RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setPort(redisPort);
        redisStandaloneConfiguration.setHostName(redisHost);
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    // 설정 객체 default 설정 -- key/value를 어떻게 직렬화해서 redis에 저장할지를 정의함
    private RedisCacheConfiguration defaultCacheConfiguration() {
        return RedisCacheConfiguration
                .defaultCacheConfig()
                .disableCachingNullValues()
                .serializeKeysWith(fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper())))
                .entryTtl(Duration.ofDays(1L));
    }

    // 캐시에서 redis 사용하기 위한 Bean
    @Bean
    public CacheManager redisCacheManager(@Qualifier("redisCacheConnectionFactory") RedisConnectionFactory connectionFactory) {
        return RedisCacheManager
                .RedisCacheManagerBuilder
                .fromConnectionFactory(connectionFactory)    // connection 적용
                .cacheDefaults(defaultCacheConfiguration())  //  캐시 설정 적용
                .build();
    }

    private ObjectMapper objectMapper() {

        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                .allowIfSubType(Object.class)
                .build();

        return new ObjectMapper()
                .findAndRegisterModules()
                .enable(SerializationFeature.INDENT_OUTPUT)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule())
                .activateDefaultTyping(typeValidator, ObjectMapper.DefaultTyping.NON_FINAL);
    }

}
