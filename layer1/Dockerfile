# Build context is the companion/ root so the Gradle wrapper and settings are accessible.
# Run from layer1/: docker compose up --build
FROM eclipse-temurin:21-jdk-alpine AS build
WORKDIR /build

COPY gradlew gradlew.bat ./
COPY gradle ./gradle
RUN chmod +x ./gradlew

COPY settings.gradle.kts build.gradle.kts ./
COPY layer1/build.gradle.kts layer1/
RUN ./gradlew :layer1:dependencies --no-daemon -q

COPY layer1/src layer1/src
RUN ./gradlew :layer1:bootJar --no-daemon -q

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /build/layer1/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
