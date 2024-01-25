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
import org.springframework.http.MediaType;


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
                        .title(" 쇼핑몰 ")
                        .description(" 연습용 프로젝트 ")
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