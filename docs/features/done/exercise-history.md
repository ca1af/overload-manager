# 운동 히스토리 (Exercise History)

## Overview
운동별 과거 기록 목록 조회. 세션별 세트 기록, 최대 중량, 총 볼륨, 추정 1RM을 시간순으로 확인.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P0-6)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 6)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.6)

## Backend
- **API 엔드포인트**:
  - `GET /api/v1/exercises/{exerciseId}/history` — 운동별 히스토리 목록
    - 파라미터: from, to, page, size
    - 응답: 세션별 세트 목록, maxWeightKg, totalVolumeKg, estimatedOneRepMax
- **비즈니스 로직**: Epley 공식으로 추정 1RM 계산 (`weight x (1 + reps / 30)`)

## Frontend
- **페이지**: ReportPage (`/report`) - 운동별 탭
- **컴포넌트**: ExerciseSelector (드롭다운), PeriodFilterGroup, MaxWeightTrendChart (라인차트), SessionHistoryList, SessionHistoryRow
- **상태**: TanStack Query (`useExerciseHistory`)
- **인터랙션**:
  - 운동 드롭다운 선택으로 해당 운동 히스토리 표시
  - 기간 필터(1개월/3개월/6개월/전체)로 범위 조정
  - 차트 포인트 탭으로 세션 상세 확인
  - 세션 기록 행 탭으로 세션 상세 화면 이동

## Acceptance Criteria
- 특정 운동의 과거 기록을 시간순으로 조회 가능
- 세션별 최대 중량, 총 볼륨, 추정 1RM 표시
- 최대 중량 추이 그래프(라인차트) 표시
- 기간 필터링 지원
