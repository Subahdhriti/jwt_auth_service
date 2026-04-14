# ---------- BUILD STAGE ----------
FROM gradle:8.5-jdk21 AS build

WORKDIR /app

COPY . .

RUN ./gradlew clean build -x test

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]