# Backend Engineering Guide

Read `CLAUDE.md` first for shared rules. This guide covers backend-specific standards.

---

## Tech Stack

- **Framework**: Spring Boot 4.0.3 + Kotlin 2.2.21
- **Build**: Gradle (Kotlin DSL), Java 21
- **ORM**: Spring Data JPA + Flyway migrations
- **Auth**: JWT (JJWT 0.12.6) — access token 1h, refresh token 30d
- **DB**: H2 (dev/test), MySQL (prod)
- **Test**: JUnit 5 + H2 in-memory

---

## Architecture — Hexagonal + DDD

Single module. Package root: `com.calaf.overloadmanager`

```
{domain}/
├── domain/
│   ├── model/              # Pure Kotlin data classes (no JPA)
│   └── port/
│       ├── in/             # Use case interfaces
│       └── out/            # Repository interfaces (plain Kotlin)
├── application/
│   └── service/            # Use case implementations (@Service)
└── adapter/
    ├── in/
    │   └── web/            # REST controllers + request/response DTOs
    └── out/
        └── persistence/    # JPA entities, Spring Data repos, adapters
```

Shared packages:
```
common/              # ErrorCode, AppException, GlobalExceptionHandler, ApiResponse
config/              # SecurityConfig, JpaConfig
infrastructure/jwt/  # JwtTokenProvider, JwtAuthenticationFilter
```

### Dependency Direction

```
adapter/in/web → application/service → domain/port
adapter/out/persistence → domain/port
```

Domain layer has **zero** outward dependencies.

### Hexagonal Rules

1. `domain/model/`: Pure Kotlin. No Spring, no JPA annotations. Business logic lives here.
2. `domain/port/in/`: Use case interfaces. No implementation details.
3. `domain/port/out/`: Repository interfaces using domain model types only.
4. `application/service/`: Implements input ports. Depends only on output ports. Holds `@Transactional`.
5. `adapter/in/web/`: Controllers + DTOs. Maps between web DTOs and domain models.
6. `adapter/out/persistence/`: JPA entities + Spring Data repos + adapters implementing output ports.

---

## Naming Conventions

| Type | Pattern | Example |
|------|---------|---------|
| Domain model | `{Name}` | `User`, `Exercise`, `WorkoutSession` |
| JPA entity | `{Name}JpaEntity` | `UserJpaEntity` |
| Output port | `{Name}Repository` | `UserRepository` (interface in `domain/port/out/`) |
| Persistence adapter | `{Name}PersistenceAdapter` | `UserPersistenceAdapter` |
| Use case interface | `{Verb}{Noun}UseCase` | `RegisterUseCase`, `GetProfileUseCase` |
| Spring Data repo | `{Name}JpaRepository` | `UserJpaRepository` (in `adapter/out/persistence/`) |

---

## New Feature Checklist

1. Define domain model in `domain/model/`
2. Define use case interface in `domain/port/in/`
3. Define repository interface in `domain/port/out/`
4. Implement service in `application/service/`
5. Implement controller in `adapter/in/web/`
6. Implement JPA entity + persistence adapter in `adapter/out/persistence/`
7. Add Flyway migration if schema changes needed
8. Write unit tests for domain layer
9. Write integration tests for application layer
10. Run `./gradlew build` — must pass

---

## Testing

- **Domain layer**: unit tests required (pure Kotlin, no Spring context)
- **Application layer**: integration tests required
- **Build**: `./gradlew build`
- All tests must pass before reporting "done" to leader
- For E2E verification process, see [`docs/conventions/qa/GUIDE.md`](../qa/GUIDE.md)

---

## API Contract Rules

### Field Sync with Frontend

- Response DTO field names must **1:1 match** frontend TypeScript type field names
- When creating a new API, define backend DTO and frontend type simultaneously
- Nullable fields: explicit on both sides (backend `?`, frontend `| null`)

### Pagination

- Do NOT serialize `PageImpl` directly
- Use custom `PageResponse<T>` DTO (defined in `common/ApiResponse.kt`)
- Apply `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` if using Spring Data PagedModel

---

## Verification Duties

### During Implementation

- Run `./gradlew build` after each use case implementation
- Do not proceed to next use case if tests fail

### After Implementation (Pre-merge)

- All integration tests pass: `./gradlew build`
- Zero compilation errors

### Cross-cutting Changes (DTO changed)

When you modify a response DTO:
1. **Message the FE agent** with: changed endpoint, old vs new field names, nullable changes
2. Wait for FE agent to confirm type sync and build pass
3. If no FE agent (solo work): update FE types yourself → `cd frontend && npm run build`

---

## Backend Agent Team Workflow

1. Leader builds plan, creates task list
2. Architect writes design docs → leader approval
3. On approval, implementers start parallel work (split by domain package)
4. Code reviewer reviews completed work
5. Leader verifies `./gradlew build` passes
6. Commit to designated branch
