# Project Rules

This document is auto-loaded at every Claude Code session start.
Read this fully before beginning any work.

---

## Team Guides — Read Your Team's Guide First

| Team | Guide | Scope |
|------|-------|-------|
| **Planning** (기획/디자인/PM) | [`docs/conventions/planning/GUIDE.md`](docs/conventions/planning/GUIDE.md) | Requirements lifecycle, PRD, user flows, wireframes |
| **Backend** (BE) | [`docs/conventions/backend/GUIDE.md`](docs/conventions/backend/GUIDE.md) | Hexagonal architecture, naming, API contracts, testing |
| **Frontend** (FE) | [`docs/conventions/frontend/GUIDE.md`](docs/conventions/frontend/GUIDE.md) | React/TypeScript stack, project structure, API integration |
| **QA** (공통) | [`docs/conventions/qa/GUIDE.md`](docs/conventions/qa/GUIDE.md) | E2E testing (Playwright + Chrome), QA round process |

**Every agent must read this file + their team's guide before starting work.**

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
│   ├── planning/GUIDE.md
│   ├── backend/GUIDE.md
│   ├── frontend/GUIDE.md
│   └── qa/GUIDE.md
├── qa/
│   └── e2e/
│       └── v{N}/           # Versioned E2E test rounds
│           ├── test-plan.md
│           ├── {test-case}.md
│           └── {test-case}_done.md
├── issues/                 # Blocking issue tracker
├── sessions/               # Team handoff
│   └── archive/
└── README.md
```

---

## Feature Spec (`docs/features/todo/`)

```markdown
# {Feature Name}

## Overview
## Planning References
## Backend
## Frontend
## Acceptance Criteria
## User Confirmation
```

Move to `docs/features/done/` on completion.

---

## Blocking Issue Management

- Log as `docs/issues/issue_{n}.md`
- Current team documents only, does NOT resolve
- Next team resolves as top priority

---

## User Collaboration Pipeline

사용자는 대화를 통해 요구사항을 전달하고, Claude가 모든 문서를 작성한다.
파일 존재 여부와 파일 내 마커가 파이프라인 게이트 역할을 한다.

### Pipeline Gate Rules

- `docs/planning/prd/{feature}.md`에 `## User Confirmation: YES`가 없으면 → 구현 착수 금지
- `docs/planning/wireframes/{feature}.md`에 `## User Confirmation: YES`가 없으면 → FE 구현 착수 금지
- 사용자가 새 기능을 요청했는데 해당 PRD가 없으면 → 대화로 요구사항 파악 후 PRD 작성
- 사용자가 버그 보고 → Claude가 `docs/issues/bug_{n}.md` 생성, 해결 시 `bug_{n}_resolved.md`로 rename
- 사용자가 변경 요청 → Claude가 `docs/issues/cr_{n}.md` 생성, 사용자 동의 후 `## User Confirmation: YES` 추가

### Document Markers

Claude가 관리하며, 사용자가 직접 작성하지 않는다.

| Marker | 의미 | 적용 파일 |
|--------|------|-----------|
| `## User Confirmation: YES` | 사용자가 대화에서 확인 완료 | PRD, wireframe, CR, feature spec |
| `## User Confirmation: REVISION_REQUESTED` | 사용자가 수정 요청 | PRD, wireframe, CR |
| `## Status: RESOLVED` | 이슈 해결 완료 | bug, CR 파일 |

### Issue File Templates

Claude는 사용자 보고 시 아래 형식으로 파일을 생성한다.

**Bug Report** (`docs/issues/bug_{n}.md`):

```markdown
# BUG-{n}: {제목}

## Reported: {YYYY-MM-DD}
## Screen / Page
## What Happened
## Expected Behavior
## Reproduction Steps
## Status: OPEN | IN_PROGRESS | RESOLVED
```

**Change Request** (`docs/issues/cr_{n}.md`):

```markdown
# CR-{n}: {제목}

## Reported: {YYYY-MM-DD}
## Current Behavior
## Desired Behavior
## Affected Features
## Priority: P0 | P1 | P2
## User Confirmation
## Status: OPEN | IN_PROGRESS | RESOLVED
```

---

## "Done" Criteria (All Teams)

1. All unit/integration tests pass
2. Zero compilation errors (`./gradlew build` + `npm run build`)
3. E2E tests pass for related workflows (see [QA Guide](docs/conventions/qa/GUIDE.md))

---

## Agent Team Operating Rules

### Common

- All teammates use worktrees for isolation
- Leader uses delegation mode; does not write code
- Implementation teammates split by package; no overlapping file edits
- **팀 작업 중 CLAUDE.md를 수정하지 않는다** — CLAUDE.md는 사용자만 수정

### 구현 후 검증 의무

- 백엔드 구현 에이전트: 작업 완료 전 통합 테스트를 실행하여 정상 작동하는지 확인
- 프론트엔드 구현 에이전트: 백엔드 응답 DTO 코드를 직접 읽어 TypeScript 타입과 대조한 후 작업 시작
- 풀스택 수정 시: 백엔드 DTO 변경 → 프론트엔드 타입 동기화 → 빌드 확인까지 한 에이전트가 담당

### Model Assignment

- Leader, Architect, Code Reviewer: Opus
- Implementation, Planning drafts: Sonnet
