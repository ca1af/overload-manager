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
- **"Done" criteria**: all tests pass, zero compilation errors, **E2E 검증 통과**

### E2E 검증 (필수)

구현 완료 후 반드시 실제 브라우저에서 검증한다. 검증 없이 "완료" 처리 금지.

**자동화 E2E (Playwright)** — 회귀 테스트, CI 연동:

```bash
# 실행 (백엔드+프론트엔드 자동 기동)
cd frontend && npx playwright test

# UI 모드로 디버깅
cd frontend && npx playwright test --ui

# 특정 테스트만 실행
cd frontend && npx playwright test tests/auth.spec.ts
```

**탐색적 E2E (Chrome 익스텐션)** — 새 기능 개발 중 즉석 검증:

```bash
# 1. 백엔드 실행
./gradlew bootRun

# 2. 프론트엔드 실행 (별도 터미널)
cd frontend && npm run dev

# 3. Chrome 익스텐션(claude-in-chrome)으로 워크플로우 테스트
```

실패 시 수정 → 재검증을 반복한다. 모든 워크플로우가 통과할 때까지 종료하지 않는다.

### Playwright 규칙

- **위치**: `frontend/tests/` 디렉토리. 설정 파일은 `frontend/playwright.config.ts`
- **서버 기동**: `playwright.config.ts`의 `webServer` 옵션으로 백엔드(8080) + 프론트엔드(5173) 자동 기동
- **네이밍**: `{feature}.spec.ts` (예: `auth.spec.ts`, `dashboard.spec.ts`, `session.spec.ts`)
- **구조**: 각 spec 파일은 하나의 기능 영역을 담당. Page Object 패턴은 복잡해질 때만 도입
- **데이터 격리**: 각 테스트는 독립적. H2 인메모리 DB이므로 서버 재시작 시 초기화됨. 테스트 간 상태 공유 금지
- **대기 전략**: `waitForURL`, `waitForResponse` 등 Playwright 내장 대기 사용. 하드코딩 `sleep` 금지
- **Assertion**: `expect(page).toHaveURL()`, `expect(locator).toBeVisible()` 등 Playwright 내장 matcher 사용
- **CI 연동**: headless 모드 기본. `--trace on` 옵션으로 실패 시 trace 파일 자동 생성

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
├── qa/
│   └── e2e/
│       └── v{N}/           # 버전별 E2E 테스트 라운드
│           ├── test-plan.md              # 테스트 항목 정의
│           ├── {test-case}.md            # 개별 테스트 결과
│           └── {test-case}_done.md       # 통과 완료된 테스트
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

## QA / E2E 검증 프로세스

### 프로세스 흐름

```
1. 테스트 항목 정의 → docs/qa/e2e/v{N}/test-plan.md
2. 항목별 테스트 실행 → Chrome 익스텐션(claude-in-chrome)으로 실제 브라우저 조작
3. 개별 결과 기록 → docs/qa/e2e/v{N}/{test-case}.md (버그/개선사항 포함)
4. 수정 후 재검증 통과 → 파일명을 {test-case}_done.md 로 변경
5. 모든 항목이 _done.md 가 되기 전에는 절대 종료하지 않는다
```

### 테스트 항목 문서 형식 (`{test-case}.md`)

```markdown
# TC-{N}: {테스트 제목}

## 대상 워크플로우
## 테스트 단계
## 기대 결과
## 실제 결과
## 발견된 버그 (있으면)
## 상태: PASS / FAIL / BLOCKED
```

### 규칙

- 테스트 시 백엔드(`./gradlew bootRun`) + 프론트엔드(`cd frontend && npm run dev`) 반드시 동시 실행
- Chrome 익스텐션으로 실제 UI 조작하여 검증 (API 호출만으로 대체 불가)
- FAIL 또는 BLOCKED인 항목이 하나라도 있으면 수정 → 재검증 반복
- 모든 test-case가 `_done.md`로 완료될 때까지 QA 라운드를 종료하지 않는다
- 수정이 필요한 버그는 `docs/issues/` 에도 등록하여 추적한다

---

## 백엔드-프론트엔드 API 계약 규칙

### 필드 동기화

- 백엔드 응답 DTO 필드명과 프론트엔드 TypeScript 타입 필드명은 **반드시 1:1 대응**
- 새 API를 만들 때 백엔드 DTO와 프론트엔드 타입을 **동시에** 정의하고 대조
- nullable 필드는 양쪽 모두 명시 (백엔드: `?`, 프론트엔드: `| null`)

### 방어 코딩

- 프론트엔드에서 API 응답의 숫자 필드 사용 시 반드시 fallback 처리 (`?? 0`, `?.toFixed()`)
- API 에러(4xx/5xx) 발생 시 사용자에게 토스트 등 시각적 피드백 필수

### 페이지네이션

- `PageImpl` 직접 직렬화 금지 → Spring Data `PagedModel` 또는 커스텀 응답 DTO 사용
- `@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)` 적용 권장

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
- **팀 작업 중 CLAUDE.md를 수정하지 않는다** — CLAUDE.md는 사용자만 수정

#### Before Implementation
- FE agent MUST read backend response DTO source code and verify field-name
  alignment with TypeScript types before writing any component code
- If docs/features/todo/{feature}.md references an API, verify the endpoint
  exists and response shape matches before starting

#### During Implementation
- BE agent: run integration tests after each use case implementation
  (`./gradlew build`). Do not proceed to next use case if tests fail
- FE agent: run `npm run build` after each feature component.
  Fix type errors before moving on

#### After Implementation (Pre-merge)
- BE agent: all integration tests pass (`./gradlew build`)
- FE agent: build succeeds (`cd frontend && npm run build`)
- Playwright E2E: `cd frontend && npx playwright test` — all specs pass

#### Cross-cutting Changes (BE DTO ↔ FE Type)
- When a BE agent modifies a response DTO, it MUST message the FE agent
  with: changed endpoint, old vs new field names, nullable changes
- FE agent updates TypeScript types and confirms build passes
- Both agents verify independently before reporting done to leader
- If working as a single agent (no team), handle both sides sequentially:
  BE DTO → FE type sync → both builds pass

### Model Assignment
- Leader, Architect, Code Reviewer: Opus
- Implementation, Planning drafts: Sonnet
