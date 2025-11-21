# 1. Gradle 빌드 스테이지
FROM gradle:8.5-jdk17-alpine AS builder
WORKDIR /app

COPY . .
RUN ./gradlew clean bootJar --no-daemon

# 2. 런타임 스테이지
FROM eclipse-temurin:17-jdk-alpine

WORKDIR /app

# 빌드된 jar 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 포트 오픈
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-jar", "app.jar"]
