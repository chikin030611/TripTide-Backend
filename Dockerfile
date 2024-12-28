# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build --no-daemon

# Run stage
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/build/libs/triptide-backend-0.0.1-SNAPSHOT.jar app.jar
ENV PORT=8080
EXPOSE ${PORT}
ENTRYPOINT exec java -Dserver.port=${PORT} -jar app.jar