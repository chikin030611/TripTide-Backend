# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon
RUN ls -la build/libs/

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
# Copy and rename the JAR file to a known name
COPY --from=build /app/build/libs/triptide-backend-0.0.1-SNAPSHOT.jar app.jar

VOLUME /root/.config/gcloud

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE ${PORT}
ENTRYPOINT ["java", \
            "-Dserver.port=${PORT}", \
            "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
            "-jar", "app.jar"] 