# .jar 파일 생성
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./
COPY src src

RUN chmod +x gradlew
#RUN ./gradlew bootJar
RUN rm -f build/libs/*.jar && ./gradlew bootJar

# .jar 실행
FROM eclipse-temurin:21-jdk
WORKDIR /app
ARG JAR_FILE=build/libs/wearly-0.0.1-SNAPSHOT.jar
COPY --from=build /app/${JAR_FILE} app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]