# Overload Manager

점진적 과부하(Progressive Overload) 원칙을 기반으로 헬스 트레이닝을 기록하고 분석하는 웹 애플리케이션.

---

## 주요 기능

### 인증
- 이메일/비밀번호 회원가입 및 로그인
- JWT 기반 인증 (Access Token 1시간, Refresh Token 30일)
- 토큰 자동 갱신

### 운동 관리
- 7개 카테고리(가슴/등/하체/어깨/이두/삼두/코어) 36개 기본 운동 제공
- 카테고리 필터 및 한국어/영어 검색
- 운동별 상세 정보 (주동근, 장비, 권장 세트/반복수)

### 운동 세션 기록
- 날짜별 운동 세션 생성 (하루 복수 세션 허용)
- 세션에 운동 복수 추가/제거
- 세트별 무게(kg)/반복수 입력 및 완료 체크
- 세트 자동저장 (500ms debounce)
- 세션 경과 타이머

### 과부하 추적
- 운동 중 직전 세션의 동일 운동 기록 표시
- 운동별 히스토리 (최대 중량, 총 볼륨, 추정 1RM)
- 1RM 추정 계산 (Epley 공식: weight x (1 + reps/30))

### 휴식 타이머
- 세트 완료 시 자동 카운트다운 타이머
- +30초 연장 및 건너뜀 기능

### 리포트
- 운동별 최대 중량 추이 라인 차트
- 주간 요약 (세션 수, 총 볼륨, 전주 대비 변화율)
- 운동별 과부하 달성 현황

---

## 기술 스택

| 영역 | 기술 |
|------|------|
| Backend | Spring Boot 4.0.3 + Kotlin 2.2.21, Java 21 |
| Frontend | React 19 + TypeScript 5.9, Vite 8 |
| Database | H2 (dev), MySQL (prod) |
| ORM | Spring Data JPA + Flyway |
| Auth | JWT (JJWT 0.12.6) |
| State | TanStack Query v5 + Zustand v5 |
| UI | Tailwind CSS 4 + Radix UI |

---

## 시작하기

### 사전 요구사항
- Java 21
- Node.js 20+

### Backend 실행

```bash
# 프로젝트 루트에서
./gradlew bootRun
```

서버가 `http://localhost:8080`에서 시작됩니다.
dev 프로필이 기본 활성화되어 H2 인메모리 DB를 사용하므로 별도 DB 설치가 필요 없습니다.

H2 콘솔: http://localhost:8080/h2-console
- JDBC URL: `jdbc:h2:mem:overloadmanager`
- Username: `sa`
- Password: (빈 값)

### Frontend 실행

```bash
cd frontend
npm install
npm run dev
```

프론트엔드가 `http://localhost:5173`에서 시작됩니다.
Vite 프록시가 `/api` 요청을 `localhost:8080`으로 전달합니다.

---

## 테스트

### Backend 테스트

```bash
# 전체 빌드 + 테스트
./gradlew build

# 테스트만 실행
./gradlew test
```

테스트는 H2 인메모리 DB로 실행되며, Flyway 마이그레이션이 자동 적용됩니다.

### Frontend 빌드 검증

```bash
cd frontend
npm run build
```

### API 수동 테스트

Backend 실행 후 아래 순서로 API를 테스트할 수 있습니다.

**1. 회원가입**
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!",
    "nickname": "테스터"
  }'
```

**2. 로그인**
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "Password123!"
  }'
```
응답에서 `accessToken`을 복사합니다.

**3. 운동 목록 조회**
```bash
# 인증 없이 가능
curl http://localhost:8080/api/v1/exercises

# 카테고리 필터
curl http://localhost:8080/api/v1/exercises?category=CHEST

# 검색
curl http://localhost:8080/api/v1/exercises?search=벤치
```

**4. 운동 세션 생성**
```bash
curl -X POST http://localhost:8080/api/v1/sessions \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {ACCESS_TOKEN}" \
  -d '{
    "sessionDate": "2026-03-20"
  }'
```

**5. 세션에 운동 추가**
```bash
curl -X POST http://localhost:8080/api/v1/sessions/{SESSION_ID}/exercises \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {ACCESS_TOKEN}" \
  -d '{
    "exerciseIds": [1, 5, 10]
  }'
```

**6. 세트 기록**
```bash
curl -X POST http://localhost:8080/api/v1/sessions/{SESSION_ID}/exercises/{SESSION_EXERCISE_ID}/sets \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {ACCESS_TOKEN}" \
  -d '{
    "weight": 80.0,
    "reps": 8,
    "completed": true
  }'
```

**7. 세션 상세 조회**
```bash
curl http://localhost:8080/api/v1/sessions/{SESSION_ID} \
  -H "Authorization: Bearer {ACCESS_TOKEN}"
```

**8. 이전 기록 조회 (과부하 참조)**
```bash
curl "http://localhost:8080/api/v1/exercises/1/previous-session?excludeSessionId={SESSION_ID}" \
  -H "Authorization: Bearer {ACCESS_TOKEN}"
```

**9. 운동 히스토리**
```bash
curl http://localhost:8080/api/v1/exercises/1/history \
  -H "Authorization: Bearer {ACCESS_TOKEN}"
```

**10. 주간 리포트**
```bash
curl http://localhost:8080/api/v1/reports/weekly-summary \
  -H "Authorization: Bearer {ACCESS_TOKEN}"
```

### End-to-End 테스트 (브라우저)

1. Backend (`./gradlew bootRun`) + Frontend (`cd frontend && npm run dev`) 동시 실행
2. `http://localhost:5173` 접속
3. 회원가입 → 로그인
4. "오늘 운동 시작" → 운동 선택 → 세트 기록 → 세션 종료
5. 통계 탭에서 리포트 확인

---

## 프로젝트 구조

```
overload-manager/
├── src/main/kotlin/com/calaf/overloadmanager/
│   ├── auth/          # 인증 (회원가입/로그인/토큰갱신)
│   ├── user/          # 사용자 프로필 관리
│   ├── exercise/      # 운동 목록/검색/히스토리
│   ├── workout/       # 세션/세트 CRUD
│   ├── report/        # 주간 요약 리포트
│   ├── common/        # 에러 처리, 응답 래퍼
│   ├── config/        # Security, JPA 설정
│   └── infrastructure/jwt/  # JWT 발급/검증
├── src/main/resources/
│   ├── application.yml
│   └── db/migration/  # Flyway 마이그레이션 (V1~V7)
├── frontend/          # React 프론트엔드
│   └── src/
│       ├── pages/     # AuthPage, DashboardPage, SetRecordPage, ReportPage 등
│       ├── features/  # auth, exercise, session, report 모듈
│       └── api/       # Backend API 연동
└── docs/              # 기획/설계/기능 추적 문서
```

각 도메인은 Hexagonal Architecture를 따릅니다.
상세 구조는 [CLAUDE.md](./CLAUDE.md)를 참고하세요.

---

## API 문서

전체 API 명세는 [docs/design/api-spec.md](./docs/design/api-spec.md)를 참고하세요.

| 그룹 | 주요 엔드포인트 |
|------|----------------|
| Auth | `POST /api/v1/auth/register`, `POST /api/v1/auth/login`, `POST /api/v1/auth/refresh` |
| User | `GET/PATCH/DELETE /api/v1/users/me` |
| Exercise | `GET /api/v1/exercises`, `GET /api/v1/exercises/{id}/history` |
| Session | `GET/POST /api/v1/sessions`, `PATCH /api/v1/sessions/{id}` |
| Set | `POST/PATCH/DELETE .../sets` |
| Report | `GET /api/v1/reports/weekly-summary` |
