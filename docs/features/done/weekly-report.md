# 주간 요약 리포트 (Weekly Report)

## Overview
주간 운동 요약 정보 제공. 운동 횟수, 총 볼륨, 과부하 달성 현황, 지난 주 대비 볼륨 변화율을 한눈에 파악.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P1 - MVP 포함)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 6)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.2, 3.6)

## Backend
- **API 엔드포인트**:
  - `GET /api/v1/reports/weekly-summary` — 주간 요약 리포트
    - 파라미터: date (해당 주의 임의 날짜, default: 오늘)
    - 응답: weekStart, weekEnd, sessionCount, weeklyGoalSessions, totalSets, totalVolumeKg, previousWeekVolumeKg, volumeChangePercent, overloadAchieved 배열
- **비즈니스 로직**: 월~일 기준 주간 집계, 이전 주 대비 볼륨 변화율 계산, 운동별 과부하 달성 여부 판정

## Frontend
- **페이지**: DashboardPage (요약 카드), ReportPage - 주간 요약 탭
- **컴포넌트**: WeeklySummaryCard, WeekNavigation, WeekSummaryCards, VolumeProgressBar, OverloadAchievementList, WeeklyCalendarDots
- **상태**: TanStack Query (`useWeeklySummary`)
- **인터랙션**:
  - 대시보드 상단에 이번 주 요약 카드 (운동 횟수/총 볼륨/과부하 달성)
  - 리포트 주간 요약 탭에서 상세 정보
  - 주간 네비게이션으로 이전/다음 주 전환
  - 볼륨 달성률 프로그레스 바
  - 운동별 과부하 달성/미달성 표시
  - 요일별 운동 캘린더 도트

## Acceptance Criteria
- 주간 운동 횟수, 총 볼륨, 과부하 달성 수 표시
- 지난 주 대비 볼륨 변화율(%) 표시
- 운동별 과부하 달성 여부 목록 표시
- 주간 네비게이션으로 이전/다음 주 조회 가능
- 대시보드에 이번 주 요약 카드 표시
