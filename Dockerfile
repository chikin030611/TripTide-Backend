# Build stage
FROM gradle:8.5-jdk21 AS build
WORKDIR /app/

# Copy Gradle files first for better layer caching
COPY gradle/ gradle/
COPY gradlew gradlew.bat ./
COPY build.gradle settings.gradle ./

# Copy source code
COPY src/ src/

RUN gradle build --no-daemon -x test

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app/
COPY --from=build /app/build/libs/*.jar ./app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]