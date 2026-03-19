# 세트 기록 (Set Recording)

## Overview
세트별 무게(kg)·반복수 입력, 완료 체크, 세트 추가/수정/삭제. 자동 저장(debounce 500ms) 지원.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P0-4)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 3)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.5)

## Backend
- **엔티티**: WorkoutSet
- **유스케이스**: RecordSetUseCase
- **API 엔드포인트**:
  - `POST /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets` — 세트 추가 (setNumber 서버 자동 채번)
  - `PATCH /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setId}` — 세트 수정/완료
  - `DELETE /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setId}` — 세트 삭제
- **비즈니스 로직**: weight는 항상 kg 저장, completed=true 시 completedAt 기록, setNumber 자동 채번

## Frontend
- **페이지**: SetRecordPage (`/sessions/:id/exercises/:sessionExerciseId`)
- **컴포넌트**: SetTable, SetRow, AddSetButton, TotalVolumeDisplay, WeightInput, RepsInput, CompleteCheckbox
- **상태**: TanStack Query (낙관적 업데이트), useAutoSave (debounce 500ms)
- **인터랙션**:
  - 무게 -> 반복수 -> 완료 체크 순차 입력 (Tab 키 지원)
  - 숫자 키패드 자동 활성
  - 이전 세션 값 placeholder 표시
  - 세트 행 좌 스와이프로 삭제
  - 실시간 총 볼륨 계산 (완료 세트만)
  - 자동 저장: 입력 후 500ms debounce

## Acceptance Criteria
- 세트별 무게·반복수 입력 가능
- 완료 체크로 세트 완료 처리
- 세트 추가/수정/삭제 가능
- 자동 저장 (debounce 500ms)
- 실시간 총 볼륨 계산 표시
- 무게 0 이하, 반복수 0 이하 입력 방지
