---
description: Review staged or uncommitted code for architectural leaks and issues
---

Review the recent code changes in the repository.

## 1. Establish the full scope first

`git diff` alone shows only unstaged edits to tracked files. It silently misses staged changes,
new (untracked) files, and commits already on this branch. Cover all three:

```bash
git diff HEAD --stat && git diff HEAD        # staged + unstaged, tracked files
git ls-files --others --exclude-standard     # new, untracked files
git log --oneline main..HEAD                 # commits already made on this branch
```

## 2. Run the architecture gate

```bash
./gradlew :app:test --tests '*HexagonalArchitectureTest'
```

## 3. Verify

1. Are any Spring / JPA (`jakarta.persistence`) / Jackson dependencies leaking into `core`?
2. Does every non-interface class in `app.adapter.out` (excluding `*Entity`) implement a port from
   `core.application.port.out`? (ArchUnit rule 8)
3. Are domain models decoupled from REST DTOs and JPA entities?
4. Is error handling mapped across port boundaries via `GlobalExceptionHandler` / `ProblemDetail`?
5. Do AI adapters carry the correct profile, and do two adapters for the same port have
   non-colliding profiles?
6. Are adequate JUnit 5 tests added for core business logic — using hand-written fakes, since
   Mockito is unavailable in `core`?

Provide constructive criticism and list required fixes if any violations are found.
