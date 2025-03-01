# 🛒 오늘의 식탁 – 레시피 기반 쇼핑 API

오늘의 식탁은 레시피를 기반으로 식재료를 쉽게 구매할 수 있도록 지원하는 백엔드 API 서비스입니다.

## 주요 기능

## 기술 스택 🛠

**언어 / 툴**

- Java 17
- Intellij Ultimate
- Gradle

**Backend**

- Spring Boot 3.0.6
- Spring Data JPA
- Spring Security
- QueryDsl
- MySQL
- Redis

**배포**

- Git Actions
- Docker
- Docker-Compose
- AWS EC2

## 아키텍처 📃

![](img/today_table_architecture.png)


## API 명세서 📡

- [Swagger](http://mystudyproject.store:8080/swagger-ui/index.html)

## ERD 🗄️

![](img/today_table_erd.png)

## Trouble Shooting 🚧

[Git Actions와 docker-compose를 이용한 자동 배포](https://velog.io/@zvyg1023/CICD-Docker-Github-Action-Spring-Boot)

[@RequestPart를 이용한 MultipartFile, DTO 처리 및 테스트](https://velog.io/@zvyg1023/Spring-Boot-RequestPart%EB%A5%BC-%EC%9D%B4%EC%9A%A9%ED%95%9C-MultipartFile-DTO-%EC%B2%98%EB%A6%AC-%EB%B0%8F-%ED%85%8C%EC%8A%A4%ED%8A%B8)

[Swagger에서 @ReqeustPart를 사용하여 MultiPartFile과 DTO 처리 시 Content type 'application/octet-stream' not supported 오류 해결](https://velog.io/@zvyg1023/Spring-Boot-Swagger%EC%97%90%EC%84%9C-ReqeustPart%EB%A5%BC-%EC%82%AC%EC%9A%A9%ED%95%98%EC%97%AC-MultiPartFile%EA%B3%BC-DTO-%EC%B2%98%EB%A6%AC-%EC%8B%9C-Content-type-applicationoctet-stream-not-supported-%EC%98%A4%EB%A5%98-%ED%95%B4%EA%B2%B0)

[MySQL 데이터 분산 처리를 위한 Master-Slave 이중화 구축](https://velog.io/@zvyg1023/mysql-master-slave)

[MySQL 데이터 분산 처리를 위한 Master-Slave 이중화 구축 (Spring Boot, JPA 설정)](https://velog.io/@zvyg1023/spring-boot-mysql-master-slave)

[Docker Volume으로 인해 MySQL 컨테이너 재실행시 스키마 안 생기는 이슈 해결](https://velog.io/@zvyg1023/docker-volume-schema-issue)