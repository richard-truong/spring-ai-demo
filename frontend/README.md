# EvShop Frontend (Lesson 9)

A simple Next.js (App Router + TypeScript + Tailwind) storefront that talks to the
EvShop Spring Boot backend. Pages call `http://localhost:8080` directly from the
browser, so the backend must allow CORS from `http://localhost:3000` (already
configured in `SecurityConfig`).

## Setup

```bash
npm install
cp .env.example .env.local   # optional; defaults to http://localhost:8080
```

## Run

```bash
# 1. Start infra (Postgres + Redis — Redis is required for spring.cache.type=redis)
docker compose -f ../docker/docker-compose.db.yml up -d

# 2. Start the backend (repo root)
JWT_SECRET="0123456789abcdef0123456789abcdef0123456789abcdef" ./gradlew bootRun

# 3. Start the frontend (this dir)
npm run dev
```

Open http://localhost:3000.

### Chat page

The chat endpoints only exist when the backend runs with the `langchain4j` Spring
profile. Without it the chat page shows an "unavailable" banner.

```bash
SPRING_PROFILES_ACTIVE="langchain4j,openai" OPENAI_API_KEY="sk-..." JWT_SECRET="..." ./gradlew bootRun
```

## Note on the auth rate limit

`/api/v1/auth/**` is rate-limited to about 5 requests per minute per IP, shared
between your browser and any `curl` calls. If you hit 429, wait about a minute.
