# 아키텍처 설계

## 기술 스택

### 백엔드

| 영역 | 기술 | 버전 |
|------|------|------|
| 프레임워크 | Spring Boot | 3.3.x |
| 언어 | Kotlin | 1.9.x |
| ORM | Spring Data JPA | - |
| DB | MySQL | - |
| 마이그레이션 | Flyway | - |
| 보안 | Spring Security + JJWT | 0.12.x |
| 빌드 | Gradle (Kotlin DSL) | - |
| 테스트 | JUnit 5 + H2 (인메모리) | - |

### 프론트엔드

| 영역 | 기술 | 버전 | 선정 근거 |
|------|------|------|-----------|
| 프레임워크 | React + TypeScript | 18.x / 5.x | 생태계 성숙도, P2 React Native 전환 용이성 |
| 빌드 도구 | Vite | 5.x | 개발 서버 속도, Spring Boot 프록시 설정 간단 |
| 라우팅 | React Router | 6.x | data router API 활용 |
| 서버 상태 관리 | TanStack Query | v5 | 낙관적 업데이트, stale-while-revalidate |
| 클라이언트 상태 | Zustand | v4 | 타이머·임시 데이터에 적합, 보일러플레이트 최소 |
| UI 컴포넌트 | shadcn/ui + Tailwind CSS | - | Radix 기반 접근성, 커스터마이징 자유도 |
| 바텀시트 | vaul | - | 드래그 핸들·스냅 포인트 네이티브 수준 지원 |
| 차트 | Recharts | 2.x | React 컴포넌트 기반, 반응형 처리 용이 |
| 폼 관리 | React Hook Form + Zod | - | 세트 입력 유효성 검사, 리렌더링 최소화 |
| HTTP | Axios | - | 인터셉터로 JWT 자동 첨부 및 토큰 갱신 |
| 애니메이션 | Framer Motion | 11.x | 바텀시트·모달 전용 lazy import |
| 날짜 | date-fns | 3.x | 경량, 주간 계산에 충분 |
| 테스트 | Vitest + Testing Library | - | Vite 환경 통합 |

### 주요 기술 결정 근거

**React 18 + TypeScript** — Kotlin 타입 시스템과 유사한 개발 경험. React Native로 코드 공유 가능(비즈니스 로직, API 훅, Zod 스키마). Concurrent Features로 타이머+입력 동시 처리.

**TanStack Query + Zustand** — 서버 상태(운동 목록, 세션 기록, 통계)는 TanStack Query로 캐싱·동기화·낙관적 업데이트. 클라이언트 상태(타이머, 임시 세션)는 Zustand로 최소 보일러플레이트 관리. Redux를 택하지 않은 이유: 이 규모에서 미들웨어·액션·리듀서 구조가 과도.

**shadcn/ui + Tailwind CSS** — Radix UI 기반 접근성 완비. 소스 복사 방식으로 운동 앱 특화 커스터마이징. Tailwind로 모바일 퍼스트 반응형 빠른 구현.

**Framer Motion lazy import 정책** — OverloadInfoSheet + SessionSummaryModal에만 적용. 페이지 전환은 CSS transition (운동 중 즉시 전환 UX 우선).

**P2 모바일 확장** — React Native + Expo로 비즈니스 로직/API 훅/Zod 스키마 재사용. PWA는 초기 구조만 포함, 서비스 워커 비활성화 상태로 시작.

---

## 백엔드 아키텍처

### 아키텍처 패턴: Hexagonal Architecture + DDD

단일 모듈(Single Module) Spring Boot 프로젝트에서 Hexagonal Architecture와 DDD 패키지 구조를 적용한다.

### 도메인별 패키지 구조

각 비즈니스 도메인(auth, user, exercise, workout, routine, report)은 동일한 헥사고날 패키지 구조를 따른다.

```
com.calaf.overloadmanager.{domain}/
├── domain/
│   ├── model/       # 도메인 엔티티, 값 객체(Value Object), enum
│   └── port/
│       ├── in/      # 입력 포트 인터페이스 (유스케이스)
│       └── out/     # 출력 포트 인터페이스 (레포지토리)
├── application/
│   └── service/     # 유스케이스 구현체
└── adapter/
    ├── in/
    │   └── web/     # REST 컨트롤러 + 요청/응답 DTO
    └── out/
        └── persistence/  # JPA 엔티티, Spring Data 레포지토리
```

### 공유 인프라스트럭처

```
com.calaf.overloadmanager/
├── common/          # 공유 에러 처리, 응답 래퍼
│   ├── exception/
│   │   ├── AppException.kt        # 커스텀 예외 베이스
│   │   ├── ErrorCode.kt           # 에러 코드 enum
│   │   └── GlobalExceptionHandler.kt
│   ├── response/
│   │   ├── ApiResponse.kt         # 표준 응답 래퍼
│   │   └── PageResponse.kt
│   └── util/
│       └── OneRmCalculator.kt     # Epley 공식
├── config/          # Spring 설정 (Security, JPA, CORS)
│   ├── SecurityConfig.kt
│   ├── JpaConfig.kt
│   └── WebMvcConfig.kt
└── infrastructure/
    └── jwt/         # JWT 프로바이더, 인증 필터
        ├── JwtTokenProvider.kt
        └── JwtAuthenticationFilter.kt
```

### 도메인별 구조 예시

#### auth 도메인

```
com.calaf.overloadmanager.auth/
├── domain/
│   ├── model/
│   │   └── RefreshToken.kt
│   └── port/
│       ├── in/
│       │   ├── RegisterUseCase.kt
│       │   ├── LoginUseCase.kt
│       │   ├── RefreshTokenUseCase.kt
│       │   └── LogoutUseCase.kt
│       └── out/
│           └── RefreshTokenRepository.kt
├── application/
│   └── service/
│       └── AuthService.kt
└── adapter/
    ├── in/
    │   └── web/
    │       ├── AuthController.kt
    │       └── dto/
    │           ├── LoginRequest.kt
    │           ├── LoginResponse.kt
    │           └── RegisterRequest.kt
    └── out/
        └── persistence/
            ├── RefreshTokenJpaEntity.kt
            └── RefreshTokenJpaRepository.kt
```

#### workout 도메인 (핵심)

```
com.calaf.overloadmanager.workout/
├── domain/
│   ├── model/
│   │   ├── WorkoutSession.kt
│   │   ├── SessionExercise.kt
│   │   └── WorkoutSet.kt
│   └── port/
│       ├── in/
│       │   ├── CreateSessionUseCase.kt
│       │   ├── ManageSessionExerciseUseCase.kt
│       │   ├── RecordSetUseCase.kt
│       │   └── OverloadDetectionUseCase.kt
│       └── out/
│           ├── WorkoutSessionRepository.kt
│           ├── SessionExerciseRepository.kt
│           └── WorkoutSetRepository.kt
├── application/
│   └── service/
│       ├── WorkoutSessionService.kt
│       ├── WorkoutSetService.kt
│       └── OverloadDetectionService.kt
└── adapter/
    ├── in/
    │   └── web/
    │       ├── WorkoutSessionController.kt
    │       ├── WorkoutSetController.kt
    │       └── dto/
    └── out/
        └── persistence/
            ├── WorkoutSessionJpaEntity.kt
            ├── SessionExerciseJpaEntity.kt
            ├── WorkoutSetJpaEntity.kt
            ├── WorkoutSessionJpaRepository.kt
            ├── SessionExerciseJpaRepository.kt
            └── WorkoutSetJpaRepository.kt
```

### 주요 의존성

```kotlin
// build.gradle.kts
dependencies {
    // Spring Boot Core
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Security & JWT
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("io.jsonwebtoken:jjwt-api:0.12.x")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.x")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.x")

    // Database
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")
    runtimeOnly("com.mysql:mysql-connector-j")

    // Kotlin
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // Email
    implementation("org.springframework.boot:spring-boot-starter-mail")

    // Test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
}
```

### Flyway 마이그레이션

```
src/main/resources/db/migration/
├── V1__create_users.sql
├── V2__create_refresh_tokens.sql
├── V3__create_exercises.sql
├── V4__insert_default_exercises.sql
├── V5__create_workout_sessions.sql
├── V6__create_session_exercises.sql
├── V7__create_workout_sets.sql
├── V8__create_routines.sql
└── V9__create_body_weight_logs.sql
```

---

## 프론트엔드 프로젝트 구조

```
src/
│
├── api/
│   ├── client.ts                    # Axios 인스턴스, 인터셉터
│   ├── auth.ts                      # 인증 API 함수
│   ├── exercises.ts                 # 운동 API 함수
│   ├── sessions.ts                  # 세션/세트 API 함수
│   └── reports.ts                   # 리포트 API 함수
│
├── components/                      # 공통 재사용 컴포넌트
│   ├── ui/                          # shadcn/ui 기반 (Button, Input, Sheet...)
│   ├── AppHeader.tsx
│   ├── BottomTabBar.tsx
│   ├── LoadingSpinner.tsx
│   ├── ToastProvider.tsx
│   └── ErrorBoundary.tsx
│
├── features/
│   ├── auth/
│   │   ├── components/
│   │   │   ├── LoginForm.tsx
│   │   │   ├── RegisterForm.tsx
│   │   │   └── EmailVerificationModal.tsx
│   │   ├── hooks/
│   │   │   ├── useLoginMutation.ts
│   │   │   └── useRegisterMutation.ts
│   │   └── schemas.ts               # Zod 스키마
│   │
│   ├── exercise/
│   │   ├── components/
│   │   │   ├── ExerciseItem.tsx
│   │   │   ├── ExerciseDetailSheet.tsx
│   │   │   ├── CategoryFilterTabs.tsx
│   │   │   └── SelectionSummaryBar.tsx
│   │   └── hooks/
│   │       └── useExercises.ts
│   │
│   ├── session/
│   │   ├── components/
│   │   │   ├── SessionCard.tsx
│   │   │   ├── SessionExerciseCard.tsx
│   │   │   ├── SetRow.tsx
│   │   │   ├── SetTable.tsx
│   │   │   ├── RestTimerOverlay.tsx
│   │   │   ├── SessionElapsedTimer.tsx
│   │   │   ├── OverloadInfoSheet.tsx
│   │   │   ├── MiniTrendChart.tsx
│   │   │   └── SessionSummaryModal.tsx
│   │   ├── hooks/
│   │   │   ├── useSessionDetail.ts
│   │   │   ├── useCreateSessionMutation.ts
│   │   │   ├── useSetMutations.ts
│   │   │   ├── usePreviousSession.ts
│   │   │   └── useAutoSave.ts
│   │   └── store.ts                 # Zustand: activeSession, restTimer
│   │
│   └── report/
│       ├── components/
│       │   ├── MaxWeightTrendChart.tsx
│       │   ├── WeeklyVolumeChart.tsx
│       │   ├── OverloadAchievementList.tsx
│       │   └── WeeklyCalendarDots.tsx
│       └── hooks/
│           ├── useExerciseHistory.ts
│           ├── useVolumeTrend.ts
│           ├── useEstimated1RM.ts
│           └── useWeeklySummary.ts
│
├── hooks/                           # 공통 커스텀 훅
│   ├── useWeightUnit.ts
│   └── useDebounce.ts
│
├── pages/
│   ├── AuthPage.tsx                 # /auth
│   ├── DashboardPage.tsx            # /
│   ├── ExerciseSelectPage.tsx       # /sessions/:id/exercises/add
│   ├── ActiveSessionPage.tsx        # /sessions/:id
│   ├── SetRecordPage.tsx            # /sessions/:id/exercises/:exerciseId
│   └── ReportPage.tsx               # /report
│
├── router/
│   └── index.tsx                    # React Router 6, ProtectedRoute
│
├── store/
│   └── authStore.ts                 # accessToken, user
│
├── types/
│   ├── api.ts                       # API 요청/응답 타입
│   ├── domain.ts                    # 도메인 모델 타입
│   └── common.ts                    # 공통 타입
│
└── utils/
    ├── weight.ts                    # kg <-> lb 변환
    ├── volume.ts                    # 볼륨 계산
    └── oneRepMax.ts                 # Epley 공식
```

### 라우트 구조

```typescript
const router = createBrowserRouter([
  {
    path: '/auth',
    element: <AuthPage />,           // 비로그인 전용
  },
  {
    path: '/',
    element: <ProtectedRoute />,     // 로그인 필요
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'sessions/new', element: <ExerciseSelectPage /> },
      { path: 'sessions/:sessionId', element: <ActiveSessionPage /> },
      {
        path: 'sessions/:sessionId/exercises/:sessionExerciseId',
        element: <SetRecordPage />,
      },
      { path: 'report', element: <ReportPage /> },
    ],
  },
]);
```

### 상태 관리 흐름

```
[서버 API]
    | (TanStack Query: fetch, cache, sync)
[쿼리 캐시]
    | (useQuery, useMutation)
[컴포넌트]
    | (useStore)
[Zustand 스토어]
  - authStore: 토큰, 유저 정보
  - activeSessionStore: 진행 중 세션 ID, 경과 타이머
  - restTimerStore: 휴식 타이머 카운트다운
```
