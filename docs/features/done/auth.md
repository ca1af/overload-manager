# 인증 (Auth)

## Overview
이메일+비밀번호 기반 회원가입, 로그인, 토큰 갱신, 로그아웃 기능. JWT(Access Token + Refresh Token) 기반 세션 관리.

## Planning References
- PRD: docs/planning/prd/overload-manager.md (P0-1)
- User Flow: docs/planning/user-flows/overload-manager.md (Flow 1)
- Wireframe: docs/planning/wireframes/overload-manager.md (3.1)

## Backend
- **엔티티**: User, RefreshToken
- **유스케이스**: RegisterUseCase, LoginUseCase, RefreshTokenUseCase, LogoutUseCase
- **API 엔드포인트**:
  - `POST /api/v1/auth/register` — 회원가입
  - `POST /api/v1/auth/login` — 로그인
  - `POST /api/v1/auth/refresh` — 토큰 갱신
  - `POST /api/v1/auth/logout` — 로그아웃
  - `POST /api/v1/auth/verify-email` — 이메일 인증
  - `GET /api/v1/users/me` — 내 정보 조회
  - `PATCH /api/v1/users/me` — 내 정보 수정
  - `DELETE /api/v1/users/me` — 회원 탈퇴 (소프트 삭제)
- **보안**: BCrypt 비밀번호 해싱, Refresh Token Rotation, HttpOnly 쿠키

## Frontend
- **페이지**: AuthPage (`/auth`)
- **컴포넌트**: LoginForm, RegisterForm, EmailVerificationModal, WeightUnitRadioGroup
- **상태**: React Hook Form + Zod 폼 검증, Zustand authStore (accessToken, user)
- **인터랙션**: 탭 전환(로그인/회원가입), 인라인 에러, 로딩 스피너, 성공 시 대시보드 이동

## Acceptance Criteria
- 이메일+비밀번호로 회원가입 가능
- 로그인 시 Access Token + Refresh Token 발급
- 토큰 만료 시 자동 갱신
- 로그아웃 시 Refresh Token 폐기
- 비밀번호 유효성 검사 (최소 8자, 영문+숫자+특수문자)
