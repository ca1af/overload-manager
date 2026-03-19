# ERD (Entity-Relationship Diagram)

## Mermaid ERD

```mermaid
erDiagram
    USER {
        bigint id PK
        varchar(255) email UK "NOT NULL"
        varchar(255) password_hash "NOT NULL"
        varchar(100) nickname "NOT NULL"
        varchar(10) weight_unit "kg | lb, DEFAULT kg"
        int weekly_goal_sessions "DEFAULT 3"
        boolean email_verified "DEFAULT false"
        timestamp created_at "NOT NULL"
        timestamp updated_at "NOT NULL"
        timestamp deleted_at "nullable, soft delete"
    }

    REFRESH_TOKEN {
        bigint id PK
        bigint user_id FK "NOT NULL"
        varchar(512) token "NOT NULL"
        timestamp expires_at "NOT NULL"
        timestamp created_at "NOT NULL"
    }

    EXERCISE {
        bigint id PK
        bigint created_by FK "nullable, null=기본DB"
        varchar(200) name_ko "NOT NULL"
        varchar(200) name_en "NOT NULL"
        varchar(50) category "CHEST|BACK|LEGS|SHOULDERS|BICEPS|TRICEPS|CORE"
        varchar(50) exercise_type "COMPOUND|ISOLATION"
        varchar(50) equipment "BARBELL|DUMBBELL|MACHINE|CABLE|BODYWEIGHT"
        varchar(100) primary_muscle "NOT NULL"
        json secondary_muscles "JSON 배열"
        int default_sets_min "nullable"
        int default_sets_max "nullable"
        int default_reps_min "nullable"
        int default_reps_max "nullable"
        boolean is_custom "DEFAULT false"
        timestamp created_at "NOT NULL"
    }

    WORKOUT_SESSION {
        bigint id PK
        bigint user_id FK "NOT NULL"
        date session_date "NOT NULL"
        varchar(500) notes "nullable"
        timestamp started_at "NOT NULL"
        timestamp finished_at "nullable"
        int duration_seconds "nullable, 완료 시 계산"
        timestamp created_at "NOT NULL"
        timestamp updated_at "NOT NULL"
    }

    SESSION_EXERCISE {
        bigint id PK
        bigint session_id FK "NOT NULL"
        bigint exercise_id FK "NOT NULL"
        int order_index "NOT NULL, 세션 내 운동 순서"
        timestamp created_at "NOT NULL"
    }

    WORKOUT_SET {
        bigint id PK
        bigint session_exercise_id FK "NOT NULL"
        int set_number "NOT NULL"
        decimal(6_2) weight "NOT NULL, 0 이상, 항상 kg"
        int reps "NOT NULL, 1 이상"
        boolean completed "DEFAULT false"
        int rest_seconds "nullable, 완료 후 휴식 시간"
        timestamp completed_at "nullable"
        timestamp created_at "NOT NULL"
        timestamp updated_at "NOT NULL"
    }

    ROUTINE {
        bigint id PK
        bigint user_id FK "NOT NULL"
        varchar(200) name "NOT NULL"
        varchar(500) description "nullable"
        timestamp created_at "NOT NULL"
        timestamp updated_at "NOT NULL"
        timestamp deleted_at "nullable"
    }

    ROUTINE_EXERCISE {
        bigint id PK
        bigint routine_id FK "NOT NULL"
        bigint exercise_id FK "NOT NULL"
        int order_index "NOT NULL"
        int target_sets "nullable"
        int target_reps_min "nullable"
        int target_reps_max "nullable"
        timestamp created_at "NOT NULL"
    }

    BODY_WEIGHT_LOG {
        bigint id PK
        bigint user_id FK "NOT NULL"
        decimal(5_2) weight "NOT NULL"
        varchar(10) unit "kg | lb"
        date logged_date "NOT NULL"
        timestamp created_at "NOT NULL"
    }

    USER ||--o{ REFRESH_TOKEN : "has"
    USER ||--o{ EXERCISE : "creates (custom)"
    USER ||--o{ WORKOUT_SESSION : "logs"
    USER ||--o{ ROUTINE : "owns"
    USER ||--o{ BODY_WEIGHT_LOG : "tracks"
    WORKOUT_SESSION ||--o{ SESSION_EXERCISE : "contains"
    SESSION_EXERCISE }o--|| EXERCISE : "references"
    SESSION_EXERCISE ||--o{ WORKOUT_SET : "has"
    ROUTINE ||--o{ ROUTINE_EXERCISE : "contains"
    ROUTINE_EXERCISE }o--|| EXERCISE : "references"
```

## 엔티티 상세 설명

| 엔티티 | 설명 |
|---|---|
| **USER** | 회원 계정 정보. 소프트 삭제 적용. `weight_unit`은 사용자 선호 단위, DB 저장은 항상 kg. `weekly_goal_sessions`는 대시보드 주간 목표 표시용. |
| **REFRESH_TOKEN** | JWT Refresh Token 별도 저장. 로그아웃/탈취 의심 시 레코드 삭제로 토큰 폐기. |
| **EXERCISE** | 기본 운동 DB(`created_by = NULL`)와 사용자 커스텀 운동을 단일 테이블 관리. |
| **WORKOUT_SESSION** | 하루 복수 세션 허용. `started_at`은 생성 시 서버 시간, `finished_at`은 종료 시 기록. |
| **SESSION_EXERCISE** | 세션에 추가된 운동 목록. `order_index`로 순서 관리. |
| **WORKOUT_SET** | 핵심 기록 단위. `weight`는 항상 kg. `completed = false`는 계획된 세트, `true`는 완료 세트. |
| **ROUTINE / ROUTINE_EXERCISE** | P1 기능. 자주 쓰는 운동 조합 저장. |
| **BODY_WEIGHT_LOG** | P1 기능. 날짜별 체중 기록. |

## 인덱스 전략

| 테이블 | 인덱스 | 이유 |
|---|---|---|
| USER | `UNIQUE (email)` | 로그인/중복 검사 |
| USER | `INDEX (deleted_at)` | 소프트 삭제 필터링 |
| REFRESH_TOKEN | `INDEX (user_id)` | 사용자별 토큰 조회 |
| REFRESH_TOKEN | `INDEX (expires_at)` | 만료 토큰 배치 삭제 |
| EXERCISE | `INDEX (category, is_custom)` | 카테고리별 목록 |
| EXERCISE | `INDEX (created_by)` | 사용자 커스텀 운동 목록 |
| EXERCISE | `FULLTEXT (name_ko, name_en)` | 운동명 검색 (MySQL) |
| WORKOUT_SESSION | `INDEX (user_id, session_date DESC)` | 날짜순 세션 목록 |
| SESSION_EXERCISE | `INDEX (session_id, order_index)` | 세션 내 운동 순서 조회 |
| SESSION_EXERCISE | `INDEX (exercise_id)` | 특정 운동 히스토리 조회 |
| WORKOUT_SET | `INDEX (session_exercise_id, set_number)` | 세트 목록 조회 |
| BODY_WEIGHT_LOG | `INDEX (user_id, logged_date DESC)` | 날짜순 체중 목록 |
| ROUTINE | `INDEX (user_id, deleted_at)` | 사용자 루틴 목록 |
