---
description: Run build/tests, diagnose failures, and automatically fix errors
---

Run the build to diagnose issues:

```bash
./gradlew build
```

(Not `clean` — it forces a full recompile for no benefit here.)

If compilation or test errors are reported:

1. Identify which module failed first. `:core:` and `:app:` fail for different reasons; Gradle
   names the failing task, so read it before editing anything.
2. Reproduce in isolation: `./gradlew :core:test --tests '*ClassName*'` (or `:app:`).
3. Inspect the stack trace and failing files, then apply the smallest correct fix.

## Guardrails — do not do these to go green

- **Never weaken or delete an `HexagonalArchitectureTest` rule.** It encodes the architecture; if
  it fails, the production code is wrong.
- **Never delete, `@Disabled`, or `@Ignore` a failing test.** If the asserted behavior genuinely
  changed, update the assertion and say so explicitly.
- **Never add Spring / JPA / Jackson to `core/build.gradle`** to fix a `core` compile error.
  Introduce a port in `core` and implement the adapter in `app`.
- A skipped Testcontainers test is not a failure — they are `disabledWithoutDocker`.

Re-run `./gradlew build` to confirm. If it still fails, report that plainly rather than claiming
success.
