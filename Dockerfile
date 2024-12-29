FROM gradle:8.5-jdk21 AS build
WORKDIR /app
COPY . .
RUN gradle build -x test

# Run stage
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/triptide-backend-0.0.1-SNAPSHOT.jar app.jar

ENV PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod
EXPOSE ${PORT}
ENTRYPOINT ["java", \
            "-Dserver.port=${PORT}", \
            "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE}", \
            "-jar", "app.jar"]