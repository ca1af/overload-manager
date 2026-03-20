# docs 디렉토리 구조

Overload Manager 프로젝트의 문서 디렉토리.

## 구조

```
docs/
├── planning/                        # 기획 산출물 (비기술)
│   ├── prd/                         # 기능별 PRD
│   │   └── overload-manager.md      # 핵심 가치, 기능 목록, 유저 스토리, MVP 범위
│   ├── user-flows/                  # 유저 플로우
│   │   └── overload-manager.md      # 전체 플로우, 상세 플로우 6개, 운동 DB, 과부하 원칙
│   ├── wireframes/                  # 와이어프레임 (텍스트 기반)
│   │   └── overload-manager.md      # 화면별 레이아웃, UI 컴포넌트, 인터랙션
│   └── requirements/                # 요구사항 라이프사이클
│       ├── backlog/                 # 제안됨 (미승인)
│       ├── approved/                # 승인됨 (PRD 작성 대상)
│       └── archive/                 # 완료 / 거부 / 대체됨
│
├── design/                          # 기술 설계 (BE/FE 공통)
│   ├── architecture.md              # 기술 스택, Hexagonal Architecture + DDD 패키지 구조
│   ├── erd.md                       # ERD (Mermaid), 엔티티 설명, 인덱스 전략
│   └── api-spec.md                  # REST API 명세 (인증, 사용자, 운동, 세션, 세트, 리포트, 루틴)
│
├── features/                        # 기능 추적 (BE/FE 통합)
│   ├── done/                        # 구현 완료된 기능
│   │   ├── auth.md                  # 인증 (회원가입/로그인/갱신/로그아웃)
│   │   ├── exercise-management.md   # 운동 목록, 검색, 상세
│   │   ├── workout-session.md       # 세션 CRUD, 운동 추가/제거
│   │   ├── set-recording.md         # 세트 CRUD, 자동 저장
│   │   ├── previous-session.md      # 이전 세션 조회 (과부하 참고)
│   │   ├── exercise-history.md      # 운동 히스토리, 1RM
│   │   ├── weekly-report.md         # 주간 요약 리포트
│   │   └── rest-timer.md            # 휴식 타이머 (프론트엔드 전용)
│   └── todo/                        # 미구현 기능
│       ├── routine-management.md    # P1: 루틴 템플릿
│       ├── volume-trend-chart.md    # P1: 볼륨 트렌드 시각화
│       ├── custom-exercise.md       # P1: 커스텀 운동 등록
│       └── body-weight-log.md       # P1: 몸무게 기록
│
└── README.md                        # 이 파일
```

## 문서 작성 규칙

- **기획 문서** (`planning/`): 기능의 "무엇"을 정의. 기술 프레임워크명 포함 금지.
- **설계 문서** (`design/`): 기술적 "어떻게"를 정의. 아키텍처, ERD, API 명세.
- **기능 추적** (`features/`): 각 기능의 구현 현황. 기획 참조 + 백엔드/프론트엔드 구현 사항 + 수용 기준.

## 기능 라이프사이클

```
planning/prd/ + user-flows/ + wireframes/
    |
    v
features/todo/{feature-name}.md   (기획 → 엔지니어링 인수인계)
    |
    v (구현 완료 시)
features/done/{feature-name}.md
```
