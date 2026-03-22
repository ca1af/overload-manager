# QA & E2E Testing Guide

Read `CLAUDE.md` first for shared rules. This guide covers QA-specific workflows.

All teams (BE, FE, Planning) reference this guide for verification processes.

---

## E2E Testing — Playwright (Automated)

### Location & Config

- Tests: `frontend/tests/*.spec.ts`
- Config: `frontend/playwright.config.ts`
- `webServer` option auto-starts backend (8080) + frontend (5173)

### Commands

```bash
cd frontend && npx playwright test                    # Run all
cd frontend && npx playwright test --ui               # UI debug mode
cd frontend && npx playwright test tests/auth.spec.ts # Specific file
```

### Rules

- Naming: `{feature}.spec.ts` (e.g. `auth.spec.ts`, `session.spec.ts`)
- One feature area per spec file
- Page Object pattern: introduce only when complexity demands it
- Data isolation: each test is independent. H2 resets on server restart. No shared state.
- Waiting: use `waitForURL`, `waitForResponse`. **No hardcoded `sleep`.**
- Assertions: use Playwright built-in matchers (`toHaveURL`, `toBeVisible`)
- CI: headless by default. `--trace on` for failure diagnostics.
- Known bugs: mark as `test.fixme()` with bug ID in description

---

## E2E Testing — Chrome Extension (Exploratory)

For ad-hoc verification during development:

```bash
# Terminal 1: backend
./gradlew bootRun

# Terminal 2: frontend
cd frontend && npm run dev

# Then use claude-in-chrome extension for workflow testing
```

---

## QA Round Process

### Flow

```
1. Define test items → docs/qa/e2e/v{N}/test-plan.md
2. Execute per item → chrome extension or Playwright
3. Record results → docs/qa/e2e/v{N}/{test-case}.md
4. Fix & re-verify → rename to {test-case}_done.md on pass
5. Do NOT finish until ALL items are _done.md
```

### Test Plan Format (`test-plan.md`)

```markdown
# E2E Test Plan - v{N}

## Overview
## Test Summary (PASS / FAIL / FIXME counts)
## Test Cases (table: ID, Description, Status)
## Known Bugs Blocking Tests
```

### Test Case Format (`{test-case}.md`)

```markdown
# TC-{N}: {Title}

## Target Workflow
## Test Steps
## Expected Result
## Actual Result
## Bugs Found (if any)
## Status: PASS / FAIL / BLOCKED
```

### Status Lifecycle

```
{test-case}.md (FAIL/BLOCKED) → fix → re-verify → {test-case}_done.md (PASS)
```

### Rules

- Backend + frontend must both be running during testing
- Verify with real UI interaction. API-only checks do not count.
- Any FAIL or BLOCKED → fix → re-verify cycle
- Bugs found → also log in `docs/issues/`
- QA round is NOT complete until every test-case file ends with `_done.md`

---

## "Done" Criteria (All Teams)

A feature is "done" only when:
1. Unit/integration tests pass (`./gradlew build` or `npm run build`)
2. Playwright E2E tests pass for related workflows
3. Corresponding QA round test-cases are all `_done.md`

---

## Verification Duties by Team

### Backend

- After implementation: `./gradlew build` passes
- If DTO changed: notify FE, wait for type sync confirmation
- See [`docs/conventions/backend/GUIDE.md`](../backend/GUIDE.md) for details

### Frontend

- Before implementation: read backend DTO source, verify field alignment
- After implementation: `npm run build` + `npx playwright test` both pass
- See [`docs/conventions/frontend/GUIDE.md`](../frontend/GUIDE.md) for details

### Planning

- After handoff: verify `docs/features/todo/{feature}.md` is complete
- See [`docs/conventions/planning/GUIDE.md`](../planning/GUIDE.md) for details

---

## User Bug Report Handling

When a user reports an issue through conversation:

- Claude creates `docs/issues/bug_{n}.md` using the template in `CLAUDE.md`. The user never writes issue files.
- Claude investigates the root cause, implements the fix, and asks the user to verify.
- On user confirmation that the bug is fixed, rename `bug_{n}.md` → `bug_{n}_resolved.md` and set `## Status: RESOLVED`.
- If the fix is not confirmed, keep the file as-is and continue the fix-verify cycle.
- Bug files without `_resolved.md` suffix are treated as open issues by all teams.
