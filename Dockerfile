# Gunakan Java 17 slim runtime
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copy jar hasil build
COPY build/libs/app.jar app.jar

# Expose port Spring Boot
EXPOSE 8080

# Jalankan dengan profile docker
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=docker"]
