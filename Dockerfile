FROM gradle:8.8-jdk21 AS builder

WORKDIR /app

COPY build.gradle.kts settings.gradle.kts gradle.properties ./
COPY gradle ./gradle
COPY gradlew ./
COPY gradlew.bat ./
COPY src ./src

RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=builder /app/build/libs/*-all.jar app.jar

RUN mkdir -p /app/data

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]