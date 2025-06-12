# MyTodoList Spring Boot Application

## Prerequisites
- Java 17 or higher
- Maven
- Oracle Autonomous Database Wallet files

## Setup Instructions

### 1. Configure Database Connection
- Navigate to `backend/src/main/resources/`
- Copy the Oracle Autonomous Database wallet files to this directory
- Update `application.properties` with the correct wallet path:
  ```properties
  spring.datasource.url=jdbc:oracle:thin:@(description=(retry_count=20)(retry_delay=3)(address=(protocol=tcps)(port=1522)(host=adb.mx-queretaro-1.oraclecloud.com))(connect_data=(service_name=g13318455f68945_fatdatabase_high.adb.oraclecloud.com))(security=(ssl_server_dn_match=yes)))
  spring.datasource.username=ADMIN
  spring.datasource.password=your_password
  ```

### 2. Run the Application Locally (Maven)
```bash
# Navigate to the backend directory
cd backend

# Run the Spring Boot application
mvn spring-boot:run
```

### 3. Run the Application with Docker
Make sure you've built the Docker image using `./build.sh` or manually.

```bash
# Run the Docker image
docker run -p 8080:8080 casrhub/todolistapp-springboot:0.1
```

### 4. Access the Application
- Open in browser: [http://localhost:8080](http://localhost:8080)

## Important Notes
- Make sure the wallet files are properly placed in the `backend/src/main/resources/` directory or mounted correctly in Docker/Kubernetes
- The `application.properties` file contains sensitive information and should **not be committed** to version control
- For production deployment, use environment variables or a secure configuration management system (like Kubernetes secrets)

## Development
- The application uses **Spring Boot** for the backend
- **React** is used for the frontend
- **Oracle UCP** (Universal Connection Pool) is used for managing DB connections

## Troubleshooting
If you encounter connection issues:
1. Verify the wallet files are in the correct location
2. Check the database credentials in `application.properties`
3. Ensure the database service is running and accessible
4. Review logs after running `./build.sh` or running the container with Docker


