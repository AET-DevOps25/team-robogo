# ---- Build Stage ----
FROM gradle:8.7.0-jdk17 AS build

LABEL maintainer="Team Robogo"
LABEL version="1.0"
LABEL description="Team Robogo Server"
LABEL org.opencontainers.image.source="https://github.com/AET-DevOps25/team-robogo"

WORKDIR /app

COPY . .

RUN gradle clean build -x test --no-daemon

# ---- Runtime Stage ----
FROM gcr.io/distroless/java17-debian12
WORKDIR /app
COPY --from=build /app/server/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
