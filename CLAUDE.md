# Project Rules

This document is auto-loaded at every Claude Code session start.
Read this fully before beginning any work.

---

## Tech Stack

- Backend: Spring Boot + Kotlin
- Architecture: Hexagonal Architecture + DDD
- Build: Gradle (multi-module)
- Frontend stack: see docs/design/architecture.md
- Tech stack decisions belong to the engineering team. Planning team does not participate.

---

## docs Directory Structure

Check if the target directory exists before creating files. Create it if missing.

```
docs/
├── planning/                        # Planning artifacts (non-technical)
│   ├── requirements/                # Requirements lifecycle
│   │   ├── backlog/                 # Proposed (not yet approved)
│   │   ├── approved/               # Approved, ready for PRD
│   │   └── archive/                # Completed / rejected / superseded
│   ├── prd/                         # PRD per feature
│   ├── user-flows/                  # User flow per feature
│   ├── wireframes/                  # Wireframe descriptions (text)
│   └── competitors/                 # Competitor analysis
│
├── design/                          # Technical design (BE/FE shared)
│   ├── architecture.md
│   ├── erd.md
│   └── api-spec.md
│
├── features/                        # Feature tracking (BE/FE combined)
│   ├── todo/                        # Pending implementation
│   └── done/                        # Moved from todo/ on completion
│
├── conventions/                     # Code conventions (BE/FE split)
│   ├── backend/
│   └── frontend/
│
├── testing/                         # Test standards (BE/FE split)
│   ├── backend/
│   └── frontend/
│
├── issues/                          # Blocking issue tracker
│
├── sessions/                        # Team handoff
│   ├── archive/
│   └── SESSION_{TEAMNAME}.md
│
└── README.md
```

---

## Planning Team Rules

### Role Boundary

Planning team defines **what** to build. **How** to build (tech stack, architecture, DB) belongs to engineering. Do not include framework names or technical constraints in planning docs.

### Requirements Lifecycle

```
backlog/ → approved/ → archive/
```

#### Stage 1: Backlog (docs/planning/requirements/backlog/)

Anyone can propose. File: `REQ-{n}.md`, n is sequential.

```markdown
# REQ-{n}: {Title}

## Status: PROPOSED | UNDER_REVIEW | NEEDS_INFO
## Proposed by: {team/person}
## Date: {YYYY-MM-DD}

## Problem Statement
- What problem? Who is affected?

## Proposed Solution
- High-level desired outcome

## User Impact
- Scope and criticality

## Priority Suggestion
- P0 (MVP blocker) | P1 (post-MVP) | P2 (future)

## Open Questions
- Unresolved items before approval
```

#### Stage 2: Approved (docs/planning/requirements/approved/)

On approval:
1. Move from `backlog/` to `approved/`
2. Set status to `APPROVED`, assign final priority
3. Planning team begins PRD breakdown

#### Stage 3: Archive (docs/planning/requirements/archive/)

Move when:
- **Completed**: all derived features in `docs/features/done/`. Status: `COMPLETED`
- **Rejected**: add `## Rejection Reason`. Status: `REJECTED`
- **Superseded**: link to replacement. Status: `SUPERSEDED`

### Planning Deliverables

3 documents per approved requirement. All text-based markdown.

#### 1. PRD (docs/planning/prd/{feature-name}.md)

```markdown
# {Feature Name} PRD

## Source Requirement
- REQ-{n}: {path}

## Background & Purpose
## Target Users
## User Stories
- As a [user], I want to [action], so that [goal]
- Sort by P0 > P1 > P2

## Functional Requirements
### P0 (MVP)
### P1 (Post-MVP)
### P2 (Future)

## Non-Functional Requirements
## MVP Scope
## Success Metrics
```

#### 2. User Flow (docs/planning/user-flows/{feature-name}.md)

```markdown
# {Feature Name} User Flow

## Main Flow
1. User enters [entry point]
2. ...

## Branch Flows
### Case A: [condition]
### Case B: [condition]

## Exception Flows
### [Error]
- User message
- Recovery path
```

#### 3. Wireframe (docs/planning/wireframes/{feature-name}.md)

```markdown
# {Feature Name} Wireframes

## {Screen Name}

### Layout
- Top / Center / Bottom areas

### Key UI Elements
- [Element]: position, shape, role

### Interactions
- Click [element] → [result]

### State Variations
- Loading / Empty / Error
```

### Planning Work Standards

1. PRD first. User flows and wireframes derive from PRD
2. Competitor analysis in `docs/planning/competitors/` — benchmark strengths, improve weaknesses
3. One feature per file. Never mix features
4. Tech-neutral wireframes. No framework assumptions
5. Wireframes must include empty state, loading, error cases
6. Every PRD must reference source requirement via `## Source Requirement`

### Planning → Engineering Handoff

1. Complete PRD + user flow + wireframes in `docs/planning/`
2. For each P0 feature, create `docs/features/todo/{feature-name}.md`:
    - Summarized requirements, user flow, wireframe
    - Reference links to planning originals
3. Engineering works from `docs/features/todo/` only

---

## Engineering Team Code Standards

### Architecture

- Hexagonal Architecture
- DDD conventions
- Reference people module for package structure and naming

### Testing

- Domain layer: unit tests required
- Application layer (use cases): integration tests required
- "Done" criteria:
    - All integration tests matching docs/features/todo/ spec pass
    - Zero compilation errors

### Build & Verify

```bash
./gradlew :apps:asset:asset-domain:compileKotlin
./gradlew :apps:asset:asset-application:compileKotlin
./gradlew :apps:asset:asset-infra:compileKotlin
./gradlew :apps:asset:asset-domain:test
./gradlew :apps:asset:asset-application:test
```

---

## Blocking Issue Management

Applies to both planning and engineering teams.

- Log as `docs/issues/issue_{n}.md` (n = next sequential)
- **Current team does NOT resolve. Document only.**
- **Next team** resolves as top priority on start
- Rename to `solved_{n}.md` when resolved

```markdown
# Issue {n}: {Title}

## Reported by: {team}
## Date: {YYYY-MM-DD}
## Blocking: {what is blocked}

## Description
## Reproduction / Context
## Attempted Solutions
## Suggested Resolution
```

---

## Team Handoff

### On Completion

Write `docs/sessions/SESSION_{TEAMNAME}.md`:

```markdown
# SESSION_{TEAMNAME}

## Completed Work
- Tasks performed and outcomes

## Instructions for Next Team
- Remaining items
- UX assessment of current API / screen design
- Competitor benchmarking if applicable

## Unresolved Issues
- Open files in docs/issues/ with descriptions
```

### On Start (Next Team)

1. Resolve any open `docs/issues/issue_{n}.md` first
2. Use previous SESSION doc to build work plan
3. Move previous SESSION doc to `docs/sessions/archive/`
4. Never delete. Always archive.

---

## Feature Spec (docs/features/todo/)

```markdown
# {Feature Name}

## Overview

## Planning References
- PRD: docs/planning/prd/{feature-name}.md
- User Flow: docs/planning/user-flows/{feature-name}.md
- Wireframe: docs/planning/wireframes/{feature-name}.md
- Requirement: docs/planning/requirements/approved/REQ-{n}.md

## Backend
- Entities, services, API endpoints, business logic

## Frontend
- Screens, components, interaction flow

## Acceptance Criteria
- Conditions for "done"
```

Move to `docs/features/done/` on completion.

---

## Agent Team Operating Rules

### Common

- All teammates use worktrees for isolation
- Leader uses delegation mode; does not write code
- Implementation teammates split by package; no overlapping file edits

### Model Assignment

- Leader, Architect, Code Reviewer: Opus
- Implementation, Planning drafts: Sonnet

### Planning Team Workflow

1. Leader defines scope, creates task list
2. PM drafts PRD → leader approval
3. On approval, UX writes user flows + wireframes
4. Competitor analyst works in parallel
5. Leader reviews consistency across all deliverables
6. Generate docs/features/todo/ handoff for P0 features

### Engineering Team Workflow

1. Leader builds plan, creates task list
2. Architect writes design docs → leader approval
3. On approval, implementers start parallel work
4. Code reviewer reviews completed work
5. Leader verifies integration and tests after review fixes
6. Commit to designated branch after all tests pass