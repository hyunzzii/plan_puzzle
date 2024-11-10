# 빌드 스테이지
FROM gradle:7.5.1-jdk17 AS build

# 작업 디렉토리 생성
WORKDIR /app

# 소스 복사
COPY --chown=gradle:gradle . .

# 의존성 설치 (의존성 캐싱)
RUN gradle dependencies --no-daemon

# 테스트 및 빌드 실행
RUN gradle clean test build --no-daemon

# 패키징 스테이지
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app

# 빌드된 jar 파일 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]