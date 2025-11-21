# 1단계: Gradle로 빌드
FROM gradle:7.6.2-jdk17 AS builder
WORKDIR /app

# Gradle 캐시 활용을 위해 먼저 빌드 파일만 복사
COPY build.gradle settings.gradle ./
COPY gradle gradle

# 의존성만 먼저 다운로드
RUN gradle dependencies --no-daemon || true

# 전체 프로젝트 복사
COPY . .

# jar 빌드
RUN gradle clean bootJar --no-daemon

# 2단계: JDK 17 이미지에서 실행
FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app

# 빌드 산출물 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 컨테이너 포트
EXPOSE 8080

# 실행 명령
ENTRYPOINT ["java", "-jar", "app.jar"]
