# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test --no-daemon

# Add debug command to see what files are generated
RUN ls -la build/libs/

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar ./

# Add debug command to verify the JAR exists
RUN ls -la /app/

VOLUME /root/.config/gcloud

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE ${PORT}
ENTRYPOINT ["java", \
            "-Dserver.port=${PORT}", \
            "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "/app/app.jar"]