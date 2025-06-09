# oci-react-samples
A repository for full stack Cloud Native applications with a React JS frontend and various backends (Java, Python, DotNet, and so on) on the Oracle Cloud Infrastructure.

![image](https://user-images.githubusercontent.com/7783295/116454396-cbfb7a00-a814-11eb-8196-ba2113858e8b.png)
  

## MyToDo React JS
The `mtdrworkshop` repository hosts the materiald (code, scripts and instructions) for building and deploying Cloud Native Application using a Java/Helidon backend Prueba


### Requirements
The lab executes scripts that require the following software to run properly: (These are already installed on and included with the OCI Cloud Shell)
* oci-cli
* python 2.7^
* terraform
* kubectl
* mvn (maveennn)



1. **Copy the hook scripts** from this repo into your local `.git/hooks` folder:  
   ```bash
   cp scripts/pre-commit .git/hooks/pre-commit
   cp scripts/pre-push   .git/hooks/pre-push
   ```

2. **Make them executable**:
   ```bash
   chmod +x .git/hooks/pre-commit
   chmod +x .git/hooks/pre-push
   ```

3. **Verify** by making a small change and running:
   ```bash
   git commit -m "test hooks"
   git push
   ```
   You should see Spotless/Checkstyle format checks on commit, and JUnit tests on push.

### Example: `scripts/pre-commit`

```bash
#!/usr/bin/env bash
# --- pre-commit: format + lint Java code via Spotless & Checkstyle ---

echo "▶ Formatting & linting code…"

# Move to backend
cd MtdrSpring/backend || exit 1

# 1) Apply Google Java Format
mvn -q spotless:apply || { echo "❌ spotless failed"; exit 1; }

# 2) Run Checkstyle (abort on violations)
mvn -q checkstyle:check || { echo "❌ checkstyle violations"; exit 1; }

exit 0
```

### Example: `scripts/pre-push`

```bash
#!/usr/bin/env bash
# --- pre-push: run backend tests before pushing ---

echo "▶ Running backend tests…"

cd MtdrSpring/backend || { echo "❌ backend folder not found"; exit 1; }

mvn -q test
if [ $? -ne 0 ]; then
  echo "❌ Tests failed; push aborted."
  exit 1
else
  echo "✅ Tests passed; proceeding with push."
  exit 0
fi
```

---

## CI/CD with GitHub Actions

We also enforce these checks in CI on every push or PR:

```yaml
# .github/workflows/build.yml
name: Backend + Frontend CI

on:
  push:
    branches: [ main, dev, LinuxTaks ]
  pull_request:

jobs:
  # ---------- BACKEND ----------
  backend:
    runs-on: ubuntu-22.04
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 11
        uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: 11
          cache: maven

      - name: Build backend JAR (skip tests)
        working-directory: MtdrSpring/backend
        run: mvn --batch-mode clean package -DskipTests

      - name: Upload JAR artifact
        uses: actions/upload-artifact@v4
        with:
          name: MyTodoList-jar
          path: MtdrSpring/backend/target/MyTodoList-0.0.1-SNAPSHOT.jar

  # ---------- FRONTEND ----------
  frontend:
    runs-on: ubuntu-22.04
    needs: backend

    defaults:
      run:
        working-directory: MtdrSpring/backend/src/main/frontend

    steps:
      - uses: actions/checkout@v4

      - name: Set up Node 16
        uses: actions/setup-node@v4
        with:
          node-version: 16

      - name: Install dependencies
        run: npm ci

      - name: Lint (ESLint)
        run: npm run lint

      - name: Build React app
        run: npm run build

      - name: Upload React build
        uses: actions/upload-artifact@v4
        with:
          name: react-build
          path: MtdrSpring/backend/src/main/frontend/build

## Local Deployment (Backend + Frontend)

You can run the full application — Spring Boot backend and embedded React frontend — locally using Docker. This works across macOS, Windows (WSL2), and Linux.

### Prerequisites

- Docker Desktop installed
- Oracle Wallet folder named `Wallet_FATDATABASE/` placed inside `MtdrSpring/backend/`
- Ports 8080 and 8081 available

### Steps to Deploy Locally

1. Open a terminal and navigate to the backend folder:

   ```bash
   cd MtdrSpring/backend
   ```

2. Build the Docker image:

   ```bash
   docker build -t mytodoapp .
   ```

3. Run the container:

   ```bash
   docker run -d -p 8080:8080 --name mytodoapp-container mytodoapp
   ```

4. Open your browser and visit:

   ```
   http://localhost:8080
   ```

   You will see the React frontend served by the Spring Boot backend.

### View Logs (Optional)

To monitor logs in real time (e.g., backend startup, request handling, DB events):

```bash
docker logs -f mytodoapp-container
```

###  OS-specific Notes

| OS        | Notes                                                                 |
|-----------|-----------------------------------------------------------------------|
| macOS     | Runs out-of-the-box with Docker Desktop                              |
| Windows   | Requires [WSL2](https://docs.microsoft.com/en-us/windows/wsl/) and Docker set to Linux containers |
| Linux     | Works with standard Docker installation                               |

---
