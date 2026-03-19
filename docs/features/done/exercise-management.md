# 운동 관리 (Exercise Management)

## Overview
운동 카테고리별 검색·선택 기능. 기본 운동 DB(가슴/등/하체/어깨/팔/코어) 제공 및 운동 상세 정보 조회.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P0-2)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 2, 2.3)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.3)

## Backend
- **엔티티**: Exercise
- **유스케이스**: 운동 목록 조회, 운동 상세 조회
- **API 엔드포인트**:
  - `GET /api/v1/exercises` — 운동 목록 조회 (카테고리, 검색어 필터)
  - `GET /api/v1/exercises/{exerciseId}` — 운동 상세 조회
- **기본 운동 DB**: Flyway 마이그레이션으로 초기 데이터 삽입 (V4__insert_default_exercises.sql)
- **운동 속성**: nameKo, nameEn, category, exerciseType, equipment, primaryMuscle, secondaryMuscles, defaultSets, defaultReps

## Frontend
- **페이지**: ExerciseSelectPage (`/sessions/:id/exercises/add`)
- **컴포넌트**: ExerciseSearchInput, CategoryFilterTabs, ExerciseItem, ExerciseDetailSheet, SelectionSummaryBar
- **상태**: TanStack Query (`useExercises`), 로컬 useState (검색어, 카테고리), Zustand (selectedExerciseIds)
- **인터랙션**: 카테고리 탭 필터, 실시간 검색, 체크박스 복수 선택, long press 상세 정보, 하단 요약 바

## Acceptance Criteria
- 7개 카테고리(전체/가슴/등/하체/어깨/팔/코어)로 운동 필터링 가능
- 한국어/영어 운동명 검색 지원
- 복수 운동 선택 후 세션에 추가 가능
- 운동 상세 정보(주동근, 운동 유형, 장비, 권장 세트/반복수) 확인 가능
