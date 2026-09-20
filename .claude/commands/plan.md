---
description: Create an execution plan for a feature using Hexagonal Architecture
---

Analyze the requirement: "$ARGUMENTS"

Generate a step-by-step execution plan covering:

1. **Domain** — the entity/value object and business rules in
   `core/src/main/java/com/eshop/core/domain/{model,vo,exception}`. Records with
   compact-constructor validation; mutators return a new instance.
2. **Application** — the inbound port (use case interface) in `core/application/port/in`, the
   outbound port in `core/application/port/out`, command/result DTOs in `core/application/dto`,
   and the use case implementation in `core/application/usecase`. Plain Java 21 — no Spring, no
   JPA, no Jackson.
3. **Adapters** in `app/src/main/java/com/eshop/app/adapter/{in,out}` — REST controllers with
   `@Valid @RequestBody`, request/response DTO records under `adapter/in/web/dto`, persistence
   adapters with explicit `toDomain`/`toEntity` mappers. Wire `@Bean`s in `app/config/`; never
   annotate a use case.
4. **AI features only** — decide the profile up front: Spring AI (`@Profile("!langchain4j")`),
   LangChain4j (`@Profile("langchain4j")`), or two adapters behind one port as
   `ProductSuggestionPort` does. Prompts go in `app/src/main/resources/prompts/`.
5. **Tests** — this repo has two separate test trees with different rules:
   - `core/src/test/java/com/eshop/core/` — JUnit 5 + AssertJ. Collaborators are **hand-written
     fakes** (`core/test/fake/`, or a `private static final class` inside the test). **Mockito is
     not on `core`'s test classpath and will not compile here.**
   - `app/src/test/java/com/eshop/app/` — `@WebMvcTest` + `MockMvc` + `@MockitoBean` for web
     slices; extend `app/support/PostgresIntegrationTest.java` (Testcontainers) for integration.
     `WebTestClient` is not used and `spring-webflux` is not a dependency.
6. **Architecture gate** — the plan must keep `HexagonalArchitectureTest` (8 ArchUnit rules)
   passing. Call out anything that touches a module boundary.

State how the change will be verified — `./gradlew build` is the correctness gate.

DO NOT write implementation code yet. Output only the structured plan and wait for confirmation.
