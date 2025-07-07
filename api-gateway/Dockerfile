# ---- Build Stage ----
FROM gradle:8.14.2-jdk17 AS build

LABEL maintainer="Team Robogo"
LABEL version="1.0"
LABEL description="Team Robogo API Gateway"
LABEL org.opencontainers.image.source="https://github.com/AET-DevOps25/team-robogo"

WORKDIR /app

# First copy gradle wrapper files
COPY gradle/ ./gradle/
COPY gradlew ./
COPY gradlew.bat ./
COPY gradle.properties ./
COPY settings.gradle ./

# Make gradlew executable
RUN chmod +x gradlew

# Copy project files
COPY . .

# Set up proper network configuration for Gradle
RUN ./gradlew clean build -x test --no-daemon --stacktrace --info


# ---- Runtime Stage ----
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/api-gateway/build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
