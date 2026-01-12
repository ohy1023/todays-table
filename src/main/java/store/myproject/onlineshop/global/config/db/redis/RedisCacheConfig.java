package store.myproject.onlineshop.global.config.db.redis;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import tools.jackson.databind.*;
import tools.jackson.databind.json.JsonMapper;
import tools.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import tools.jackson.databind.jsontype.PolymorphicTypeValidator;
import tools.jackson.datatype.jsr310.JavaTimeModule;

import java.time.Duration;

@Configuration
public class RedisCacheConfig {


    @Value("${spring.data.redis.cache.host}")
    private String redisHost;

    @Value("${spring.data.redis.cache.port}")
    private int redisPort;

    @Value("${spring.data.redis.cache.password}")
    private String redisPassword;

    @Bean(name = "redisConnectionFactory")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisHost);
        serverConfig.setPort(redisPort);

        if (redisPassword != null && !redisPassword.isBlank()) {
            serverConfig.setPassword(redisPassword);
        }

        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .shutdownTimeout(Duration.ofMillis(100))
                .build();

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }


    @Bean(name = "cacheRedisTemplate")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("redisConnectionFactory") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory);

        // Jackson 3 기반 Serializer
        Jackson3RedisSerializer<Object> jsonSerializer = new Jackson3RedisSerializer<>(redisObjectMapper());

        template.setKeySerializer(RedisSerializer.string());
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(RedisSerializer.string());
        template.setHashValueSerializer(jsonSerializer);
        template.setDefaultSerializer(jsonSerializer);

        template.afterPropertiesSet();

        return template;
    }

    @Bean(name = "redisObjectMapper")
    public ObjectMapper redisObjectMapper() {
        // 애플리케이션 패키지만 허용하는 보안 강화 타입 검증기
        PolymorphicTypeValidator typeValidator = BasicPolymorphicTypeValidator.builder()
                // 애플리케이션 도메인 패키지
                .allowIfSubType("store.myproject.onlineshop.domain")
                .allowIfSubType("store.myproject.onlineshop.dto")
                // Java 표준 타입
                .allowIfSubType("java.util.List")
                .allowIfSubType("java.util.ArrayList")
                .allowIfSubType("java.util.LinkedList")
                .allowIfSubType("java.util.Map")
                .allowIfSubType("java.util.HashMap")
                .allowIfSubType("java.util.LinkedHashMap")
                .allowIfSubType("java.util.Set")
                .allowIfSubType("java.util.HashSet")
                .allowIfSubType("java.time")
                .allowIfSubType("java.lang.String")
                .allowIfSubType("java.lang.Number")
                .allowIfSubType("java.lang.Boolean")
                .allowIfSubType("java.util.UUID")
                .build();

        return JsonMapper.builder()
                // Java 8 날짜/시간 지원
                .addModule(new JavaTimeModule())

                // 역직렬화 설정
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)

                // 직렬화 설정
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.INDENT_OUTPUT)

                // 속성 정렬로 일관된 직렬화
                .enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY)

                // 타입 정보 포함
                .activateDefaultTyping(
                        typeValidator,
                        DefaultTyping.NON_FINAL,
                        JsonTypeInfo.As.PROPERTY
                )

                .build();
    }

}
