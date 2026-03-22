# Planning Team Guide

Read `CLAUDE.md` first for shared rules. This guide covers planning-specific workflows.

---

## Role Boundary

Planning team defines **what** to build.
**How** to build (tech stack, architecture, DB) belongs to engineering.
Do not include framework names or technical constraints in planning docs.

---

## Requirements Lifecycle

```
backlog/ → approved/ → archive/
```

### Stage 1: Backlog (`docs/planning/requirements/backlog/`)

Anyone can propose. File: `REQ-{n}.md`, n is sequential.

```markdown
# REQ-{n}: {Title}

## Status: PROPOSED | UNDER_REVIEW | NEEDS_INFO
## Proposed by: {team/person}
## Date: {YYYY-MM-DD}

## Problem Statement
## Proposed Solution
## User Impact
## Priority Suggestion: P0 | P1 | P2
## Open Questions
```

### Stage 2: Approved (`docs/planning/requirements/approved/`)

On approval:
1. Move from `backlog/` to `approved/`
2. Set status to `APPROVED`, assign final priority
3. Begin PRD breakdown

### Stage 3: Archive (`docs/planning/requirements/archive/`)

Move when:
- **Completed**: all derived features in `docs/features/done/`. Status → `COMPLETED`
- **Rejected**: add `## Rejection Reason`. Status → `REJECTED`
- **Superseded**: link to replacement. Status → `SUPERSEDED`

---

## Deliverables

3 documents per approved requirement. All text-based markdown.

### 1. PRD (`docs/planning/prd/{feature-name}.md`)

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

### 2. User Flow (`docs/planning/user-flows/{feature-name}.md`)

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

### 3. Wireframe (`docs/planning/wireframes/{feature-name}.md`)

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

---

## Work Standards

1. PRD first. User flows and wireframes derive from PRD
2. Competitor analysis in `docs/planning/competitors/` — benchmark strengths, improve weaknesses
3. One feature per file. Never mix features
4. Tech-neutral wireframes. No framework assumptions
5. Wireframes must include empty state, loading, error cases
6. Every PRD must reference source requirement via `## Source Requirement`

---

## Planning → Engineering Handoff

1. Complete PRD + user flow + wireframes in `docs/planning/`
2. For each P0 feature, create `docs/features/todo/{feature-name}.md`:
   - Summarized requirements, user flow, wireframe
   - Reference links to planning originals
3. Engineering works from `docs/features/todo/` only

---

## Planning Team Workflow (Agent Teams)

1. Leader defines scope, creates task list
2. PM drafts PRD → leader approval
3. On approval, UX writes user flows + wireframes
4. Competitor analyst works in parallel
5. Leader reviews consistency across all deliverables
6. Generate `docs/features/todo/` handoff for P0 features

---

## User Intake Protocol

- 요청된 기능의 PRD가 없으면, 대화를 통해 **목적, 대상 사용자, 핵심 기능, 제약사항**을 파악한 뒤 PRD를 작성한다.
- PRD 또는 wireframe 작성 후, 사용자에게 요약을 제시하고 확인을 받은 뒤 `## User Confirmation: YES`를 문서에 추가한다.
- 사용자가 수정을 요청하면 문서를 갱신하고 `## User Confirmation: REVISION_REQUESTED`로 표기한다. 재확인 후 `YES`로 변경.
- PRD에 `## User Confirmation: YES`가 없는 기능은 구현 팀에 핸드오프하지 않는다.
