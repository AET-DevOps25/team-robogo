# ---- Build Stage ----
FROM gradle:8.14.2-jdk17 AS build

LABEL maintainer="Team Robogo"
LABEL version="1.0"
LABEL description="Team Robogo Server"
LABEL org.opencontainers.image.source="https://github.com/AET-DevOps25/team-robogo"

WORKDIR /app

COPY . .

# Set up proper network configuration for Gradle
RUN ./gradlew clean build -x test --no-daemon -Dhttps.protocols=TLSv1.1,TLSv1.2 --stacktrace --info \
    -Dhttps.proxyHost=http.docker.internal -Dhttps.proxyPort=3128 \
    -Dhttp.proxyHost=http.docker.internal -Dhttp.proxyPort=3128

# ---- Runtime Stage ----
FROM gcr.io/distroless/java17-debian12
WORKDIR /app
COPY --from=build /app/server/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
