package store.myproject.onlineshop.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/sse/**")
                .allowedOrigins("http://localhost:3000") // 클라이언트 주소에 맞게 수정
                .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}
