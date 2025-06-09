
# Contributing to MyTodoList

Thank you for your interest in improving this project! By following these guidelines you’ll help us keep the code clear, consistent, and high-quality.

---

## 1. Where to put this file

Save this file as **`CONTRIBUTING.md`** in the **root** of your repository, alongside `README.md`, `LICENSE`, and the `.github/` folder:

```

/
├── CONTRIBUTING.md
├── README.md
├── LICENSE
├── .github/
│   └── workflows/
│       └── build.yml
├── oci\_devops.yml
└── src/
└── …

````

---

## 2. Getting Started

1. **Fork** the repository and **clone** your fork:
   ```bash
   git clone https://github.com/your-username/oci-react-samples.git
   cd oci-react-samples


2. **Create** a new branch for your feature or bugfix:

   ```bash
   git checkout -b feature/short-description
   ```

---

## 3. Code Style & Quality

* **Java**

  * `mvn spotless:apply` to auto-format code
  * `mvn checkstyle:check` to verify style rules
* **JavaScript/TypeScript**

  * `npm run lint` in `MtdrSpring/backend/src/main/frontend`
* **Pre-commit Hooks**

  * Install the provided hooks:

    ```bash
    cp scripts/pre-commit .git/hooks/pre-commit && chmod +x .git/hooks/pre-commit
    cp scripts/pre-push   .git/hooks/pre-push   && chmod +x .git/hooks/pre-push
    ```
  * These will automatically format, lint, and run tests before each commit/push.

---

## 4. Testing

* **Backend unit & integration tests** (Oracle + Testcontainers):

  ```bash
  cd MtdrSpring/backend
  mvn test
  ```
* **Frontend tests & lint**:

  ```bash
  cd MtdrSpring/backend/src/main/frontend
  npm run lint
  npm test
  ```

Ensure all tests pass and coverage remains ≥ 80% before submitting your PR.

---

## 5. Pull Request Process

1. Push your branch to **your fork**:

   ```bash
   git push origin feature/short-description
   ```
2. Open a Pull Request **against the `tocha-dev` branch** of the upstream repository.
3. Use a clear title: `feat: add XYZ` or `fix: resolve ABC bug`
4. Provide a concise description of your changes and reference any related issues (e.g. “Fixes #123”).
5. **Require two approvals** from different developers before merging into `tocha-dev`.
6. After approval, merge with a **squash merge** and delete your feature branch.

---

## 6. Branching Strategy

* **`tocha-dev`**: Main development branch (all PRs target this)
* **feature/**\*: New features or enhancements
* **fix/**\*: Bug fixes
* **hotfix/**\*: Urgent fixes to `tocha-dev` or production

Once changes are merged into `tocha-dev`, they will be included in the next release cycle.

---

## 7. Reporting Issues

1. Use the GitHub **Issues** tab.
2. Provide a clear title, steps to reproduce, and any relevant logs or screenshots.
3. Tag the issue with one of: `bug`, `enhancement`, `question`.

---

## 8. Thank You!

Your contributions make this project better for everyone. We appreciate your time and effort!

