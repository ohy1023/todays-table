package store.myproject.onlineshop.global.config.db.redis;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RedissonConfig {

    @Value("${spring.data.redis.cache.host}")
    private String redisHost;

    @Value("${spring.data.redis.cache.port}")
    private int redisPort;

    @Value("${spring.data.redis.cache.password:}")
    private String redisPassword;

    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        Config config = new Config();
        var serverConfig = config.useSingleServer()
                .setAddress(REDISSON_HOST_PREFIX + redisHost + ":" + redisPort);

        // 패스워드가 비어있지 않은 경우에만 설정
        if (redisPassword != null && !redisPassword.isBlank()) {
            serverConfig.setPassword(redisPassword);
        }

        return Redisson.create(config);
    }
}
