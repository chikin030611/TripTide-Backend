# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/triptide-backend-0.0.1-SNAPSHOT.jar app.jar

# Default environment variables
ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
ENV JPA_SHOW_SQL=false

EXPOSE ${PORT}
ENTRYPOINT exec java -Dserver.port=${PORT} \
    -Dspring.profiles.active=${SPRING_PROFILES_ACTIVE} \
    -jar app.jar