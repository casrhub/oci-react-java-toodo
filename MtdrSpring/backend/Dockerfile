FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /app
COPY . .
RUN mvn clean package spring-boot:repackage -DskipTests
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=builder /app/target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar
ENV TNS_ADMIN=/app/src/main/resources/wallet
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
