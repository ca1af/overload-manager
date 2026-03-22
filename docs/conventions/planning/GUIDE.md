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

- If no PRD exists for the requested feature/project, gather **purpose, target users, core features, and constraints** from the user through conversation, then draft the PRD.
- After writing a PRD or wireframe, present a summary to the user and ask for confirmation before adding `## User Confirmation: YES` to the document.
- If the user requests revisions, update the document and mark `## User Confirmation: REVISION_REQUESTED` until the user re-confirms.
- No implementation team may begin work on a feature whose PRD lacks `## User Confirmation: YES`.
