# Frontend Engineering Guide

Read `CLAUDE.md` first for shared rules. This guide covers frontend-specific standards.

---

## Tech Stack

- React 19 + TypeScript 5.9 + Vite 8
- Routing: React Router 7
- Server state: TanStack Query v5
- Client state: Zustand v5
- UI: shadcn/ui (Radix UI) + Tailwind CSS 4
- Chart: Recharts 3
- Form: React Hook Form + Zod
- HTTP: Axios (JWT interceptor)
- Bottom sheet: vaul
- Date: date-fns 4

---

## Project Structure

Root: `frontend/`

```
src/
├── api/              # Axios instance + API functions per domain
├── components/       # Shared (AppHeader, BottomTabBar, RestTimerOverlay)
│   └── ui/           # shadcn/ui primitives (Button, Input, Card, etc.)
├── features/         # Feature modules
│   ├── auth/
│   ├── exercise/
│   ├── session/
│   └── report/
├── hooks/            # useDebounce, useTimer
├── pages/            # Route pages
├── router/           # React Router config + ProtectedRoute
├── store/            # Zustand stores (authStore, sessionStore)
├── types/            # TypeScript domain types
└── utils/            # weight conversion, date formatting, cn()
```

---

## Build & Verify

```bash
cd frontend && npm run build    # Type check + production build
cd frontend && npm run dev      # Dev server (port 5173)
```

---

## API Integration Rules

### Type Safety

- Response DTO field names must **1:1 match** backend DTO field names
- **Before writing any component**, read the backend response DTO source code
  and verify field alignment with TypeScript types
- Nullable fields: explicit `| null` in TypeScript types

### Defensive Coding

- Numeric fields from API: always use fallback (`?? 0`, `?.toFixed()`)
- API errors (4xx/5xx): show toast or visual feedback to user. Never silent fail.

### Axios Setup

- JWT interceptor handles token refresh automatically
- Base URL: `/api/v1` (Vite proxy forwards to backend 8080)

---

## Testing & QA

- **Build check**: `cd frontend && npm run build`
- **E2E (Playwright)**: `cd frontend && npx playwright test`
- For full E2E testing process, Playwright rules, and QA round workflow, see [`docs/conventions/qa/GUIDE.md`](../qa/GUIDE.md)

---

## Verification Duties

### Before Implementation

- Read backend response DTO source code
- Verify field-name alignment with TypeScript types
- If `docs/features/todo/{feature}.md` references an API, confirm endpoint exists

### During Implementation

- Run `npm run build` after each feature component
- Fix type errors before moving on

### After Implementation (Pre-merge)

- Build succeeds: `cd frontend && npm run build`
- Playwright E2E: `cd frontend && npx playwright test` — all specs pass

### Cross-cutting Changes (DTO changed by BE)

When notified of a backend DTO change:
1. Update TypeScript types in `src/types/`
2. Update API function return types in `src/api/`
3. Confirm `npm run build` passes
4. Report completion to leader

---

## Frontend Agent Team Workflow

1. Leader builds plan, creates task list
2. Implementers work on assigned feature modules (split by `src/features/`)
3. Shared components (`src/components/`) changes require leader coordination
4. After implementation, run Playwright E2E
5. Leader verifies build + E2E pass
6. Commit to designated branch
