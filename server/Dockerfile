# ---- Build Stage ----
FROM gradle:8.7.0-jdk17 AS build

LABEL maintainer="Team Robogo"
LABEL version="1.0"
LABEL description="Team Robogo Server"
LABEL org.opencontainers.image.source="https://github.com/AET-DevOps25/team-robogo"

WORKDIR /app

COPY gradle.properties .
COPY settings.gradle .
COPY build.gradle .

COPY server/src ./server/src
COPY ../core/src ./core/src


RUN ./gradlew clean build -x test --no-daemon

# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/server/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
