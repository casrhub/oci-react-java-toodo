# Dockerfile
FROM openjdk:8-jdk-alpine
WORKDIR /app

# Copy the JAR file
COPY target/MyTodoList-0.0.1-SNAPSHOT.jar app.jar

# Create wallet directory in the correct location for the application
RUN mkdir -p /app/src/main/resources/wallet
COPY Wallet_FATDATABASE/cwallet.sso /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/ewallet.p12 /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/ewallet.pem /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/keystore.jks /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/ojdbc.properties /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/sqlnet.ora /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/tnsnames.ora /app/src/main/resources/wallet/
COPY Wallet_FATDATABASE/truststore.jks /app/src/main/resources/wallet/

# Set TNS_ADMIN environment variable
ENV TNS_ADMIN=/app/src/main/resources/wallet

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
