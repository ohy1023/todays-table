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
                        .description("오늘의 식탁은 레시피를 기반으로 사용자가 식재료를 쉽게 구매할 수 있도록 지원하는 백엔드 API 서비스입니다.\n\n"
                                + "### ADMIN 테스트 계정 정보\n"
                                + "Email: zvyg1023@naver.com\n"
                                + "Password: 1Q2w3e4r!!\n"
                                + "권한: ROLE_ADMIN\n\n"
                                + "### 주문 순서\n"
                                + "1. 단건 주문.\n"
                                + "2. 사전 검증.\n"
                                + "3. 사후 검증.\n"
                                + "### 주의 사항\n"
                                + "IMP_UID는 포트원에서 발급받은 고유 ID로, 프론트엔드에서 받아서 백엔드로 전달해야 합니다.\n"
                                + "이에 따라 IMP_UID가 사용되는 API(사후 검증, 주문 취소)는 테스트가 불가합니다.\n\n")
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