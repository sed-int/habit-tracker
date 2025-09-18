# Habit Tracker

Spring Boot 기반의 습관 추적 API 백엔드

## 기술 스택

- **Java 17** + **Spring Boot 3.x**
- **Spring Security** + **OAuth2** + **JWT**
- **Spring Data JPA** + **MySQL 8.x**
- **Redis** (토큰 관리)
- **Gradle** 빌드 시스템

## 주요 기능

- **사용자 인증**: OAuth2 (Google, Kakao, Naver) + JWT
- **습관 관리**: CRUD, 태그, 즐겨찾기, 상태 관리
- **완료 기록**: 일별 체크, 메모 추가
- **API 문서**: OpenAPI 3 + Swagger UI
- **모니터링**: Spring Actuator + Prometheus

## 프로젝트 구조
```
src/main/java/com/example/demo_habit/
├── controller/ # REST API 컨트롤러
├── service/ # 비즈니스 로직
├── domain/ # 엔티티 모델
├── repository/ # 데이터 액세스
├── dto/ # 요청/응답 객체
├── config/ # 설정 클래스
└── common/ # 공통 유틸리티
```

## 데이터베이스 스키마

```sql
users           # 사용자 정보
habits          # 습관 (이름, 태그, 상태, 주기)
habit_completions # 완료 기록 (날짜별)
user_identity   # OAuth 연동 정보
```

## API 엔드포인트

**Base URL**: `/api/v1`

### 습관 관리
- `GET /habits` - 습관 목록 조회
- `POST /habits` - 습관 생성
- `PUT /habits/{id}` - 습관 수정
- `DELETE /habits/{id}` - 습관 삭제

### 완료 기록
- `GET /habits/{id}/completions` - 완료 기록 조회
- `POST /habits/{id}/completions` - 완료 기록 추가

### 인증
- `POST /auth/oauth2/callback/*` - OAuth2 콜백
- `POST /auth/token/refresh` - 토큰 갱신
- `POST /auth/logout` - 로그아웃


