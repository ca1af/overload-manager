# 휴식 타이머 (Rest Timer)

## Overview
세트 완료 후 카운트다운 휴식 타이머. 적절한 세트 간 휴식 시간을 지킬 수 있도록 지원하는 프론트엔드 전용 기능.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P1-4 - MVP 포함)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 3)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.5)

## Backend
- 휴식 타이머 자체는 프론트엔드 전용 기능
- 실제 휴식 시간은 세트 완료 시 `restSeconds` 필드로 서버에 기록
  - `PATCH /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setId}` — `restSeconds` 포함

## Frontend
- **컴포넌트**: RestTimerOverlay (원형 프로그레스 + 카운트다운)
- **상태**: Zustand `restTimerStore` (isRunning, remainingSeconds, defaultDurationSeconds)
- **인터랙션**:
  - 세트 완료 체크 시 자동 팝업
  - 원형 프로그레스 바 + 대형 카운트다운 숫자
  - "+30초" 버튼으로 연장
  - "건너뜀" 버튼으로 즉시 종료
  - 타이머 종료 시 다음 세트 무게 입력 포커스 이동
  - 반투명 오버레이 형태

## Acceptance Criteria
- 세트 완료 체크 시 휴식 타이머 자동 시작
- 카운트다운 표시 (원형 프로그레스 + 숫자)
- +30초 연장 기능
- 건너뛰기 기능
- 타이머 종료 시 다음 세트 입력으로 포커스 이동
