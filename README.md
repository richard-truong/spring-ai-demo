# EvShop

Spring Boot e-commerce backend following Hexagonal Architecture (Ports & Adapters).

## Modules

- `core` — pure domain + application (ports, use cases, DTOs). No Spring, no JPA.
- `app` — infrastructure + delivery (REST, security, persistence, composition root).

## Build & test

```bash
./gradlew build   # compile + run all tests (Testcontainers tests require Docker)
./gradlew test    # run tests only
./gradlew bootRun # boot the app
```

## Run with Docker

```bash
# DB only (with seed script)
docker compose -f docker/docker-compose.db.yml up -d

# App + DB together (app applies Flyway migrations on startup)
docker compose -f docker/docker-compose.yml up --build
```

Configuration is injected via environment variables (see `.env.example`).

## API

- `POST /api/v1/auth/register` — create a customer account.
- `POST /api/v1/auth/login` — authenticate and return a JWT.
- `POST /api/v1/orders` — place an order (requires `Authorization: Bearer <jwt>`).

## Git workflow

Branch model (Git Flow-lite):

- `main` — protected, release-ready.
- `develop` — integration branch.
- `feature/<ticket-slug>` — one branch per ticket.

Integration is PR-based:

- `feature/*` → `develop` (feature PR)
- `develop` → `main` (release PR)

### Branch protection

Enable the following branch protection rules on GitHub for both `main` and `develop`:

- Require a pull request before merging (no direct pushes).
- Require status checks to pass (`build` job in `.github/workflows/ci.yml`).
- Require branches to be up to date before merging (optional but recommended).

The CI workflow triggers on `push` and `pull_request` to `develop` and `main`; it runs
the Gradle build (compile + tests) and, on push to `main`/`develop`, a Docker smoke build.
