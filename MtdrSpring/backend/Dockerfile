# ───── Stage 1: Build with Maven ─────
FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app

# Copy only the Maven project (backend folder)
COPY . .

# Build the Spring Boot app (adjust if you want tests)
RUN mvn clean package spring-boot:repackage -DskipTests

# ───── Stage 2: Runtime ─────
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy only the final JAR
COPY --from=builder /app/target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

ENV TNS_ADMIN=/app/src/main/resources/wallet

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
