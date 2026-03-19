# 볼륨 트렌드 차트 (Volume Trend Chart)

## Overview
운동별 주간/월간 총 볼륨(세트 x 반복수 x 무게) 시각화. 바 차트로 볼륨 추이를 한눈에 파악하여 충분한 훈련 자극을 주고 있는지 확인.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P1-2)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 6)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.6)

## Backend
- **API 엔드포인트**:
  - `GET /api/v1/exercises/{exerciseId}/volume-trend` — 볼륨 트렌드
    - 파라미터: period (WEEKLY/MONTHLY), from, to
    - 응답: 기간별 totalVolumeKg, maxWeightKg
- **비즈니스 로직**: 주간/월간 단위 볼륨 집계

## Frontend
- **페이지**: ReportPage - 운동별 탭
- **컴포넌트**: WeeklyVolumeChart (Recharts BarChart), AverageVolumeDisplay
- **상태**: TanStack Query (`useVolumeTrend`)

## Acceptance Criteria
- 운동별 주간/월간 볼륨 바 차트 표시
- 이번 주 바 강조 표시
- 평균 주간 볼륨 수치 표시
- 기간 필터(1개월/3개월/6개월/전체) 지원
