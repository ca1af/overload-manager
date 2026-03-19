# 커스텀 운동 추가 (Custom Exercise)

## Overview
기본 운동 DB에 없는 운동을 사용자가 직접 등록하는 기능. 특수 장비나 변형 동작도 기록할 수 있도록 지원.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P1-6)
- User Flow: docs/planning/user-flows/overload-manager.md (2.2 - US-04)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.3)

## Backend
- **API 엔드포인트**:
  - `POST /api/v1/exercises` — 커스텀 운동 등록
  - `DELETE /api/v1/exercises/{exerciseId}` — 커스텀 운동 삭제 (본인 것만)
- **비즈니스 로직**: created_by = userId, is_custom = true로 저장, 삭제 시 소유권 검증

## Frontend
- **컴포넌트**: 커스텀 운동 생성 폼 (운동 선택 화면 내 진입점)
- **인터랙션**: "+ 커스텀 운동 추가" 버튼으로 폼 진입, 카테고리/장비/주동근 선택

## Acceptance Criteria
- 커스텀 운동 등록 가능 (한국어/영어 이름, 카테고리, 장비 등)
- 등록한 커스텀 운동이 운동 목록에 표시
- 본인 커스텀 운동만 삭제 가능
- 커스텀 운동도 세션에 추가하여 세트 기록 가능
