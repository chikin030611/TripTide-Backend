# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar /app/app.jar/
VOLUME /root/.config/gcloud

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]