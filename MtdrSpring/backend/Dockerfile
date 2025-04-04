# ───── STAGE 1: Build React frontend ─────
FROM node:16 as frontend-builder
WORKDIR /app/frontend
COPY src/main/frontend/ ./
RUN npm install && npm run build

# ───── STAGE 2: Build Spring Boot backend ─────
FROM maven:3.8.5-openjdk-17 as backend-builder
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# ───── STAGE 3: Final image ─────
FROM openjdk:22
WORKDIR /app

# ✅ Copy built JAR
COPY --from=backend-builder /app/target/MyTodoList-0.0.1-SNAPSHOT.jar MyTodoList.jar

# ✅ Copy static frontend build
COPY --from=frontend-builder /app/frontend/build /app/src/main/resources/static

# ✅ Copy Oracle wallet into container
COPY wallet_production /etc/wallet

# ✅ Expose port
EXPOSE 8080

# ✅ Run Spring Boot app
ENTRYPOINT ["java", "-jar", "MyTodoList.jar"]
