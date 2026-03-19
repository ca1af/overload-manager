# 운동 세션 (Workout Session)

## Overview
날짜별 운동 세션 생성, 세션에 운동 추가/제거, 세션 종료 및 요약 기능. 운동 기록의 기본 단위.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P0-3)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 2, Flow 5)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.2, 3.5)

## Backend
- **엔티티**: WorkoutSession, SessionExercise
- **유스케이스**: CreateSessionUseCase, ManageSessionExerciseUseCase
- **API 엔드포인트**:
  - `GET /api/v1/sessions` — 세션 목록 조회 (날짜 범위, 페이지네이션)
  - `POST /api/v1/sessions` — 세션 생성
  - `GET /api/v1/sessions/{sessionId}` — 세션 상세 조회
  - `PATCH /api/v1/sessions/{sessionId}` — 세션 수정/종료 (`finished: true` 시 서버 계산)
  - `DELETE /api/v1/sessions/{sessionId}` — 세션 삭제
  - `POST /api/v1/sessions/{sessionId}/exercises` — 운동 추가 (복수)
  - `DELETE /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}` — 운동 제거
- **비즈니스 로직**: 동일 날짜 복수 세션 허용, 종료 시 durationSeconds 자동 계산

## Frontend
- **페이지**: DashboardPage (`/`), ActiveSessionPage (`/sessions/:id`)
- **컴포넌트**: SessionCard, SessionExerciseCard, SessionElapsedTimer, SessionSummaryModal, StartWorkoutButton
- **상태**: TanStack Query (`useSessionDetail`, `useCreateSessionMutation`), Zustand (activeSessionStore - 경과 타이머)
- **인터랙션**: "오늘 운동 시작" 버튼, 세션 경과 타이머, 운동 카드 탭으로 세트 기록 진입, 세션 종료 요약 모달

## Acceptance Criteria
- 오늘 날짜로 새 세션 생성 가능
- 세션에 복수 운동 추가/제거 가능
- 세션 진행 중 경과 시간 표시
- 세션 종료 시 총 운동 수, 세트, 볼륨, 소요 시간 요약 표시
- 대시보드에서 최근 세션 목록 조회 가능
