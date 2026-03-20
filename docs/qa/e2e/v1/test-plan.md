# E2E Test Plan - v1

## Overview

Playwright E2E tests for the Overload Manager web app.
- Framework: Playwright (chromium)
- Location: `frontend/tests/`
- Run: `cd frontend && npx playwright test`

## Test Summary

| Status | Count |
|--------|-------|
| PASS   | 15    |
| FIXME  | 5     |
| FAIL   | 0     |

## Test Cases

### auth.spec.ts

| ID      | Description                                      | Status |
|---------|--------------------------------------------------|--------|
| AUTH-01 | Register form displays when register tab clicked | PASS   |
| AUTH-02 | Validation errors on empty register form submit  | PASS   |
| AUTH-03 | Successful registration navigates to dashboard   | PASS   |
| AUTH-04 | Login form displays by default                   | PASS   |
| AUTH-05 | Successful login navigates to dashboard          | PASS   |
| AUTH-06 | Unauthenticated access to / redirects to /auth   | PASS   |
| AUTH-07 | Unauthenticated access to /report redirects to /auth | PASS |

### dashboard.spec.ts

| ID      | Description                                      | Status |
|---------|--------------------------------------------------|--------|
| DASH-01 | Greeting and date display after login            | PASS   |
| DASH-02 | Weekly summary card displays                     | PASS   |
| DASH-03 | Start workout button visible                     | PASS   |
| DASH-04 | Bottom tab bar with home and report tabs         | PASS   |
| DASH-05 | Navigate to report page via tab bar              | PASS   |
| DASH-06 | Navigate back to home via tab bar                | PASS   |
| DASH-07 | Workout tab navigation (not implemented)         | FIXME  |
| DASH-08 | History tab navigation (not implemented)         | FIXME  |
| DASH-09 | Session creation (BUG-09: 500 error)             | FIXME  |

### report.spec.ts

| ID      | Description                                      | Status |
|---------|--------------------------------------------------|--------|
| RPT-01  | Report page displays with exercise/weekly tabs   | PASS   |
| RPT-02  | Exercise select dropdown visible                 | PASS   |
| RPT-03  | Exercise dropdown loads exercise list (BUG-12)   | FIXME  |
| RPT-04  | Weekly summary tab displays without crash (BUG-13) | FIXME |

## Known Bugs Blocking Tests

- **BUG-09**: POST /api/v1/sessions returns 500 (JWT principal casting issue)
- **BUG-12**: Exercise list not loading in report dropdown
- **BUG-13**: Weekly summary view crashes due to response field name mismatch (totalVolume vs totalVolumeKg)
