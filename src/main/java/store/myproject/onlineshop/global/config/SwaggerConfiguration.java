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
                                + "### 🔐 ADMIN 테스트 계정 정보\n"
                                + "- Email: zvyg1023@naver.com\n"
                                + "- Password: 1Q2w3e4r!!\n"
                                + "- 권한: ROLE_ADMIN\n\n"
                                + "### 🛒 주문 흐름\n"
                                + "1. 단건 주문\n"
                                + "2. 사전 검증\n"
                                + "3. 사후 검증\n\n"
                                + "### 🔑 인증 방식 (JWT)\n"
                                + "- 로그인 후 응답받은 토큰(Access Token)을 사용합니다.\n"
                                + "- Swagger UI 우측 상단의 Authorize 버튼을 눌러,\n"
                                + "  `Bearer` 접두어 없이 순수 토큰 값만 입력하세요.\n"
                                + "  예시: `eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...`\n\n"
                                + "### ⚠️ 테스트 시 유의 사항\n"
                                + "- IMP_UID는 포트원(PortOne)에서 프론트엔드가 받아 백엔드로 전달해야 합니다.\n"
                                + "- 따라서 IMP_UID가 필요한 API(사후 검증, 주문 취소)는 Swagger에서 직접 테스트할 수 없습니다.\n\n"
                                + "### 🚨 속도 관련 안내\n"
                                + "- 현재 본 프로젝트는 **Cloudtype 무료 배포 환경**에서 서비스되고 있어,\n"
                                + "  Swagger UI의 초기 로딩 및 응답 속도가 느릴 수 있습니다.\n")
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