# Overload Manager E2E 테스트 리포트

> 테스트 일시: 2026-03-20
> 테스트 환경: Chrome 브라우저, localhost:5173 (Vite dev) + localhost:8080 (Spring Boot H2)
> 테스트 범위: PRD P0/P1 MVP 전체 워크플로우 (Flow 1~6)

---

## 1. 테스트 결과 요약

| 워크플로우 | 결과 | 비고 |
|---|---|---|
| Flow 1: 회원가입 | **부분 통과** | 기본 동작 OK, 이메일 인증 미구현 |
| Flow 1: 로그인 | **통과** | 정상 로그인 → 대시보드 이동 |
| Flow 2: 대시보드 | **실패** | undefinedkg, 운동횟수 "/" 표시, 빈 상태 UI 없음 |
| Flow 2: 운동 세션 생성 | **실패** | POST /api/v1/sessions → 500 에러 |
| Flow 3: 세트 기록 | **테스트 불가** | 세션 생성 실패로 진입 불가 |
| Flow 5: 세션 종료/요약 | **테스트 불가** | 세션 생성 실패로 진입 불가 |
| Flow 6: 리포트/통계 | **실패** | 운동 드롭다운 바인딩 실패, 주간요약 크래시 |

---

## 2. 발견된 버그 목록

### Critical (서비스 불가 수준)

#### BUG-01: 대시보드 총 볼륨 `undefinedkg` 표시
- **화면**: 대시보드 > 주간 요약 카드 > 총 볼륨
- **재현 방법**: 로그인 후 대시보드 진입
- **기대 동작**: 데이터 없을 시 `0kg` 표시
- **실제 동작**: `undefinedkg` 문자열 그대로 표시
- **근본 원인 (코드 확인됨)**: 백엔드-프론트엔드 필드명 불일치
  - 백엔드 응답: `totalVolume` (`ReportDtos.kt:7-14`)
  - 프론트엔드 기대: `totalVolumeKg` (`types/domain.ts:84`)
  - `DashboardPage.tsx:78`에서 `weeklySummary.totalVolumeKg` 참조 → undefined
  - `formatWeight(undefined, 'kg')` → `"undefinedkg"` 출력
- **우선순위**: Critical
- **스크린샷**: TC01-signup-flow.gif 참조

#### BUG-09: 세션 생성 API 500 에러
- **화면**: 대시보드 > "오늘 운동 시작" 버튼
- **재현 방법**: 로그인 후 "오늘 운동 시작" 클릭
- **기대 동작**: 새 운동 세션이 생성되고 운동 추가 화면으로 이동
- **실제 동작**: `POST /api/v1/sessions` → 500 Internal Server Error, UI에 에러 표시 없음
- **영향**: 핵심 워크플로우(세트 기록, 세션 종료) 전체 차단
- **근본 원인 (코드 확인됨)**:
  - `WorkoutSessionPersistenceAdapter.kt:41`에서 `userJpaRepository.findById(session.userId).orElseThrow()` 실행 — 사용자 조회 실패 시 NoSuchElementException
  - `JwtAuthenticationFilter.kt:24`에서 `auth.principal as Long` 캐스팅 — principal 타입이 Long이 아닌 경우 ClassCastException 가능
  - 예외가 적절히 처리되지 않아 500 에러로 전파
  - 컨트롤러: `WorkoutSessionController.kt:46-54`
  - 서비스: `WorkoutSessionService.kt:51-64`
- **우선순위**: Critical — MVP 핵심 기능 완전 차단

#### BUG-12: 통계 페이지 운동 선택 드롭다운 데이터 미로딩
- **화면**: 통계 > 운동별 탭 > 운동 선택 드롭다운
- **재현 방법**: 통계 페이지 진입 후 드롭다운 클릭
- **기대 동작**: API에서 받은 운동 목록이 옵션으로 표시
- **실제 동작**: "운동을 선택하세요" 기본 옵션만 존재 (API 200 OK 응답에도 불구하고)
- **근본 원인 (코드 확인됨)**: API 파라미터명 불일치
  - 프론트엔드 요청: `keyword` 파라미터 사용 (`ExerciseList.tsx:38-43`)
  - 백엔드 기대: `search` 파라미터 (`ExerciseController.kt:26`)
  - 검색 필터가 작동하지 않아 데이터가 비정상적으로 로딩됨
  - 추가로 응답 필드 타입 불일치 가능: `defaultSetsMin/Max`
- **우선순위**: Critical

#### BUG-13: 주간 요약 탭 클릭 시 앱 크래시
- **화면**: 통계 > "주간 요약" 탭
- **재현 방법**: 통계 페이지에서 "주간 요약" 탭 클릭
- **기대 동작**: 주간 볼륨, 운동 횟수 등 요약 정보 표시
- **실제 동작**: `TypeError: Cannot read properties of undefined (reading 'toFixed')` 에러로 앱 크래시
- **에러 위치**: `WeeklySummaryView.tsx:96` (실제 라인)
- **근본 원인 (코드 확인됨)**: 백엔드 응답에 필드 누락
  - 백엔드 `WeeklySummaryResponse` (`ReportDtos.kt:7-14`)에 `volumeChangePercent` 필드 없음
  - 프론트엔드 `WeeklySummary` 타입 (`types/domain.ts:86`)에는 `volumeChangePercent: number` 정의됨
  - `WeeklySummaryView.tsx:96`에서 `summary.volumeChangePercent.toFixed(1)` 호출 → undefined.toFixed() → TypeError
- **우선순위**: Critical

### High (주요 기능 결함)

#### BUG-02: 대시보드 운동 횟수 `/` 표시
- **화면**: 대시보드 > 주간 요약 카드 > 운동 횟수
- **재현 방법**: 로그인 후 대시보드 진입
- **기대 동작**: `0/3회` 형식 (현재 횟수/주간 목표)
- **실제 동작**: `/` 만 표시 (양쪽 숫자 누락)
- **근본 원인 (코드 확인됨)**: 백엔드 응답에 필드 누락
  - 백엔드 `WeeklySummaryResponse` (`ReportDtos.kt:7-14`)에 `weeklyGoalSessions` 필드 없음
  - 프론트엔드 `DashboardPage.tsx:69`에서 `{weeklySummary.sessionCount}/{weeklySummary.weeklyGoalSessions}` → `undefined/undefined` → `/`
- **우선순위**: High

#### BUG-04: 로그아웃 기능 없음
- **화면**: 전체 앱
- **재현 방법**: 로그인 상태에서 로그아웃 시도
- **기대 동작**: PRD 와이어프레임에 상단 헤더 프로필 아이콘 → 드롭다운 메뉴 → 로그아웃
- **실제 동작**: 로그아웃 UI가 아예 없음. localStorage 직접 삭제 외 방법 없음
- **우선순위**: High

#### BUG-10: API 에러 시 사용자 피드백 없음
- **화면**: 전체 앱 (특히 세션 생성 시)
- **재현 방법**: 500 에러를 반환하는 API 호출 시
- **기대 동작**: 토스트 알림이나 에러 메시지로 사용자에게 안내
- **실제 동작**: 아무런 시각적 피드백 없이 무시됨
- **우선순위**: High

#### BUG-11: 하단 네비게이션 "운동", "기록" 탭 미작동
- **화면**: 하단 탭 바
- **재현 방법**: 하단 "운동" 또는 "기록" 탭 클릭
- **기대 동작**: 각각의 페이지로 라우팅
- **실제 동작**: 클릭해도 페이지 변경 없음 (홈, 통계만 정상)
- **근본 원인 (코드 확인됨)**:
  - "운동" 탭: `BottomTabBar.tsx:29-30`에서 `navigate('/')` 하드코딩 — `/sessions/new` 대신 대시보드로 리다이렉트
  - "기록" 탭: `BottomTabBar.tsx:8`에서 `/history` 경로 정의했으나 `router/index.tsx`에 해당 라우트 미등록 → catch-all `*`이 `/`로 리다이렉트
  - `router/index.tsx`에 `/exercises`, `/history` 라우트 및 페이지 컴포넌트 미구현
- **우선순위**: High

### Medium (기능 불완전)

#### BUG-03: 회원가입 완료 시 이메일 인증 안내 모달 미표시
- **화면**: 회원가입 폼
- **재현 방법**: 회원가입 정보 입력 후 제출
- **기대 동작**: PRD Flow 1에 따라 이메일 인증 안내 모달 표시 후 로그인 탭으로 전환
- **실제 동작**: 바로 로그인 처리 후 대시보드로 이동 (이메일 인증 플로우 전체 건너뜀)
- **참고**: MVP에서 이메일 인증을 제외하는 것이 의도적이었을 수 있으나, PRD에는 P0-1으로 명시됨
- **우선순위**: Medium

#### BUG-05: 대시보드 빈 상태(Empty State) UI 없음
- **화면**: 대시보드 > 최근 세션 영역
- **재현 방법**: 신규 사용자로 대시보드 진입
- **기대 동작**: "아직 운동 기록이 없습니다. 첫 운동을 시작해보세요!" 등 안내 메시지
- **실제 동작**: 버튼 아래 완전히 빈 화면
- **우선순위**: Medium

#### BUG-06: 개인 기록(PR) 섹션 미구현
- **화면**: 대시보드
- **기대 동작**: PRD 대시보드 와이어프레임에 PersonalRecordList/PRBadge 컴포넌트 존재
- **실제 동작**: 해당 섹션이 대시보드에 아예 없음
- **우선순위**: Medium

### Low (미세 개선)

#### BUG-07: 상단 헤더 알림 벨 아이콘 없음
- **화면**: 전체 앱 상단 헤더
- **기대 동작**: PRD 와이어프레임에 알림 벨 아이콘 명시
- **실제 동작**: 헤더에 "Overload" 텍스트만 표시
- **우선순위**: Low

#### BUG-08: 주간 요약 카드 섹션 헤더 누락
- **화면**: 대시보드 > 주간 요약 카드
- **기대 동작**: "이번 주 요약" 섹션 헤더 표시
- **실제 동작**: 섹션 헤더 없이 바로 카드 표시
- **우선순위**: Low

---

## 3. 개선사항 제안

### UX 개선

| ID | 제안 | 설명 | 우선순위 |
|---|---|---|---|
| IMP-01 | 비밀번호 확인 빈 필드 유효성 검사 | 빈 폼 제출 시 비밀번호 확인 필드에도 에러 메시지 표시 필요 | Low |
| IMP-02 | 로그인 실패 시 에러 메시지 | 잘못된 비밀번호 입력 시 인라인 에러 표시 + PRD의 쉐이크 애니메이션 | High |
| IMP-03 | 대시보드 Empty State UX | 첫 사용자를 위한 온보딩 가이드 또는 운동 시작 유도 메시지 | Medium |
| IMP-04 | ErrorBoundary 적용 | React Router의 errorElement 활용하여 앱 크래시 대신 친절한 에러 페이지 표시 | High |
| IMP-05 | 비밀번호 보기 토글 | PRD 와이어프레임에 명시된 비밀번호 보기 토글 아이콘 (로그인 폼) | Low |

---

## 4. 개발팀 작업 지시사항

### 즉시 수정 필요 (Blocker — MVP 출시 차단)

#### 1. POST /api/v1/sessions 500 에러 수정 (BUG-09)
- **파일**:
  - `src/.../infrastructure/jwt/JwtAuthenticationFilter.kt:24` — `auth.principal as Long` 캐스팅 검증
  - `src/.../workout/adapter/out/persistence/WorkoutSessionPersistenceAdapter.kt:41` — `userJpaRepository.findById()` 예외 처리
  - `src/.../workout/adapter/in/web/WorkoutSessionController.kt:46-54` — createSession
  - `src/.../workout/application/service/WorkoutSessionService.kt:51-64` — createSession
- **작업**:
  1. JWT에서 추출한 userId가 올바른 Long 타입으로 전달되는지 확인
  2. `findById()` 실패 시 의미 있는 예외(404 또는 400)로 변환
  3. GlobalExceptionHandler에서 적절한 에러 응답 반환
- **검증**: 로그인 후 "오늘 운동 시작" 클릭 → 세션 생성 성공 → 운동 추가 화면 진입 확인

#### 2. 백엔드-프론트엔드 API 계약 통일 (BUG-01, BUG-02, BUG-13) — 근본 수정
- **핵심 원인**: 백엔드 `WeeklySummaryResponse`와 프론트엔드 `WeeklySummary` 타입 간 필드명/존재 불일치
- **백엔드 수정** (`src/.../report/adapter/in/web/ReportDtos.kt:7-14`):
  1. `totalVolume` → `totalVolumeKg`로 필드명 변경 (또는 프론트엔드 타입을 맞춤)
  2. `weeklyGoalSessions` 필드 추가 (User 엔티티의 weekly_goal_sessions 값 조회)
  3. `volumeChangePercent` 필드 추가 (전주 대비 볼륨 변화율 계산)
  4. `sessionCount` 필드명 확인 및 통일
- **프론트엔드 수정**:
  - `frontend/src/types/domain.ts:78-88` — 백엔드 응답과 일치하도록 타입 조정
  - `frontend/src/pages/DashboardPage.tsx:69,78` — fallback 값 추가 (`?? 0`)
  - `frontend/src/features/report/WeeklySummaryView.tsx:96` — `?.toFixed(1) ?? '0.0'`
- **검증**: 데이터 없는 신규 사용자 대시보드에서 `0kg`, `0/3회` 등 정상 표시, 주간 요약 탭 크래시 없이 표시

#### 3. 통계 페이지 운동 드롭다운 수정 (BUG-12)
- **파일**:
  - `frontend/src/features/exercise/ExerciseList.tsx:38-43` — API 호출 파라미터
  - `src/.../exercise/adapter/in/web/ExerciseController.kt:26` — 백엔드 파라미터 정의
- **작업**:
  1. 프론트엔드 `keyword` 파라미터를 백엔드 기대값 `search`로 변경 (또는 백엔드를 `keyword`로 수정)
  2. 통계 페이지의 select 컴포넌트가 exercises API 응답 데이터를 올바르게 option으로 바인딩하는지 확인
  3. `defaultSetsMin/Max` 필드 타입(nullable) 일치 확인
- **검증**: 통계 > 운동별 탭에서 드롭다운 클릭 시 전체 운동 목록 표시 확인

### 높은 우선순위 수정 (MVP 품질)

#### 4. 하단 네비게이션 라우팅 연결 (BUG-11)
- **파일**:
  - `frontend/src/components/BottomTabBar.tsx` — 29~30행의 `navigate('/')` 하드코딩 제거
  - `frontend/src/router/index.tsx` — `/history` 라우트 추가, 운동 탭 경로 결정
- **작업**:
  1. "운동" 탭: 세션 생성 후 세션 페이지로 이동하는 플로우 연결 (현재 `/sessions/new`로 정의되어 있으나 하드코딩으로 무효화됨)
  2. "기록" 탭: HistoryPage 컴포넌트 생성 + 라우터에 `/history` 경로 등록
  3. `BottomTabBar.tsx:29-30`의 임시 `navigate('/')` 코드 제거
- **검증**: 하단 4개 탭 모두 클릭 시 해당 페이지로 정상 이동

#### 5. 로그아웃 기능 구현 (BUG-04)
- **파일**: 상단 헤더 또는 프로필 메뉴 컴포넌트
- **작업**: 프로필 아이콘 + 드롭다운 메뉴에 로그아웃 버튼 추가. 클릭 시 토큰 제거 + `/auth`로 리다이렉트
- **검증**: 로그아웃 후 보호된 페이지 접근 시 로그인 페이지로 리다이렉트 확인

#### 6. API 에러 핸들링 및 사용자 피드백 (BUG-10, IMP-04)
- **파일**: Axios 인터셉터(`/src/api/client.ts`), React Router errorElement
- **작업**:
  - 응답 인터셉터에서 4xx/5xx 에러 시 토스트 알림 표시
  - React Router에 ErrorBoundary 추가하여 컴포넌트 크래시 시 에러 페이지 표시
- **검증**: 의도적으로 API 에러 유발 시 토스트 알림 확인

### 중간 우선순위 (기능 완성)

#### 7. 대시보드 빈 상태 UI 및 개인 기록 섹션 (BUG-05, BUG-06)
- **작업**: 운동 기록이 없을 때 Empty State 컴포넌트 표시. PR 뱃지 컴포넌트 연동
- **검증**: 신규 사용자 대시보드에서 안내 메시지 확인, 기록 있는 사용자에서 PR 뱃지 확인

#### 8. 이메일 인증 플로우 정비 (BUG-03)
- **작업**: MVP 범위에서 이메일 인증을 포함할지 결정 필요. 포함한다면 인증 안내 모달 + 인증 링크 처리 구현
- **검증**: 회원가입 → 이메일 인증 안내 → 인증 완료 → 로그인 플로우 확인

---

## 5. 테스트 증적 (GIF 녹화)

| 파일명 | 내용 |
|---|---|
| TC01-signup-flow.gif | 회원가입 플로우 (유효성 검사 → 가입 → 대시보드) |
| TC02-login-flow.gif | 로그인 플로우 (인증 → 대시보드) |
| TC04-07-session-report-bugs.gif | 세션 생성 500 에러, 네비 미작동, 통계 크래시 |

---

## 6. 근본 원인 분석 요약

**대부분의 Critical/High 버그는 단일 근본 원인에서 파생됨:**

> **백엔드 API 응답 DTO와 프론트엔드 TypeScript 타입 간의 계약(Contract) 불일치**

| 불일치 유형 | 영향받는 버그 | 예시 |
|---|---|---|
| 필드명 불일치 | BUG-01 | `totalVolume` vs `totalVolumeKg` |
| 필드 누락 | BUG-02, BUG-13 | `weeklyGoalSessions`, `volumeChangePercent` 백엔드 미반환 |
| 파라미터명 불일치 | BUG-12 | `keyword` vs `search` |
| 타입 캐스팅 오류 | BUG-09 | JWT principal → Long 변환 실패 |

**권장 사항**: 백엔드 `ReportDtos.kt`와 프론트엔드 `types/domain.ts`를 나란히 놓고 모든 필드의 이름·타입·nullable 여부를 일괄 대조하여 통일하는 것이 가장 효율적입니다. OpenAPI/Swagger spec 자동 생성을 도입하면 이러한 불일치를 방지할 수 있습니다.

---

## 7. 테스트 환경 정보

- **프론트엔드**: React 19 + TypeScript + Vite 5, localhost:5173
- **백엔드**: Spring Boot 4 + Kotlin, localhost:8080, H2 인메모리 DB
- **브라우저**: Chrome (최신 버전)
- **테스트 계정**: test@example.com / Test1234!
