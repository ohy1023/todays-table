package store.myproject.onlineshop.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Swagger springdoc-ui 설정
 */
@Configuration
public class SwaggerConfiguration {

    String defaultHeader = "Authorization";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .addServersItem(new Server().url("/"))
                .info(new Info()
                        .title("오늘의 식탁 ")
                        .description("오늘의 식탁은 레시피를 기반으로 사용자가 식재료를 쉽게 구매할 수 있도록 지원하는 백엔드 API 서비스입니다.")
                        .version(" 1.0.0 ")
                )
                .addSecurityItem(new SecurityRequirement().addList(defaultHeader))
                .components(new Components().addSecuritySchemes(defaultHeader,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name(defaultHeader)));

    }

    @Bean
    public GroupedOpenApi shopOpenApi() {

        String paths[] = {"/api/**"};

        return GroupedOpenApi.builder()
                .group("shop OpenAPI 1.0.0")
                .pathsToMatch(paths)
                .build();
    }
}