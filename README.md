# Setup Spring Boot + MySQL dengan Docker

## 1️⃣ Struktur Project

```
spring-boot-crud/
├─ src/
│  └─ main/
│      ├─ java/
│      └─ resources/
│          ├─ application.properties
│          └─ application-docker.properties
├─ build.gradle / pom.xml
├─ Dockerfile
└─ docker-compose.yml
```

---

## 2️⃣ Dockerfile

Buat Dockerfile di root project:

```dockerfile
# Gunakan OpenJDK 17
FROM openjdk:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy jar dari build
COPY build/libs/*.jar app.jar

# Jalankan Spring Boot
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

> Pastikan jar sudah di-build sebelum `docker build`.

---

## 3️⃣ Profile `docker` di Spring Boot

Buat file `application-docker.properties` di `src/main/resources`:

```properties
server.port=8080

spring.datasource.url=jdbc:mysql://mysql-db:3306/userdb
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.show-sql=false

spring.flyway.enabled=true
spring.flyway.baseline-on-migrate=true
spring.flyway.locations=classpath:db/migration

jwt.secret=XzQ7Bz5LJ9i6t8mN7yNp9tA+38oZTt7Zb7VcF5w4zQqE4Z1eHhXb+J7rSc+vQ8Qp
jwt.expirationMs=3600000
```

> Pastikan **JDBC URL menggunakan nama container MySQL (`mysql-db`)**, bukan `localhost`.

---

## 4️⃣ Docker Compose

Buat `docker-compose.yml` di root project:

```yaml
version: "3.8"

services:
  mysql-db:
    image: mysql:8.0.33
    container_name: mysql-db
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: userdb
    networks:
      - app-network
    volumes:
      - mysql-data:/var/lib/mysql
    ports:
      - "3308:3306"  # Optional, hanya jika ingin akses DB dari host

  spring-app:
    build: .
    image: spring-boot-crud:latest
    container_name: spring-boot-crud
    depends_on:
      - mysql-db
    networks:
      - app-network
    environment:
      SPRING_PROFILES_ACTIVE: docker
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql-db:3306/userdb
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: root
      JWT_SECRET: XzQ7Bz5LJ9i6t8mN7yNp9tA+38oZTt7Zb7VcF5w4zQqE4Z1eHhXb+J7rSc+vQ8Qp
    ports:
      - "8080:8080"  # Host port → akses Swagger/UI di browser

networks:
  app-network:
    driver: bridge

volumes:
  mysql-data:
```

> Host port 8081 bisa diganti jika ingin beberapa project jalan bersamaan.

---

## 5️⃣ Build jar

Sebelum build Docker image, build jar Spring Boot:

```bash
./gradlew clean bootJar
```

* Hasil jar berada di `build/libs/*.jar`
* Dockerfile akan menyalin jar ini ke container

---

## 6️⃣ Build dan jalankan Docker Compose

```bash
docker compose up -d --build
```

* `--build` → build image baru dari Dockerfile
* `-d` → run container di background

Cek container:

```bash
docker ps
```

* Pastikan `spring-boot-crud` dan `mysql-db` **Up**

---

## 7️⃣ Lihat logs Spring Boot

```bash
docker logs -f spring-boot-crud
```

* Pastikan Spring Boot **connect ke MySQL** dan **Tomcat start**

---

## 8️⃣ Akses Swagger UI

Buka browser:

```
http://localhost:8080/swagger-ui/index.html
```

* Sekarang bisa langsung tes semua endpoint API

---

## 9️⃣ Stop / restart container

* Stop semua container:

```bash
docker compose down
```

* Restart container (tanpa rebuild jar):

```bash
docker compose up -d
```

* Rebuild jika jar / kode berubah:

```bash
docker compose up -d --build
```
