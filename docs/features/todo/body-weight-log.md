# 몸무게 로그 (Body Weight Log)

## Overview
날짜별 체중 기록 및 그래프. 체중 변화 추이를 트레이닝 볼륨/과부하 데이터와 함께 추적.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P1-7)

## Backend
- **엔티티**: BodyWeightLog
- **API 엔드포인트**:
  - `GET /api/v1/body-weight` — 체중 기록 목록 조회 (날짜 범위)
  - `POST /api/v1/body-weight` — 체중 기록 추가
  - `DELETE /api/v1/body-weight/{id}` — 체중 기록 삭제
- **비즈니스 로직**: 날짜별 체중 기록, 단위(kg/lb) 보관

## Frontend
- **컴포넌트**: 체중 입력 폼, 체중 추이 그래프 (라인차트)
- **인터랙션**: 날짜별 체중 입력, 기간별 추이 그래프 조회

## Acceptance Criteria
- 날짜별 체중 기록 가능
- 체중 변화 추이 그래프 표시
- 체중 기록 삭제 가능
