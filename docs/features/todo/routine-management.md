# 루틴 관리 (Routine Management)

## Overview
자주 사용하는 운동 조합을 루틴 템플릿으로 저장하고, 세션 시작 시 불러와 적용하는 기능. 매번 운동을 일일이 추가하지 않고 빠르게 세션을 시작할 수 있다.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P1-5)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 2)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.2)

## Backend
- **엔티티**: Routine, RoutineExercise
- **API 엔드포인트**:
  - `GET /api/v1/routines` — 루틴 목록 조회
  - `POST /api/v1/routines` — 루틴 생성
  - `POST /api/v1/routines/{routineId}/apply` — 루틴을 세션에 적용
  - `DELETE /api/v1/routines/{routineId}` — 루틴 삭제 (소프트 삭제)
- **비즈니스 로직**: 루틴 적용 시 SessionExercise 레코드 자동 생성, 목표 세트/반복수 포함

## Frontend
- **컴포넌트**: 루틴 선택 화면, 루틴 생성/편집 폼
- **인터랙션**: 대시보드에서 "저장된 루틴 불러오기"로 세션 생성, 루틴 목록/생성/삭제

## Acceptance Criteria
- 운동 조합을 루틴으로 저장 가능
- 루틴에 운동별 목표 세트/반복수 설정 가능
- 저장된 루틴을 세션에 적용하여 빠른 세션 시작
- 루틴 삭제 (소프트 삭제)
