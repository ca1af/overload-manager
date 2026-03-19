# 이전 세션 기록 조회 (Previous Session)

## Overview
운동 중 직전 세션의 같은 운동 기록(무게·반복수)을 표시하여 오늘의 목표 중량 결정을 돕는 기능. 과부하 실현의 핵심 UX.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P0-5)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 4)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.4, 3.5)

## Backend
- **API 엔드포인트**:
  - `GET /api/v1/exercises/{exerciseId}/previous-session` — 직전 세션 기록 조회
    - 파라미터: `excludeSessionId` (현재 세션 제외)
    - 이전 기록 없음 시 404가 아닌 200 빈 응답 (`sets: []`)
- **비즈니스 로직**: 현재 활성 세션 제외, 가장 최근 완료 세션의 해당 운동 세트 반환, 총 볼륨 계산

## Frontend
- **컴포넌트**: PreviousSessionSummaryBanner (항상 표시), OverloadInfoSheet (바텀시트)
- **바텀시트 내용**: 직전 세션 세트별 기록, 추정 1RM, 오늘 목표 제안(중량 증가/반복수 증가), 8주 최대 중량 미니 차트
- **상태**: TanStack Query (`usePreviousSession`)
- **인터랙션**:
  - 세트 기록 화면 상단에 직전 기록 요약 항상 표시
  - "이전 기록" 버튼 탭으로 바텀시트 열기
  - 목표 제안 카드 탭 시 입력 필드 자동 채움
  - 바텀시트: 드래그 핸들, 스냅 포인트, 80% 높이 제한 (vaul)

## Acceptance Criteria
- 세트 기록 화면에서 직전 세션의 동일 운동 기록 확인 가능
- 이전 기록이 없는 경우 빈 상태 UI 표시
- 바텀시트에서 세트별 상세 기록 및 총 볼륨 확인 가능
- 목표 제안(중량 증가/반복수 증가) 표시
