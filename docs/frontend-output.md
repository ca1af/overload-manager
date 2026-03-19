# 프론트엔드 기술 스택 최종 확정

## designer 추천 스택 검토

designer(팀원 B)가 추천한 스택은 다음과 같다:

| 영역 | 추천 기술 | 검토 결과 |
|------|-----------|-----------|
| 프레임워크 | React 18 + TypeScript 5 | **채택** — 생태계 성숙도, P2 React Native 전환 용이성 |
| 빌드 도구 | Vite 5 | **채택** — 개발 서버 속도, Spring Boot 프록시 설정 간단 |
| 라우팅 | React Router 6 | **채택** — 표준적, data router API로 loader/action 활용 가능 |
| 서버 상태 관리 | TanStack Query v5 | **채택** — 낙관적 업데이트, stale-while-revalidate, 세트 기록 UX에 필수 |
| 클라이언트 상태 | Zustand v4 | **채택** — 타이머, 진행 중 세션 임시 데이터에 적합, 보일러플레이트 최소 |
| UI 컴포넌트 | shadcn/ui + Tailwind CSS | **채택** — Radix 기반 접근성, 소스 복사 방식으로 커스터마이징 자유도 |
| 차트 | Recharts 2 | **채택** — React 컴포넌트 기반, ResponsiveContainer로 반응형 처리 용이 |
| 폼 관리 | React Hook Form + Zod | **채택** — 세트 입력 유효성 검사 선언적 처리, 리렌더링 최소화 |
| HTTP 클라이언트 | Axios | **채택** — 인터셉터로 JWT 자동 첨부 및 토큰 갱신 처리 |
| 애니메이션 | Framer Motion 11 | **채택** — 바텀시트 슬라이드, lazy import로 번들 크기 관리 |
| 날짜 처리 | date-fns 3 | **채택** — 경량, 주간 계산 및 ISO 날짜 변환에 충분 |
| 테스트 | Vitest + Testing Library | **채택** — Vite 환경과 통합, DOM 기반 컴포넌트 테스트 |

## 최종 결정 및 근거

designer 추천 스택을 **전면 채택**한다. 추가 검토 사항:

### 추가 결정 사항 (designer 합의 완료 — Task #5)

1. **토큰 저장 전략**: Access Token은 메모리(Zustand store)에 저장, Refresh Token은 `httpOnly` 쿠키로 관리 (backend와 합의 완료 — Task #6. Axios `withCredentials: true` 설정 필수)
2. **API 요청 레이어**: Axios 인스턴스 1개 (`/src/api/client.ts`), 요청 인터셉터에서 Authorization 헤더 자동 첨부, 응답 인터셉터에서 401 시 토큰 갱신 후 재시도
3. **단위 변환**: 백엔드 API는 항상 kg 기준. 사용자 `weightUnit` 설정에 따라 프론트에서 표시 변환 (`kg ↔ lb`), Zod 스키마에 변환 로직 포함
4. **자동 저장**: 세트 입력 후 debounce 500ms 후 PATCH API 호출 (designer 와이어프레임 명세와 일치)
5. **오프라인 대응**: MVP는 네트워크 오류 시 사용자 안내 토스트만 표시. localStorage에 진행 중 세션 ID만 보존하여 재접속 시 세션 복원 지원
6. **Framer Motion lazy import 범위**: OverloadInfoSheet, SessionSummaryModal에만 lazy import 적용. 페이지 전환은 Tailwind CSS transition으로 대체 (운동 중 즉시 전환 UX 우선)
7. **바텀시트 구현**: **vaul** 라이브러리 추가 채택 (shadcn/ui 공식 권장). 드래그 핸들, 스냅 포인트, 80% 높이 제한, 드래그 다운 닫기를 네이티브 수준으로 지원. OverloadInfoSheet 전용 적용
8. **PWA 설정**: MVP 이후 추가. 단, API 레이어(Axios 인스턴스)를 깔끔한 추상화로 유지하여 향후 Service Worker 추가 시 변경 최소화

---

# 화면별 컴포넌트 트리

## 로그인/회원가입

```
AuthPage
├── AuthTabs                          # [로그인 | 회원가입] 탭 전환
│   ├── LoginForm
│   │   ├── EmailInput
│   │   ├── PasswordInput             # 보기 토글 포함
│   │   ├── SubmitButton              # 로딩 스피너 내장
│   │   └── FormErrorMessage
│   └── RegisterForm
│       ├── NicknameInput
│       ├── EmailInput
│       ├── PasswordInput
│       ├── PasswordConfirmInput
│       ├── WeightUnitRadioGroup      # kg / lb 선택
│       ├── SubmitButton
│       └── FormErrorMessage
├── SocialLoginButton                 # (비활성, 향후 Google)
└── EmailVerificationModal            # 회원가입 완료 후 안내
```

**상태**: React Hook Form + Zod. 폼 로컬 상태만 사용, 서버 상태 없음.

---

## 대시보드

```
DashboardPage
├── AppHeader                         # sticky, 전역 공유 컴포넌트
│   ├── Logo
│   ├── NotificationBell
│   └── UserAvatar (ProfileMenu)
├── GreetingSection
│   ├── UserGreeting                  # "안녕하세요 {nickname}님"
│   └── TodayDate
├── WeeklySummaryCard                 # TanStack Query: useWeeklySummary()
│   ├── StatItem (운동 횟수)
│   ├── StatItem (총 볼륨)
│   └── StatItem (과부하 달성)
├── StartWorkoutButton                # → 세션 생성 플로우 진입
├── RecentSessionList                 # TanStack Query: useSessions({ size: 5 })
│   └── SessionCard (×n)
│       ├── SessionDate
│       ├── SessionMeta               # 운동 수, 세트, 볼륨
│       └── ChevronRightIcon
├── PersonalRecordList                # TanStack Query: useRecentPRs()
│   └── PRBadge (×n)
│       ├── TrophyIcon
│       ├── ExerciseName
│       └── PRDetail
└── BottomTabBar                      # 전역 공유 컴포넌트
    ├── TabItem (홈)
    ├── TabItem (운동)
    ├── TabItem (기록)
    └── TabItem (통계)
```

---

## 운동 선택

```
ExerciseSelectPage
├── PageHeader
│   ├── BackButton
│   ├── Title ("운동 추가")
│   └── SelectionCount Badge          # "완료(3)"
├── ExerciseSearchInput               # 실시간 필터, debounce 300ms
├── CategoryFilterTabs                # 가로 스크롤, TanStack Query: useExercises()
│   └── CategoryTab (×7)             # 전체/가슴/등/하체/어깨/팔/코어
├── ExerciseList
│   ├── SectionHeader (복합 운동 / 고립 운동)
│   └── ExerciseItem (×n)
│       ├── CheckboxIndicator
│       ├── ExerciseName
│       ├── ExerciseMeta              # 카테고리·타입·장비
│       └── ExerciseDetailSheet      # long press → Radix Sheet
│           ├── PrimaryMuscle
│           ├── SecondaryMuscles
│           └── DefaultRepsRange
├── CustomExerciseButton              # P1, "+ 커스텀 운동 추가"
└── SelectionSummaryBar               # sticky bottom
    ├── SelectedCount
    └── ConfirmAddButton
```

**상태**:
- `selectedExerciseIds: Set<number>` — Zustand 세션 스토어
- 검색어·카테고리 필터 — 로컬 useState
- 운동 목록 — TanStack Query (`useExercises`)

---

## 과부하 설정 (바텀시트)

```
OverloadInfoSheet                     # Framer Motion 바텀시트, 독립 화면 아님
├── SheetDragHandle
├── SheetTitle                        # "{운동명} 과부하 현황"
├── PreviousSessionTable              # TanStack Query: usePreviousSession()
│   └── PreviousSetRow (×n)
│       ├── SetNumber
│       ├── WeightDisplay
│       ├── RepsDisplay
│       └── CompletedIcon
├── VolumeTotal                       # 직전 세션 총 볼륨
├── EstimatedOneRepMax
├── TargetSuggestionCards
│   ├── SuggestionCard (중량 증가)   # 탭 시 세트 입력 필드 자동 채움
│   └── SuggestionCard (반복수 증가)
├── MiniTrendChart                    # Recharts LineChart, 8주 최대 중량
└── CloseButton
```

**상태**: TanStack Query — `usePreviousSession(exerciseId, excludeSessionId)`

---

## 운동 기록 입력 (세션 진행)

### 세션 운동 목록 화면

```
ActiveSessionPage
├── PageHeader
│   ├── BackButton
│   ├── Title + SessionDate
│   ├── AddExerciseButton
│   └── SessionOptionsMenu            # 메모 편집, 세션 삭제
├── SessionElapsedTimer               # Zustand 타이머 스토어, 초 단위 카운트업
├── SessionExerciseList               # TanStack Query: useSessionDetail()
│   └── SessionExerciseCard (×n)
│       ├── ExerciseName
│       ├── SetProgressIndicator      # "3/3 완료 ✓" / "1/4 진행 중" / "대기 중"
│       ├── BestVolumeDisplay
│       └── ChevronRightIcon          # → SetRecordPage
├── FinishSessionButton               # → 세션 종료 확인 모달
└── SessionSummaryModal               # 세션 종료 후 표시
    ├── TotalExerciseCount
    ├── TotalSetsCount
    ├── TotalVolumeDisplay
    ├── ElapsedTime
    ├── PersonalRecordList
    ├── HomeButton
    └── ViewReportButton
```

### 세트 기록 화면

```
SetRecordPage
├── PageHeader
│   ├── BackButton
│   ├── ExerciseName
│   ├── PreviousRecordButton          # → OverloadInfoSheet 열기
│   └── ExerciseOptionsMenu
├── PreviousSessionSummaryBanner      # "이전 세션: 80kg × 8회 × 3세트" 항상 표시
├── SetTable
│   ├── SetTableHeader                # 세트 / 무게(kg) / 반복수 / 완료
│   └── SetRow (×n)                  # TanStack Query: 낙관적 업데이트
│       ├── SetNumber
│       ├── WeightInput               # 숫자 키패드, placeholder = 이전 세션 값
│       ├── RepsInput                 # 숫자 키패드
│       ├── CompleteCheckbox          # 48×48px, 체크 시 완료 처리 + 타이머 팝업
│       └── SwipeDeleteAction         # 좌 스와이프 → 삭제
├── AddSetButton
├── TotalVolumeDisplay                # 완료된 세트만 실시간 합산
└── RestTimerOverlay                  # Zustand 타이머 스토어
    ├── CircularProgress
    ├── CountdownDisplay
    ├── ExtendButton (+30초)
    └── SkipButton
```

**상태**:
- 세션 데이터 — TanStack Query (낙관적 업데이트로 즉각 반응)
- 휴식 타이머 카운트다운 — Zustand `restTimerStore`
- 세션 경과 타이머 — Zustand `sessionTimerStore`
- 자동 저장 — debounce 500ms 후 PATCH API 호출, 실패 시 로컬 캐시 큐

---

## 리포트/통계

```
ReportPage
├── PageTitle ("통계 및 리포트")
├── ReportSegmentControl
│   ├── ExerciseTab                   # 운동별
│   └── WeeklySummaryTab              # 주간 요약
│
├── [운동별 탭]
│   ├── ExerciseSelector              # 드롭다운, 사용자가 기록한 운동 목록
│   ├── PeriodFilterGroup             # [1개월] [3개월] [6개월] [전체] pill 탭
│   ├── MaxWeightTrendCard
│   │   ├── LineChart (Recharts)      # TanStack Query: useExerciseHistory()
│   │   ├── PersonalRecord            # "최고 기록: 90kg"
│   │   └── ChartTooltip → SessionDetailSheet
│   ├── WeeklyVolumeTrendCard
│   │   ├── BarChart (Recharts)       # TanStack Query: useVolumeTrend()
│   │   └── AverageVolumeDisplay
│   ├── EstimatedOneRepMaxCard        # TanStack Query: useEstimated1RM()
│   └── SessionHistoryList
│       └── SessionHistoryRow (×n)   # 날짜, 세트 요약, 볼륨
│           └── → SessionDetailSheet (탭 시)
│
└── [주간 요약 탭]
    ├── WeekNavigation                # < 이번 주 (3/17~3/23) >
    ├── WeekSummaryCards
    │   ├── StatCard (운동 횟수)
    │   ├── StatCard (총 볼륨)
    │   └── StatCard (과부하 달성)
    ├── VolumeProgressBar             # vs 지난 주 달성률
    ├── OverloadAchievementList       # TanStack Query: useWeeklySummary()
    │   └── OverloadAchievementRow (×n)
    │       ├── ExerciseName
    │       ├── AchievedIcon          # ✓ (녹색) / ✗ (빨간색)
    │       └── AchievementDetail     # "+2.5kg" / "변화 없음"
    └── WeeklyCalendarDots            # 월~일 운동일 표시
        └── DayDot (×7)
```

---

# 상태 관리 전략

## 서버 상태 — TanStack Query

서버에서 가져오는 모든 데이터를 관리한다. 쿼리 키 구조:

```typescript
// 쿼리 키 팩토리
const queryKeys = {
  exercises: (params?: ExerciseQueryParams) => ['exercises', params] as const,
  exerciseDetail: (id: number) => ['exercises', id] as const,
  previousSession: (exerciseId: number, excludeSessionId?: number) =>
    ['exercises', exerciseId, 'previous-session', excludeSessionId] as const,
  exerciseHistory: (exerciseId: number, params?: HistoryParams) =>
    ['exercises', exerciseId, 'history', params] as const,
  volumeTrend: (exerciseId: number, params?: TrendParams) =>
    ['exercises', exerciseId, 'volume-trend', params] as const,
  estimated1RM: (exerciseId: number) =>
    ['exercises', exerciseId, 'estimated-1rm'] as const,
  sessions: (params?: SessionQueryParams) => ['sessions', params] as const,
  sessionDetail: (id: number) => ['sessions', id] as const,
  weeklySummary: (date?: string) => ['reports', 'weekly-summary', date] as const,
  me: () => ['users', 'me'] as const,
};
```

**낙관적 업데이트 전략** (세트 완료 체크):
```typescript
// SET 완료 처리 — 즉각 UI 반응, 실패 시 롤백
mutationFn: patchSet,
onMutate: async (variables) => {
  await queryClient.cancelQueries(queryKeys.sessionDetail(sessionId));
  const snapshot = queryClient.getQueryData(queryKeys.sessionDetail(sessionId));
  queryClient.setQueryData(queryKeys.sessionDetail(sessionId), (old) =>
    updateSetInSession(old, variables)
  );
  return { snapshot };
},
onError: (_, __, context) => {
  queryClient.setQueryData(queryKeys.sessionDetail(sessionId), context.snapshot);
},
onSettled: () => {
  queryClient.invalidateQueries(queryKeys.sessionDetail(sessionId));
}
```

## 클라이언트 상태 — Zustand

```typescript
// 인증 스토어
interface AuthStore {
  accessToken: string | null;
  user: User | null;
  setAuth: (token: string, user: User) => void;
  clearAuth: () => void;
}

// 세션 진행 스토어 (운동 중 임시 상태)
interface ActiveSessionStore {
  activeSessionId: number | null;
  elapsedSeconds: number;           // 세션 경과 타이머
  selectedExerciseIds: Set<number>; // 운동 선택 화면
  setActiveSession: (id: number) => void;
  clearActiveSession: () => void;
  incrementElapsed: () => void;
}

// 휴식 타이머 스토어
interface RestTimerStore {
  isRunning: boolean;
  remainingSeconds: number;
  defaultDurationSeconds: number;   // 사용자 설정 기본값
  start: (durationSeconds?: number) => void;
  extend: (seconds: number) => void;
  skip: () => void;
  tick: () => void;                 // setInterval에서 호출
}
```

## 상태 흐름 요약

```
[서버 API]
    ↕ (TanStack Query: fetch, cache, sync)
[쿼리 캐시]
    ↓ (useQuery, useMutation)
[컴포넌트]
    ↕ (useStore)
[Zustand 스토어]
  - authStore: 토큰, 유저 정보
  - activeSessionStore: 진행 중 세션 ID, 경과 타이머
  - restTimerStore: 휴식 타이머 카운트다운
```

---

# API 연동 계획

## Axios 인스턴스 구성

```typescript
// src/api/client.ts
const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api/v1',
  timeout: 10000,
  withCredentials: true,  // Refresh Token httpOnly 쿠키 전송
});

// 요청 인터셉터: Access Token 자동 첨부
apiClient.interceptors.request.use((config) => {
  const token = authStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 응답 인터셉터: 401 시 토큰 갱신 후 재시도
apiClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    if (error.response?.status === 401 && !error.config._retry) {
      error.config._retry = true;
      const { accessToken } = await refreshTokenApi();
      authStore.getState().setAuth(accessToken, authStore.getState().user!);
      error.config.headers.Authorization = `Bearer ${accessToken}`;
      return apiClient(error.config);
    }
    return Promise.reject(error);
  }
);
```

## 화면별 API 매핑

| 화면 | 사용 API | Hook |
|------|----------|------|
| 로그인 | `POST /auth/login` | `useLoginMutation` |
| 회원가입 | `POST /auth/register` | `useRegisterMutation` |
| 대시보드 | `GET /reports/weekly-summary`, `GET /sessions?size=5` | `useWeeklySummary`, `useSessions` |
| 운동 선택 | `GET /exercises?category=&query=` | `useExercises` |
| 세션 생성 | `POST /sessions` | `useCreateSessionMutation` |
| 운동 추가 | `POST /sessions/{id}/exercises` | `useAddExercisesMutation` |
| 세트 기록 | `POST /sets` (setNumber optional, 서버 자동 채번), `PATCH /sets/{id}`, `DELETE /sets/{id}` | `useCreateSetMutation`, `usePatchSetMutation` |
| 이전 기록 조회 | `GET /exercises/{id}/previous-session` | `usePreviousSession` |
| 세션 상세 | `GET /sessions/{id}` (응답에 `exerciseCount`, `totalSets`, `totalVolumeKg`, `durationSeconds` 집계 필드 포함) | `useSessionDetail` |
| 세션 종료 | `PATCH /sessions/{id}` (finished: true, 응답에 `finishedAt`, `durationSeconds`, `exerciseCount`, `totalSets`, `totalVolumeKg` 포함 — 추가 API 호출 없이 요약 모달 렌더링 가능) | `useFinishSessionMutation` |
| 과부하 정보 | `GET /exercises/{id}/previous-session`, `GET /exercises/{id}/estimated-1rm` | `usePreviousSession`, `useEstimated1RM` |
| 리포트 (운동별) | `GET /exercises/{id}/history`, `GET /exercises/{id}/volume-trend` | `useExerciseHistory`, `useVolumeTrend` |
| 리포트 (주간) | `GET /reports/weekly-summary?date=` (응답에 `weeklyGoalSessions` 포함 — USER 테이블 컬럼 추가됨) | `useWeeklySummary` |

## 단위 변환 유틸리티

```typescript
// src/utils/weight.ts
// 백엔드는 항상 kg. 사용자 설정에 따라 표시 변환.
export const toDisplayWeight = (kg: number, unit: 'kg' | 'lb'): number =>
  unit === 'lb' ? Math.round(kg * 2.20462 * 10) / 10 : kg;

export const toKg = (value: number, unit: 'kg' | 'lb'): number =>
  unit === 'lb' ? Math.round((value / 2.20462) * 100) / 100 : value;
```

## 에러 처리 전략

- **400 INVALID_INPUT**: Zod 스키마로 클라이언트 선검증 후 폼 인라인 에러 표시
- **401 UNAUTHORIZED**: 인터셉터에서 자동 갱신 시도, 실패 시 로그인 페이지 리다이렉트
- **403 FORBIDDEN**: 토스트 알림 ("접근 권한이 없습니다")
- **404 NOT_FOUND**: 일반 리소스 없음 에러 처리. 단, `GET /exercises/{id}/previous-session`은 backend 합의로 404 → 200 빈 응답(`sets: []`)으로 변경됨 — `sets.length === 0`으로 빈 상태 UI 렌더링
- **500 INTERNAL_ERROR**: 토스트 알림 + Sentry 로깅 (P1)
- **네트워크 오류**: 토스트 알림, TanStack Query 자동 재시도 (3회, exponential backoff)

---

# 프로젝트 디렉토리 구조

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
│   │   └── schemas.ts               # Zod 스키마 (LoginSchema, RegisterSchema)
│   │
│   ├── exercise/
│   │   ├── components/
│   │   │   ├── ExerciseItem.tsx
│   │   │   ├── ExerciseDetailSheet.tsx
│   │   │   ├── CategoryFilterTabs.tsx
│   │   │   └── SelectionSummaryBar.tsx
│   │   └── hooks/
│   │       └── useExercises.ts      # TanStack Query
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
│   │   │   ├── useSetMutations.ts   # create, patch, delete
│   │   │   ├── usePreviousSession.ts
│   │   │   └── useAutoSave.ts       # debounce 500ms 자동 저장
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
│   ├── useWeightUnit.ts             # kg ↔ lb 변환, 사용자 설정 반영
│   └── useDebounce.ts
│
├── pages/                           # 라우트별 페이지 (레이아웃 조합)
│   ├── AuthPage.tsx                 # /auth
│   ├── DashboardPage.tsx            # /
│   ├── ExerciseSelectPage.tsx       # /sessions/:id/exercises/add
│   ├── ActiveSessionPage.tsx        # /sessions/:id
│   ├── SetRecordPage.tsx            # /sessions/:id/exercises/:exerciseId
│   └── ReportPage.tsx               # /report
│
├── router/
│   └── index.tsx                    # React Router 6 라우트 정의, ProtectedRoute
│
├── store/                           # 전역 Zustand 스토어
│   └── authStore.ts                 # accessToken, user, setAuth, clearAuth
│
├── types/
│   ├── api.ts                       # API 요청/응답 타입 (백엔드 DTO 미러링)
│   ├── domain.ts                    # 도메인 모델 타입
│   └── common.ts                    # 공통 타입 (WeightUnit, Category...)
│
└── utils/
    ├── weight.ts                    # kg ↔ lb 변환
    ├── volume.ts                    # 볼륨 계산 (sets × reps × weight)
    └── oneRepMax.ts                 # Epley 공식 클라이언트 사이드 계산
```

## 라우트 구조

```typescript
// src/router/index.tsx
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

## 환경 변수

```
# .env.development
VITE_API_BASE_URL=http://localhost:8080/api/v1

# .env.production
VITE_API_BASE_URL=https://api.overload-manager.com/api/v1
```

## Vite 프록시 설정 (개발 환경)

```typescript
// vite.config.ts
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true,
    },
  },
},
```
