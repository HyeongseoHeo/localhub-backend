# 1단계: Gradle Wrapper로 빌드하는 스테이지
FROM gradle:8.5-jdk17-alpine AS builder

WORKDIR /app

# 프로젝트 전체 복사
COPY . .

# gradlew 실행 권한 부여
RUN chmod +x ./gradlew

# bootJar 빌드
RUN ./gradlew clean bootJar --no-daemon

# ----------------------------------------------------

# 2단계: 실행 이미지
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# builder에서 jar 파일 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 개방
EXPOSE 8080

# Spring Boot 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
