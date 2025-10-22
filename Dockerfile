# Stage 1: build jar
FROM gradle:7.6-jdk17 AS build
WORKDIR /app
COPY . .
RUN gradle clean bootJar

# Stage 2: runtime image
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
