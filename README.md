

# oci-react-samples

[![Backend + Frontend CI](https://github.com/your-org/oci-react-samples/workflows/Backend%20+%20Frontend%20CI/badge.svg)](#) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A **cloud-native** ToDo application (“MyTodoList”) demonstrating:

- **Frontend**: React.js (Vite + TypeScript)  
- **Backend**: Spring Boot 3 (Java 17) + Oracle Database  
- **Auth**: Clerk (JWT-based session management)  
- **Bot**: Telegram integration using `TelegramLongPollingBot`  
- **Infra & CI/CD**: Docker, GitHub Actions, OCI DevOps, Testcontainers  

---

## Table of Contents

1. [Architecture](#architecture)  
2. [Prerequisites](#prerequisites)  
3. [Setup & Local Development](#setup--local-development)  
   - [Environment Variables](#environment-variables)  
   - [Back-end](#running-the-back-end)  
   - [Front-end](#running-the-front-end)  
   - [Docker Compose](#docker-compose)  
4. [Testing](#testing)  
5. [CI/CD](#cicd)  
6. [Git Hooks & Scripts](#git-hooks--scripts)  
7. [Platform Notes](#platform-notes)  
8. [Contributing](#contributing)  
9. [License](#license)  

---

## Architecture

![Architecture Overview](./docs/architecture.drawio.png)

1. **API Gateway**  
   - Centralizes Clerk-based JWT validation (via `AuthService`)  
   - Routes to microservices (`TaskService`, `SubTaskService`, etc.)  
2. **Spring Boot Services**  
   - **TaskService**, **SubTaskService**, **SprintService**, **KpiService**, **UsuarioService**, **ToDoItemService**  
   - Each follows Controller → Service → Repository layering  
3. **React Frontend**  
   - Consumes REST endpoints; uses Clerk for authentication  
4. **Telegram Bot**  
   - Implements conversational flows via `ToDoItemBotController`  
5. **Database**  
   - Oracle DB (free edition for dev/testing, Testcontainers for integration tests)  

---

## Prerequisites

- **Java 17** (GraalVM EE for native-image optional)  
- **Maven**  
- **Node.js** ≥ 16 & **npm**  
- **Docker Desktop** (or Linux Docker)  
- **OCI CLI** (for deployments)  

---

## Setup & Local Development

### Environment Variables

Create a `.env` (or export) with:

| Variable                 | Purpose                                           | Example                          |
|--------------------------|---------------------------------------------------|----------------------------------|
| `SPRING_PROFILES_ACTIVE` | Spring profile (`dev`/`test`/`prod`)              | `dev`                            |
| `DATABASE_URL`           | JDBC URL to Oracle DB                             | `jdbc:oracle:thin:@localhost:1521/ORCLPDB1` |
| `ORACLE_WALLET_PATH`     | Path to OCI Wallet directory                      | `./Wallet_FATDATABASE`           |
| `JWT_SECRET`             | HMAC secret for signing JWTs                      | `super-secret-change-me`         |
| `CLERK_API_KEY`          | Clerk service API key                             | `sk_test_abc123`                 |
| `TELEGRAM_BOT_TOKEN`     | Telegram Bot token                                | `123456:ABC-DEF…`               |

### Running the Back-end

```bash
# From repo root
cd MtdrSpring/backend

# Install & compile
mvn clean install

# Run with dev profile
SPRING_PROFILES_ACTIVE=dev \
java -jar target/MyTodoList-0.0.1-SNAPSHOT.jar
````

* **Health**: `GET http://localhost:8080/actuator/health`
* **Swagger UI**: `http://localhost:8080/swagger-ui.html`

### Running the Front-end

```bash
cd MtdrSpring/backend/src/main/frontend

# Install & start dev server
npm ci
npm start
```

* App available at **[http://localhost:3000](http://localhost:3000)** (proxy → backend on 8080)
* To build for production: `npm run build`

### Docker Compose

A single-command alternative (requires Docker):

```bash
# from repo root
docker-compose up --build
```

This spins up:

* Oracle DB container (with sample wallet)
* Spring Boot app on port **8080**
* React build served via Spring Boot

---

## Testing

* **Unit Tests**:

  ```bash
  cd MtdrSpring/backend
  mvn test
  ```
* **Integration Tests** (uses Testcontainers + real Oracle):

  ```bash
  mvn verify
  ```
* **Front-end Lint & Tests**:

  ```bash
  cd MtdrSpring/backend/src/main/frontend
  npm run lint
  npm test
  ```

---

## CI/CD

* **GitHub Actions**

  * Validates Maven build, runs backend tests, packages JAR
  * Runs ESLint, builds React app
  * Uploads artifacts for deployment
* **OCI DevOps Pipeline**

  * Builds Docker image, pushes to OCIR
  * Deploys to Kubernetes on OCI

Configuration lives in:

* `.github/workflows/build.yml`
* `oci_devops.yml`

---

## Git Hooks & Scripts

We provide ready-made hooks and helper scripts in `scripts/`:

| Script         | Purpose                            |
| -------------- | ---------------------------------- |
| `pre-commit`   | Spotless format, Checkstyle checks |
| `pre-push`     | Run full backend test suite        |
| `start-dev.sh` | Docker Compose up + DB migrations  |

**Install hooks:**

```bash
cp scripts/pre-commit .git/hooks/pre-commit && chmod +x .git/hooks/pre-commit
cp scripts/pre-push   .git/hooks/pre-push   && chmod +x .git/hooks/pre-push
```

---

## Platform Notes

| OS      | Notes                                                    |
| ------- | -------------------------------------------------------- |
| macOS   | Use Docker Desktop                                       |
| Windows | Enable WSL2 and run Docker in Linux mode                 |
| Linux   | Standard Docker setup; ensure `docker-compose` installed |

---

## Contributing

We welcome issues and pull requests! Please:

1. Fork the repo & create a feature branch
2. Write tests for new logic
3. Adhere to code style (Spotless, Checkstyle)
4. Submit a PR against `dev` branch

See [CONTRIBUTING.md](CONTRIBUTING.md) for full guidelines.

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

