---
description: Implement a feature based on Hexagonal Architecture rules
---

Implement the feature: "$ARGUMENTS"

Adhere strictly to the following architectural rules:

1. **Module `core`** — `core/src/main/java/com/eshop/core/`:
    - `domain/{model,vo,exception}` — records with compact-constructor validation, immutable
      (mutators return a new instance).
    - `application/{dto,usecase,port/{in,out}}` — command/result DTO records, plain use case
      classes with constructor injection, and pure interfaces.
    - NO Spring, JPA (`jakarta.persistence.*`), Jackson, servlet API, or jjwt. These are absent
      from `core`'s classpath, so importing them is a compile error, not a style preference.
2. **Module `app`** — `app/src/main/java/com/eshop/app/`:
    - Inbound adapters under `adapter/in/web` (`@RestController`, `@Valid @RequestBody`, response
      records with a static `from(...)` factory); outbound adapters under `adapter/out`.
    - Use `jakarta.*` for validation and persistence here.
    - Wire `@Bean`s in `app/config/`. Never annotate a use case with `@Service`/`@Component`.
    - AI features: decide the profile explicitly — `@Profile("!langchain4j")` for Spring AI,
      `@Profile("langchain4j")` for LangChain4j, or both behind one port. Prompts live in
      `app/src/main/resources/prompts/`.
3. **Execution**:
    - Write domain logic first, then ports, then adapters and wiring.
    - Run `./gradlew build` on completion. `HexagonalArchitectureTest` must stay green.
