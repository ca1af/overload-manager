# Project Rules

This document is auto-loaded at every Claude Code session start.
Read this fully before beginning any work.

---

## Tech Stack

### Backend
- **Framework**: Spring Boot 4.0.3 + Kotlin 2.2.21
- **Architecture**: Hexagonal Architecture + DDD (single module)
- **Build**: Gradle (Kotlin DSL), Java 21
- **ORM**: Spring Data JPA + Flyway migrations
- **Auth**: JWT (JJWT 0.12.6) — access token 1h, refresh token 30d
- **DB**: H2 (dev/test), MySQL (prod)
- **Test**: JUnit 5 + H2 in-memory

### Frontend
- **Framework**: React 19 + TypeScript 5.9
- **Build**: Vite 8
- **Routing**: React Router 7
- **Server State**: TanStack Query v5
- **Client State**: Zustand v5
- **UI**: shadcn/ui pattern (Radix UI) + Tailwind CSS 4
- **Chart**: Recharts 3
- **Form**: React Hook Form + Zod
- **HTTP**: Axios (JWT interceptor)
- **Bottom Sheet**: vaul
- **Date**: date-fns 4

---

## Architecture Overview

### Backend — Hexagonal + DDD

Single module. Package root: `com.calaf.overloadmanager`

Each domain follows this structure:
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

### Naming Conventions
- Domain model: `User`, `Exercise`, `WorkoutSession`
- JPA entity: `UserJpaEntity`, `ExerciseJpaEntity`, `WorkoutSessionJpaEntity`
- Output port: `UserRepository` (interface in domain/port/out/)
- Persistence adapter: `UserPersistenceAdapter` (implements output port)
- Use case interface: `RegisterUseCase`, `GetProfileUseCase`
- Spring Data repo: `UserJpaRepository` (interface in adapter/out/persistence/)

### Frontend Structure

Project root: `frontend/`

```
src/
├── api/              # Axios instance + API functions per domain
├── components/       # Shared components (AppHeader, BottomTabBar, RestTimerOverlay)
│   └── ui/           # shadcn/ui style primitives (Button, Input, Card, etc.)
├── features/         # Feature modules
│   ├── auth/         # LoginForm, RegisterForm
│   ├── exercise/     # ExerciseList
│   ├── session/      # SetTable, OverloadInfoSheet, SessionSummaryModal
│   └── report/       # MaxWeightChart, WeeklySummaryView
├── hooks/            # useDebounce, useTimer
├── pages/            # Route pages (AuthPage, DashboardPage, etc.)
├── router/           # React Router config with ProtectedRoute
├── store/            # Zustand stores (authStore, sessionStore)
├── types/            # TypeScript domain types
└── utils/            # weight conversion, date formatting, cn()
```

---

## Build & Verify

```bash
# Backend
./gradlew build

# Frontend
cd frontend && npm run build
```

---

## Testing

- **Backend domain layer**: unit tests required (pure Kotlin, no Spring)
- **Backend application layer**: integration tests required
- **"Done" criteria**: all tests pass, zero compilation errors

---

## docs Directory Structure

```
docs/
├── planning/
│   ├── requirements/
│   │   ├── backlog/        # Proposed (not yet approved)
│   │   ├── approved/       # Approved, ready for PRD
│   │   └── archive/        # Completed / rejected / superseded
│   ├── prd/                # PRD per feature
│   ├── user-flows/         # User flow per feature
│   └── wireframes/         # Wireframe descriptions
├── design/
│   ├── architecture.md     # Tech stack + project structure
│   ├── erd.md              # Database ERD
│   └── api-spec.md         # REST API specification
├── features/
│   ├── todo/               # Pending implementation
│   └── done/               # Completed features
├── conventions/
│   ├── backend/
│   └── frontend/
├── issues/                 # Blocking issue tracker
├── sessions/               # Team handoff
│   └── archive/
└── README.md
```

---

## Planning Team Rules

### Role Boundary

Planning team defines **what** to build. **How** to build belongs to engineering. Do not include framework names or technical constraints in planning docs.

### Requirements Lifecycle

```
backlog/ → approved/ → archive/
```

### Planning Deliverables

3 documents per approved requirement:
1. **PRD** (`docs/planning/prd/{feature-name}.md`)
2. **User Flow** (`docs/planning/user-flows/{feature-name}.md`)
3. **Wireframe** (`docs/planning/wireframes/{feature-name}.md`)

### Planning → Engineering Handoff

1. Complete PRD + user flow + wireframes
2. Create `docs/features/todo/{feature-name}.md` with summarized requirements
3. Engineering works from `docs/features/todo/` only

---

## Engineering Team Code Standards

### Hexagonal Architecture Rules

1. **domain/model/**: Pure Kotlin. No Spring, no JPA annotations. Business logic lives here.
2. **domain/port/in/**: Use case interfaces. No implementation details.
3. **domain/port/out/**: Repository interfaces using domain model types only.
4. **application/service/**: Implements input ports. Depends only on output ports. Holds @Transactional.
5. **adapter/in/web/**: Controllers + DTOs. Maps between web DTOs and domain models.
6. **adapter/out/persistence/**: JPA entities + Spring Data repos + adapters implementing output ports.

### Dependency Direction
```
adapter/in/web → application/service → domain/port
adapter/out/persistence → domain/port
```
Domain layer has **zero** outward dependencies.

### New Feature Checklist
1. Define domain model in `domain/model/`
2. Define use case interface in `domain/port/in/`
3. Define repository interface in `domain/port/out/`
4. Implement service in `application/service/`
5. Implement controller in `adapter/in/web/`
6. Implement JPA entity + persistence adapter in `adapter/out/persistence/`
7. Add Flyway migration if schema changes needed

---

## Feature Spec (docs/features/todo/)

```markdown
# {Feature Name}

## Overview
## Planning References
## Backend
## Frontend
## Acceptance Criteria
```

Move to `docs/features/done/` on completion.

---

## Blocking Issue Management

- Log as `docs/issues/issue_{n}.md`
- Current team documents only, does NOT resolve
- Next team resolves as top priority

---

## Agent Team Operating Rules

### Common
- All teammates use worktrees for isolation
- Leader uses delegation mode; does not write code
- Implementation teammates split by package; no overlapping file edits

### Model Assignment
- Leader, Architect, Code Reviewer: Opus
- Implementation, Planning drafts: Sonnet
