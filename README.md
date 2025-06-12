# Oracle SprintSight

Oracle SprintSight is a **cloud-native** application that streamlines sprint and KPI tracking for development teams. It marries a fast Type-Script + React.js front-end with a robust Spring Boot 3 back-end running on Oracle Autonomous Database, and secures everything with Firebase Authentication. A Telegram bot extends the experience to chat, while automated CI/CD pipelines (GitHub Actions + OCI DevOps) and Testcontainers keep builds, security, and testing reproducible from laptop to cloud.

---

## Key Technology Stack

| Tier / Concern       | Technology                                       |
| -------------------- | ------------------------------------------------ |
| **Front-end**        | React 18 + Vite + TypeScript                     |
| **Back-end**         | Spring Boot 3 (Java 17)                          |
| **Data**             | Oracle Autonomous Database (ATP)                 |
| **Auth**             | Firebase Authentication (JWT, hosted login)      |
| **Chat UX**          | TelegramLongPollingBot (Java)                    |
| **Containerization** | Docker                                           |
| **CI/CD**            | GitHub Actions, OCI DevOps build & deploy stages |
| **Testing**          | JUnit 5, Testcontainers, Selenium (E2E)          |
| **Infra-as-Code**    | Kubernetes YAML manifests                        |
| **Security**         | OWASP ZAP scan in pipeline                       |

---

## Architecture

[**Download the PDF**](https://drive.google.com/uc?export=view&id=1ezv0N4BDVg8ytpaWaS_nmzur3LcKrq20) | [**View in Drive**](https://drive.google.com/file/d/1ezv0N4BDVg8ytpaWaS_nmzur3LcKrq20/view?usp=sharing)

**High-level flow**

1. **Client** (React) obtains a Firebase **ID token** after login.
2. **API Gateway**

   - Validates the JWT against the Firebase JWKS endpoint (`https://securetoken.google.com/<PROJECT_ID>`).
   - Routes the request to the corresponding micro-service.

3. **Spring Boot micro-services**

   - _TaskService, SubTaskService, SprintService, KpiService, UsuarioService, ToDoItemService_
   - Each adopts the **Controller → Service → Repository** layered pattern (hexagonal ports/adapters).

4. **Data layer**

   - Oracle ATP for production.
   - Testcontainers spins up an ephemeral ATP-compatible container during `mvn test`.

5. **Telegram Bot**

   - `ToDoItemBotController` hosts conversational flows (add task, list tasks, complete task).

---

## Prerequisites

| Tool        | Minimum Version |
| ----------- | --------------- |
| **Java**    | 17              |
| **Maven**   | 3.8+            |
| **Node.js** | 16+             |
| **Docker**  | 20+             |
| **OCI CLI** | Latest          |

---

## Getting Started (Local Dev)

1. **Clone repository**

   ```bash
   git clone https://github.com/casrhub/oci-react-java-toodo.git
   cd oci-react-java-toodo
   ```

2. **Configure Oracle Wallet path**

   Edit
   `MtdrSpring/backend/src/main/resources/application.properties`

   ```properties
   spring.datasource.url=jdbc:oracle:thin:@fatdatabase_high?TNS_ADMIN=<ABSOLUTE_PATH>/oci-react-java-toodo/MtdrSpring/backend/src/main/resources/wallet/Wallet_FATDATABASE/
   ```

3. **Build back-end**

   ```bash
   cd MtdrSpring/backend
   mvn clean install      # compiles + unit + integration tests
   mvn spring-boot:run    # app available at http://localhost:8080
   ```

4. **Run front-end (optional)**
   The React SPA is pre-built into the Spring Boot JAR, but if you want concurrent hot-reload:

   ```bash
   cd MtdrSpring/backend/src/main/frontend
   npm install
   npm run dev           # Vite dev server at http://localhost:3000
   ```

---

## Local Deployment with Docker

```bash
cd MtdrSpring/backend
docker build -t todoapp .
docker run -d -p 8080:8080 --name todoappcontainer todoapp
```

The service is now reachable at **[http://localhost:8080](http://localhost:8080)** inside its own container, using the in-container Oracle Wallet.

---

## Running Tests

| Scope                     | Command                                                       | Description                                                   |
| ------------------------- | ------------------------------------------------------------- | ------------------------------------------------------------- |
| **Unit + Integration**    | `cd MtdrSpring/backend && mvn test`                           | JUnit + Testcontainers (spins up Oracle-compatible container) |
| **End-to-End (Selenium)** | `cd MtdrSpring/backend/src/main/frontend && npm run test:e2e` | Headless Chrome via Selenium                                  |

---

## CI/CD Overview

> The pipeline combines **GitHub Actions** (build) and **OCI DevOps** (deploy). Key files:

### `build_spec.yaml` (OCI DevOps Build Stage)

| Step                        | What it does                                                                                                                                        |
| --------------------------- | --------------------------------------------------------------------------------------------------------------------------------------------------- |
| **Set IMAGE_TAG**           | Derives a 7-character git SHA for an immutable image tag and exports it for later steps.                                                            |
| **Docker Hub Login**        | Authenticates using `${DOCKER_USERNAME}` and a secret `${DOCKER_PASSWORD}`.                                                                         |
| **Build & Push**            | `docker build` builds the back-end image via the multi-stage Dockerfile, then pushes `${IMAGE_TAG}` and `latest` tags.                              |
| **OWASP ZAP Quick Scan**    | Runs an unauthenticated dynamic scan against a staging URL (`http://220.158.74.30/`), prints alerts, but never fails the build (exit code ignored). |
| **Install JDK 11 & verify** | Installs Oracle JDK 11 inside the runner and executes `mvn verify` to re-run integration tests in a pristine environment.                           |

### `MtdrSpring/backend/Dockerfile` (Multi-Stage Build)

1. **Builder stage**

   - `maven:3.8.6-openjdk-11` compiles the Spring Boot project and re-packages it as a layered JAR (`spring-boot:repackage`).

2. **Runtime stage**

   - `openjdk:11-jre-slim` is used for a minimal runtime image (\~250 MB → \~200 MB after layering).
   - The resulting `app.jar` is copied, `TNS_ADMIN` is set to the in-image wallet, and port **8080** is exposed.

### `todolistapp-springboot.yaml` (Kubernetes Manifests)

| Kind           | Purpose                                                                                                                                                      |
| -------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **Deployment** | Schedules **1 replica** of the Docker image (`casrhub/todolistapp-springboot:${IMAGE_TAG}`) with `Always` pull policy so every rollout grabs the newest tag. |
| **Service**    | Exposes the pod via a **LoadBalancer** on port 80 → container port 8080.                                                                                     |

> **How it ties together**
>
> - GitHub pushes trigger OCI DevOps Build.
> - The build publishes the Docker image and exports `${IMAGE_TAG}`.
> - A subsequent OCI DevOps **Deploy Stage** patches the Kubernetes manifest with the same `${IMAGE_TAG}` and applies it to your OCI Container Engine for Kubernetes (OKE) cluster, achieving blue/green or rolling updates depending on the OKE strategy.

---

## Observability

- **Local Logs**
  Regardless of the tool you invoke—whether it’s

  - **Maven** (`mvn clean install`, `mvn spring-boot:run`, `mvn test`),
  - **Docker** (`docker build`, `docker run`),
  - **React dev server** (`npm run dev` for hot-reload), or
  - **End-to-end tests** (`npm run test:e2e`)—
    the full stdout/stderr output from each command streams directly to your local terminal in real time. You’ll see compilation details, test results, container logs, hot-reload updates, and everything in between as they happen.

- **CI/CD Logs**
  In both our GitHub Actions workflows and OCI DevOps pipelines, every build, test, security-scan, and deployment step emits its logs to the pipeline UI. Logs are automatically captured and made available in each run, so you can inspect the output of any stage—right from the GitHub Actions run view or the OCI DevOps build/deploy dashboard.

---

## Quality & Security Gates

| Stage            | Gate                                                                                    |
| ---------------- | --------------------------------------------------------------------------------------- |
| **Pull Request** | Runs end-to-end tests in GitHub Actions CI                                              |
| **Pre-commit**   | ESLint + Prettier                                                                       |
| **Build**        | JUnit (unit), Testcontainers (integration)                                              |
| **Pipeline**     | OWASP ZAP Dynamic Scan                                                                  |
| **Deploy**       | OCI DevOps builds and deploys with automatic image scanning to ensure secure production |

---

## Contributing

1. Clone the repository.
2. Create a new branch from main (or the appropriate base branch) to work on your changes. Use a descriptive branch name like feature/add-kpi-dashboard or bugfix/fix-task-deletion.
3. Follow **Conventional Commits** (`feat: add gantt chart view`).
4. Write/extend tests → `mvn test` & `npm run test:e2e`.
5. Open PR; the CI pipeline will test, build, scan, and deploy to the cloud.
