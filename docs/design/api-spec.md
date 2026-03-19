# API 명세

## 기본 규칙

- **Base URL**: `/api/v1`
- **인증**: Bearer Token (JWT Access Token) — `Authorization: Bearer <token>`
- **Content-Type**: `application/json`
- **날짜 형식**: ISO 8601 (`2026-03-19`, `2026-03-19T10:30:00Z`)
- **무게 단위**: API는 항상 `kg` 기준, 단위 변환은 클라이언트 책임
- **페이지네이션**: `page` (0-based), `size` (default 20), `sort` 쿼리 파라미터

### 공통 에러 응답

```json
{
  "code": "INVALID_INPUT",
  "message": "요청 값이 올바르지 않습니다.",
  "details": [
    { "field": "email", "reason": "이메일 형식이 아닙니다." }
  ]
}
```

| HTTP 상태 | 에러 코드 | 상황 |
|---|---|---|
| 400 | `INVALID_INPUT` | 유효성 검사 실패 |
| 401 | `UNAUTHORIZED` | 미인증 또는 토큰 만료 |
| 403 | `FORBIDDEN` | 타인 리소스 접근 |
| 404 | `NOT_FOUND` | 리소스 없음 |
| 409 | `CONFLICT` | 중복 (이메일 등) |
| 500 | `INTERNAL_ERROR` | 서버 오류 |

---

## 5.1 인증 API

### POST /api/v1/auth/register — 회원가입

**Request**
```json
{
  "email": "user@example.com",
  "password": "Password123!",
  "nickname": "철수",
  "weightUnit": "kg"
}
```

**Response 201**
```json
{
  "id": 1,
  "email": "user@example.com",
  "nickname": "철수",
  "weightUnit": "kg",
  "emailVerified": false,
  "createdAt": "2026-03-19T10:00:00Z"
}
```

### POST /api/v1/auth/login — 로그인

**Request**
```json
{
  "email": "user@example.com",
  "password": "Password123!"
}
```

**Response 200**
```json
{
  "accessToken": "eyJ...",
  "tokenType": "Bearer",
  "expiresIn": 3600,
  "user": {
    "id": 1,
    "email": "user@example.com",
    "nickname": "철수",
    "weightUnit": "kg"
  }
}
```

> Refresh Token은 `Set-Cookie: HttpOnly` 헤더로 전달 (보안 강화)

### POST /api/v1/auth/refresh — 토큰 갱신

Refresh Token은 HttpOnly 쿠키로 자동 전송.

**Response 200**
```json
{
  "accessToken": "eyJ...",
  "expiresIn": 3600
}
```

### POST /api/v1/auth/logout — 로그아웃

**Headers**: `Authorization: Bearer <accessToken>`

**Response 204** (No Content)

### POST /api/v1/auth/verify-email — 이메일 인증

**Request**
```json
{
  "token": "verification-uuid-token"
}
```

**Response 200**
```json
{
  "message": "이메일 인증이 완료되었습니다."
}
```

---

## 5.2 사용자 API

### GET /api/v1/users/me — 내 정보 조회

**Response 200**
```json
{
  "id": 1,
  "email": "user@example.com",
  "nickname": "철수",
  "weightUnit": "kg",
  "weeklyGoalSessions": 3,
  "emailVerified": true,
  "createdAt": "2026-03-19T10:00:00Z"
}
```

### PATCH /api/v1/users/me — 내 정보 수정

**Request**
```json
{
  "nickname": "철수2",
  "weightUnit": "lb",
  "weeklyGoalSessions": 4
}
```

### DELETE /api/v1/users/me — 회원 탈퇴

**Response 204** (소프트 삭제)

---

## 5.3 운동(Exercise) API

### GET /api/v1/exercises — 운동 목록 조회

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `category` | string | `CHEST\|BACK\|LEGS\|SHOULDERS\|BICEPS\|TRICEPS\|CORE` |
| `query` | string | 운동명 검색 (한국어/영어) |
| `includeCustom` | boolean | 사용자 커스텀 운동 포함 여부 (default: true) |
| `page` | int | 페이지 번호 (default: 0) |
| `size` | int | 페이지 크기 (default: 50) |

**Response 200**
```json
{
  "content": [
    {
      "id": 1,
      "nameKo": "벤치 프레스",
      "nameEn": "Bench Press",
      "category": "CHEST",
      "exerciseType": "COMPOUND",
      "equipment": "BARBELL",
      "primaryMuscle": "대흉근",
      "secondaryMuscles": ["삼두근", "전면 삼각근"],
      "defaultSetsMin": 3,
      "defaultSetsMax": 5,
      "defaultRepsMin": 5,
      "defaultRepsMax": 12,
      "isCustom": false
    }
  ],
  "totalElements": 48,
  "totalPages": 1,
  "page": 0,
  "size": 50
}
```

### GET /api/v1/exercises/{exerciseId} — 운동 상세 조회

### POST /api/v1/exercises — 커스텀 운동 등록 (P1)

**Request**
```json
{
  "nameKo": "케이블 체스트 프레스",
  "nameEn": "Cable Chest Press",
  "category": "CHEST",
  "exerciseType": "COMPOUND",
  "equipment": "CABLE",
  "primaryMuscle": "대흉근",
  "secondaryMuscles": ["삼두근"],
  "defaultSetsMin": 3,
  "defaultSetsMax": 4,
  "defaultRepsMin": 10,
  "defaultRepsMax": 15
}
```

### DELETE /api/v1/exercises/{exerciseId} — 커스텀 운동 삭제

본인 커스텀 운동만 삭제 가능, 타인 것은 403.

---

## 5.4 운동 세션 API

### GET /api/v1/sessions — 세션 목록 조회

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `from` | date | 시작 날짜 (inclusive) |
| `to` | date | 종료 날짜 (inclusive) |
| `page` | int | 페이지 번호 |
| `size` | int | 페이지 크기 |

**Response 200**
```json
{
  "content": [
    {
      "id": 10,
      "sessionDate": "2026-03-19",
      "notes": "오늘 컨디션 좋음",
      "startedAt": "2026-03-19T09:00:00Z",
      "finishedAt": "2026-03-19T10:12:00Z",
      "durationSeconds": 4320,
      "exerciseCount": 4,
      "totalSets": 12,
      "totalVolumeKg": 5240.0
    }
  ],
  "totalElements": 45,
  "totalPages": 3,
  "page": 0,
  "size": 20
}
```

### POST /api/v1/sessions — 세션 생성

**Request**
```json
{
  "sessionDate": "2026-03-19",
  "notes": "오늘 컨디션 좋음"
}
```

**Response 201**
```json
{
  "id": 10,
  "sessionDate": "2026-03-19",
  "notes": "오늘 컨디션 좋음",
  "startedAt": "2026-03-19T09:00:00Z",
  "finishedAt": null,
  "durationSeconds": null,
  "exercises": []
}
```

### GET /api/v1/sessions/{sessionId} — 세션 상세 조회

**Response 200**
```json
{
  "id": 10,
  "sessionDate": "2026-03-19",
  "notes": "오늘 컨디션 좋음",
  "startedAt": "2026-03-19T09:00:00Z",
  "finishedAt": null,
  "exercises": [
    {
      "id": 100,
      "orderIndex": 0,
      "exercise": {
        "id": 1,
        "nameKo": "벤치 프레스",
        "category": "CHEST"
      },
      "sets": [
        {
          "id": 200,
          "setNumber": 1,
          "weightKg": 80.0,
          "reps": 8,
          "completed": true,
          "restSeconds": 120,
          "completedAt": "2026-03-19T09:10:00Z"
        }
      ]
    }
  ]
}
```

### PATCH /api/v1/sessions/{sessionId} — 세션 수정 (메모, 종료)

**Request**
```json
{
  "notes": "수정된 메모",
  "finished": true
}
```

`finished: true`이면 서버에서 `finishedAt` 및 `durationSeconds` 계산.

### DELETE /api/v1/sessions/{sessionId} — 세션 삭제

### POST /api/v1/sessions/{sessionId}/exercises — 세션에 운동 추가

**Request**
```json
{
  "exerciseIds": [1, 3, 5]
}
```

### DELETE /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId} — 세션에서 운동 제거

---

## 5.5 세트 기록 API

### POST /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets — 세트 추가

`setNumber`는 클라이언트가 생략 시 서버가 자동 채번.

**Request**
```json
{
  "weightKg": 80.0,
  "reps": 8
}
```

**Response 201**
```json
{
  "id": 200,
  "setNumber": 1,
  "weightKg": 80.0,
  "reps": 8,
  "completed": false,
  "restSeconds": null,
  "completedAt": null,
  "createdAt": "2026-03-19T09:05:00Z"
}
```

### PATCH /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setId} — 세트 수정/완료

**Request**
```json
{
  "weightKg": 82.5,
  "reps": 8,
  "completed": true,
  "restSeconds": 120
}
```

`completed: true`이면 서버에서 `completedAt` 기록.

### DELETE /api/v1/sessions/{sessionId}/exercises/{sessionExerciseId}/sets/{setId} — 세트 삭제

---

## 5.6 이전 기록 조회 API (핵심)

### GET /api/v1/exercises/{exerciseId}/previous-session — 직전 세션 기록

운동 중 참고용. 현재 활성 세션 제외한 가장 최근 완료 세션의 해당 운동 세트 반환.

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `excludeSessionId` | long | 현재 세션 ID (제외) |

**Response 200 (기록 있음)**
```json
{
  "sessionId": 9,
  "sessionDate": "2026-03-15",
  "sets": [
    { "setNumber": 1, "weightKg": 80.0, "reps": 8, "completed": true },
    { "setNumber": 2, "weightKg": 80.0, "reps": 8, "completed": true },
    { "setNumber": 3, "weightKg": 80.0, "reps": 7, "completed": true }
  ],
  "totalVolumeKg": 1840.0
}
```

**Response 200 (이전 기록 없음)**
```json
{
  "sessionId": null,
  "sessionDate": null,
  "sets": [],
  "totalVolumeKg": 0.0
}
```

> 이전 기록 없음은 404가 아닌 200 빈 응답. `sets.length === 0`으로 판별.

---

## 5.7 리포트 API

### GET /api/v1/exercises/{exerciseId}/history — 운동별 히스토리 (P0)

**Response 200**
```json
{
  "content": [
    {
      "sessionId": 10,
      "sessionDate": "2026-03-19",
      "sets": [
        { "setNumber": 1, "weightKg": 82.5, "reps": 8, "completed": true }
      ],
      "maxWeightKg": 82.5,
      "totalVolumeKg": 1980.0,
      "estimatedOneRepMax": 106.5
    }
  ],
  "totalElements": 30,
  "totalPages": 2,
  "page": 0,
  "size": 20
}
```

### GET /api/v1/exercises/{exerciseId}/volume-trend — 볼륨 트렌드 (P1)

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `period` | string | `WEEKLY` \| `MONTHLY` |
| `from` | date | 시작 날짜 |
| `to` | date | 종료 날짜 |

**Response 200**
```json
{
  "exerciseId": 1,
  "exerciseName": "벤치 프레스",
  "period": "WEEKLY",
  "data": [
    { "periodLabel": "2026-W10", "startDate": "2026-03-02", "totalVolumeKg": 4800.0, "maxWeightKg": 80.0 },
    { "periodLabel": "2026-W11", "startDate": "2026-03-09", "totalVolumeKg": 5120.0, "maxWeightKg": 82.5 }
  ]
}
```

### GET /api/v1/reports/weekly-summary — 주간 요약 리포트

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `date` | date | 해당 주의 임의 날짜 (default: 오늘) |

**Response 200**
```json
{
  "weekStart": "2026-03-16",
  "weekEnd": "2026-03-22",
  "sessionCount": 3,
  "weeklyGoalSessions": 4,
  "totalSets": 36,
  "totalVolumeKg": 18400.0,
  "previousWeekVolumeKg": 17000.0,
  "volumeChangePercent": 8.2,
  "overloadAchieved": [
    { "exerciseId": 1, "exerciseName": "벤치 프레스", "achieved": true },
    { "exerciseId": 2, "exerciseName": "데드리프트", "achieved": false }
  ]
}
```

### GET /api/v1/exercises/{exerciseId}/estimated-1rm — 추정 1RM 조회 (P1)

Epley 공식: `1RM = weight x (1 + reps / 30)`

**Response 200**
```json
{
  "exerciseId": 1,
  "estimatedOneRepMax": 106.5,
  "basedOnSet": {
    "weightKg": 82.5,
    "reps": 8,
    "sessionDate": "2026-03-19"
  }
}
```

---

## 5.8 루틴 API (P1)

### GET /api/v1/routines — 루틴 목록

### POST /api/v1/routines — 루틴 생성

**Request**
```json
{
  "name": "상체 A",
  "description": "가슴+등 복합",
  "exercises": [
    { "exerciseId": 1, "orderIndex": 0, "targetSets": 4, "targetRepsMin": 6, "targetRepsMax": 8 },
    { "exerciseId": 3, "orderIndex": 1, "targetSets": 3, "targetRepsMin": 8, "targetRepsMax": 12 }
  ]
}
```

### POST /api/v1/routines/{routineId}/apply — 루틴을 세션에 적용

**Request**
```json
{
  "sessionId": 10
}
```

### DELETE /api/v1/routines/{routineId} — 루틴 삭제 (소프트 삭제)

---

## JWT 설계

| 항목 | 값 |
|---|---|
| Access Token 유효 기간 | 1시간 |
| Refresh Token 유효 기간 | 30일 |
| Access Token Payload | `sub` (userId), `email`, `iat`, `exp` |
| 저장 위치 | Access: 메모리(클라이언트 Zustand), Refresh: DB + HttpOnly Cookie |
| 갱신 전략 | Refresh Token Rotation — 갱신 시 기존 토큰 폐기 후 신규 발급 |
| CORS 설정 | `allowCredentials: true`, 프론트엔드 도메인 명시적 지정 |

## 보안 고려사항

1. **비밀번호**: BCrypt (strength 10) 해싱 저장
2. **SQL Injection**: JPA/Prepared Statement 사용으로 방지
3. **CORS**: `allowedOrigins`에 프론트엔드 도메인 명시적 지정, `allowCredentials: true`
4. **Rate Limiting**: 로그인 엔드포인트에 Bucket4j로 제한 (MVP 이후)
5. **데이터 격리**: 모든 Service에서 `userId` 기준 소유권 검증
6. **HTTPS**: 운영 환경 필수
