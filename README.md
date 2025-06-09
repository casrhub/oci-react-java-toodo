
# oci-react-samples

[![Backend + Frontend CI](https://github.com/your-org/oci-react-samples/workflows/Backend%20+%20Frontend%20CI/badge.svg)](#) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A **cloud-native** ToDo application (“MyTodoList”) demonstrating:

- **Frontend**: React.js (Vite + TypeScript)  
- **Backend**: Spring Boot 3 (Java 17) + Oracle Database  
- **Auth**: Clerk (JWT-based session management)  
- **Bot**: Telegram integration via `TelegramLongPollingBot`  
- **Infra & CI/CD**: Docker, GitHub Actions, OCI DevOps, Testcontainers  

---

## Table of Contents

1. [Architecture](#architecture)  
2. [Prerequisites](#prerequisites)  
3. [Setup & Local Development](#setup--local-development)  
   - [Environment Variables](#environment-variables)  
   - [Running the Back-end](#running-the-back-end)  
   - [Running the Front-end](#running-the-front-end)  
   - [Docker Compose](#docker-compose)  
4. [Testing](#testing)  
   - [Unit Tests](#unit-tests)  
   - [Integration Tests](#integration-tests)  
   - [Front-end Lint & Tests](#front-end-lint--tests)  
   - [End-to-End Selenium Tests](#end-to-end-selenium-tests)  
5. [CI/CD](#cicd)  
6. [Git Hooks & Scripts](#git-hooks--scripts)  
7. [Platform Notes](#platform-notes)  
8. [Contributing](#contributing)  
9. [License](#license)  

---

## Architecture

[[Link to Architecture Overview](https://drive.google.com/uc?export=view&id=1ezv0N4BDVg8ytpaWaS_nmzur3LcKrq20)](https://drive.google.com/file/d/1ezv0N4BDVg8ytpaWaS_nmzur3LcKrq20/view?usp=sharing)


1. **API Gateway**  
   - Centralized Clerk-based JWT validation via `AuthService`  
   - Routes requests to microservices (`TaskService`, `SubTaskService`, etc.)  
2. **Spring Boot Services**  
   - `TaskService`, `SubTaskService`, `SprintService`, `KpiService`, `UsuarioService`, `ToDoItemService`  
   - Each follows Controller → Service → Repository layering  
3. **React Frontend**  
   - Consumes REST endpoints; uses Clerk for authentication  
4. **Telegram Bot**  
   - Conversational flows implemented in `ToDoItemBotController`  
5. **Database**  
   - Oracle DB (free edition for dev/testing; Testcontainers for integration tests)  

---

## Prerequisites

- **Java 17** (GraalVM EE if you plan to build native images)  
- **Maven**  
- **Node.js** ≥ 16 & **npm**  
- **Docker Desktop** (or Docker Engine on Linux)  
- **OCI CLI** (for cloud deployments)  

---

## Setup & Local Development

### Environment Variables

Create a `.env` file or export these variables in your shell:

| Variable                 | Purpose                                           | Example                                 |
|--------------------------|---------------------------------------------------|-----------------------------------------|
| `SPRING_PROFILES_ACTIVE` | Spring profile (`dev`/`test`/`prod`)              | `dev`                                   |
| `DATABASE_URL`           | JDBC URL to Oracle DB                             | `jdbc:oracle:thin:@localhost:1521/ORCLPDB1` |
| `ORACLE_WALLET_PATH`     | Path to OCI Wallet directory                      | `./Wallet_FATDATABASE`                  |
| `JWT_SECRET`             | HMAC secret for signing JWTs                      | `super-secret-change-me`                |
| `CLERK_API_KEY`          | Clerk service API key                             | `sk_test_abc123`                        |
| `TELEGRAM_BOT_TOKEN`     | Telegram Bot token                                | `123456:ABC-DEF…`                       |

---

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

* **Health check**: `GET http://localhost:8080/actuator/health`
* **Swagger UI**: `http://localhost:8080/swagger-ui.html`

---

### Running the Front-end

```bash
cd MtdrSpring/backend/src/main/frontend

# Install dependencies & start
npm ci
npm start
```

* App available at **[http://localhost:3000](http://localhost:3000)** (proxied to backend on port 8080)
* To build for production: `npm run build`

---

### Docker Compose

Start everything with one command (requires Docker):

```bash
# from repo root
docker-compose up --build
```

This brings up:

* Oracle DB container (with sample wallet)
* Spring Boot app on port **8080**
* Static React build served by Spring Boot

---

## Testing

### Unit Tests

```bash
# Back-end unit tests (service & controller layer with MockMvc)
cd MtdrSpring/backend
mvn test
```

### Integration Tests

> Uses Testcontainers + Oracle Free to spin up a real database.

```bash
cd MtdrSpring/backend
mvn verify
```

### Front-end Lint & Unit Tests

```bash
cd MtdrSpring/backend/src/main/frontend
npm run lint
npm test
```

### End-to-End Selenium Tests

We verify critical UI flows using **selenium-webdriver** + **Jest**.

* **Location:**
  `MtdrSpring/backend/src/main/frontend/selenium/tests`

* **Sample test (`devLoginButton.test.js`):**

  ```js
  const createDriver  = require('../driver');
  const { By, until } = require('selenium-webdriver');

  describe('Dev Login – Oracle SSO button', () => {
    let driver;
    beforeAll(async () => {
      driver = createDriver();
      await driver.get('http://localhost:8080/#/dev-login');
    });
    afterAll(() => driver.quit());

    test('SSO button displays correct text', async () => {
      const btn = await driver.wait(
        until.elementLocated(By.css('.login-button')), 10000
      );
      expect(await btn.getText())
        .toBe('Iniciar Sesión Con Oracle SSO');
    });
  });
  ```

#### Running E2E

1. Ensure backend (`localhost:8080`) and frontend (`localhost:3000`) are running.
2. From the frontend folder:

   ```bash
   cd MtdrSpring/backend/src/main/frontend
   npm run test:e2e
   ```

---

## CI/CD

* **GitHub Actions**

  * Builds backend JAR, runs tests, packages artifacts
  * Installs Node, lints and builds React app

  Configuration: `.github/workflows/build.yml`

* **OCI DevOps**

  * Builds Docker image, pushes to OCIR, deploys to Kubernetes on OCI

  Configuration: `oci_devops.yml`

---

## Git Hooks & Scripts

Helper scripts in the `scripts/` directory:

| Script         | Purpose                                           |
| -------------- | ------------------------------------------------- |
| `pre-commit`   | Spotless format & Checkstyle checks               |
| `pre-push`     | Run full backend test suite                       |
| `start-dev.sh` | Docker Compose up + apply migrations + launch app |

```bash
# Install hooks
cp scripts/pre-commit .git/hooks/pre-commit && chmod +x .git/hooks/pre-commit
cp scripts/pre-push   .git/hooks/pre-push   && chmod +x .git/hooks/pre-push
```

---

## Platform Notes

| OS      | Notes                                                       |
| ------- | ----------------------------------------------------------- |
| macOS   | Use Docker Desktop                                          |
| Windows | Enable WSL2 and run Docker in Linux mode                    |
| Linux   | Standard Docker setup; ensure `docker-compose` is installed |

---

## Contributing

We welcome contributions! Please:

1. Fork the repo & create a feature branch
2. Write tests for new functionality
3. Follow code style (Spotless & Checkstyle)
4. Submit a PR against the `dev` branch

See [CONTRIBUTING.md](docs/CONTRIBUTING.md) for full guidelines.

---

## License

This project is licensed under the **MIT License**. See [LICENSE](LICENSE) for details.

