# 1. 빌드 스테이지
# Gradle 8.x 이상 버전이 Java 21 빌드를 안정적으로 지원합니다.
FROM gradle:8.5-jdk21-alpine AS builder
WORKDIR /build

# 의존성 캐싱을 위해 설정 파일만 먼저 복사
COPY build.gradle settings.gradle /build/
RUN gradle build -x test --parallel --continue > /dev/null 2>&1 || true

# 전체 소스 복사 및 빌드
COPY . /build
RUN gradle build -x test --parallel

# 2. 실행 스테이지
# 실행 환경도 Java 21로 변경 (Alpine은 경량화에 유리, 정밀한 보안이 필요하면 slim 추천)
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# 빌더 스테이지에서 생성된 jar 복사 (이름이 고정되도록 명시)
COPY --from=builder /build/build/libs/*.jar app.jar

# 포트 설정
EXPOSE 8080

# 보안을 위해 비루트(Non-root) 사용자 사용
# alpine 이미지의 경우 'nobody' 사용 가능
USER nobody

# 실행 옵션 최적화
ENTRYPOINT [ \
    "java", \
    "-jar", \
    "-Dserver.port=8080", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-Dsun.net.inetaddr.ttl=0", \
    "-Duser.timezone=Asia/Seoul", \
    "app.jar" \
]