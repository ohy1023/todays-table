package store.myproject.onlineshop.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@EnableAsync
@Configuration
public class AsyncConfiguration {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(15);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("Mail-Executor-");

        executor.initialize();
        return executor;
    }

    @Bean(name = "recipeMetaExecutor")
    public Executor recipeMetaExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(800);
        executor.setThreadNamePrefix("Recipe-Meta-Executor-");

        executor.initialize();
        return executor;
    }

    @Bean(name = "monthlyPurchaseExecutor")
    public Executor monthlyPurchaseExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(4);
        executor.setMaxPoolSize(20);
        executor.setQueueCapacity(800);
        executor.setThreadNamePrefix("Monthly-Purchase-Executor-");

        executor.initialize();
        return executor;
    }

    @Bean
    public AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            log.error("비동기 작업 중 예외 발생!", ex);
            log.error("실행 중 예외 발생 메서드: {}", method.getName());
        };
    }
}
