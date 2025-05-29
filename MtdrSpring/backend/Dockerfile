# Stage 1: Use Java 11 + Maven to build the app
FROM maven:3.8.6-openjdk-11 AS builder

WORKDIR /app
COPY . .

# Build the Spring Boot JAR
RUN mvn -f MtdrSpring/backend/pom.xml clean package spring-boot:repackage -DskipTests

# Stage 2: Use smaller base image with only JRE to run the app
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy the JAR from the builder stage
COPY --from=builder /app/MtdrSpring/backend/target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

# Wallet files
COPY MtdrSpring/backend/Wallet_FATDATABASE /app/src/main/resources/wallet/

# Set Oracle wallet path
ENV TNS_ADMIN=/app/src/main/resources/wallet

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
