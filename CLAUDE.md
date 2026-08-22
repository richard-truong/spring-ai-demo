# CLAUDE.md

Guidance for AI coding agents working in this repository.

## Project

Hexagonal Structure Shop ("EvShop") — a Spring Boot e-commerce backend that follows
the Hexagonal Architecture (Ports & Adapters) pattern.

- Language: Java 21
- Build: Gradle (wrapper) — `gradlew` / `gradlew.bat`
- Framework: Spring Boot 3.5.16, Spring Security 6.5.x
- Package root: `com.eshop.app`

## Active skills

Always strictly follow and apply all active skills located in `.claude/skills/`:

- **hexagonal-architecture**: Enforce clean boundary separation between domain core and application/adapter layers.
- **ripgrep**: Utilize fast code searching and discovery across the repository.
- **springboot-patterns**: Follow standard Spring Boot 3 enterprise patterns and clean code practices.
- **springboot-security**: Implement security, authentication, and authorization rules properly.
- **springboot-tdd**: Apply Test-Driven Development (TDD) using JUnit 5 & Mockito.
- **springboot-verification**: Run tests and verification steps to confirm code correctness before finishing tasks.

## Build & test commands

Run from the repo root. On Windows use `.\gradlew.bat`, elsewhere `./gradlew`.

- Build + test: `./gradlew build`
- Run tests only: `./gradlew test`
- Boot the app: `./gradlew bootRun`
- Clean: `./gradlew clean`

No lint/format tool is configured; rely on `./gradlew build` for correctness.

## Architecture conventions

Follow Hexagonal Architecture. Keep the dependency rule: inner layers must not
depend on outer layers (framework, persistence, transport).

- `domain` — pure business model: entities, value objects, domain services, rules.
  No Spring, no JPA annotations.
- `application` — use cases and ports. Port interfaces (inbound use cases, outbound
  repositories/gateways) are defined here. No framework dependencies except where
  unavoidable (e.g. `@Service`, transactions).
- `infrastructure` (adapters) — Spring wiring, REST controllers, persistence
  (JPA/Spring Data), security config, message adapters. Implement application ports here.

Rules:

- Domain and application code must not import `org.springframework` (application may
  use annotations only via configuration in infrastructure; keep ports pure interfaces).
- Adapters implement port interfaces; the domain is wired to adapters via
  dependency injection in the `infrastructure` layer.
- Use records for value objects and DTOs; keep DTOs out of the domain.
- Add `@Configuration`/`@Bean` wiring in `infrastructure`, never in `domain`.

## Conventions

- Match existing style before introducing new patterns.
- No comments unless asked; keep code self-documenting.
- Never commit secrets. Do not log credentials.
- Use the `hexagonal-architecture` skill when generating new domain/application/infrastructure
  code (see `.claude/skills/hexagonal-architecture`).

## Testing

- `@SpringBootTest` context tests under `src/test/java/com/eshop/app`.
- Prefer focused unit tests for domain logic; use mocks for ports.
