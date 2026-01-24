# Dango

> **소상공인을 위한 스탬프 적립 통합 서비스 – 당고(Dango)**
> 
> 소상공인들의 카페·매장의 **스탬프/쿠폰 적립**, 고객의 **매장 조회·이벤트 참여**,
> 
> 점주의 **매장 관리·스탬프 설정 관리**까지 오프라인 매장을 위한 모든 고객 리워드 기능을 **하나의 서비스로 통합**한 서비스입니다.
> 
> https://daango.site 
> 

---

## 1. Tech Stack

| Category | Stack |
| --- | --- |
| **Language** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **ORM / DB** | Spring Data JPA, MySQL |
| **Auth / Security** | Spring Security, JWT |
| **Infra / Storage** | NCP cloud   |
| **Mail Service** | JavaMailSender, MimeMessageHelper |
| **Build / Deploy** | Gradle, Docker, GitHub Actions |
| **Etc** | Lombok, Validation, Scheduler |

## 2. Architecture

```
Client (React)
      ↓
Nginx (HTTPS)
      ↓
Spring Boot (API Server)
      ↓
MySQL / S3 / Mail Service

```

---

## 3. 주요 기능

| 사용자 기능 | 점주 기능 |
| --- | --- |
| **스탬프 적립 / 조회**: 매장 스탬프 적립, 보유 스탬프·이력 확인 | **스탬프 관리**: 설정값 등록·수정, 스탬프 적립 처리(주문 기반) |
| **쿠폰 사용 / 조회**: 쿠폰 발급, 사용 인증(점주 코드 기반) | **쿠폰 관리**: 쿠폰 사용 승인(매장별 인증 코드), 사용 내역 확인 |
| **매장 조회 / 검색**: 전체 매장 조회, 상세 보기, 주변 매장 탐색 | **매장 관리**: 매장 프로필·운영시간 수정, 공유 링크 생성 |
| **이벤트 조회**: 진행 중 이벤트 확인(EventBoard) | **이벤트 신청 / 조회**: 점주 이벤트 신청, 진행·지난 이벤트 조회 |
| **즐겨찾기 관리**: 매장 즐겨찾기 등록·삭제·목록 조회 | **통계 / 대시보드**: 주간 통계, 고객 수, 레벨·성별 통계 조회 |

| 인증 기능 | AI 기능 |
| --- | --- |
| **회원가입 / 로그인**: 유저·점주 가입 및 로그인 | **추천**: AI 기반 매장·이벤트 추천 |
| **온보딩**: 유저 성별·주소·좌표 등록 | **태그 생성**: 자동 해시태그 생성 |
| **JWT 기반 인증체계** | |

---

## 4. 상세 기능 명세

### 4.1 스탬프 즐겨찾기 관련 기능

- 즐겨찾기 리스트 조회 → `GET /api/v1/stamps`
- 즐겨찾기 설정 → `POST /api/v1/stamps/{stampId}/favorite`
- 즐겨찾기 취소 → `DELETE /api/v1/stamps/{stampId}/favorite`

---

### 4.2 Stamp 등록·적립·삭제 관련 기능

- 스탬프 등록 → `POST /api/v1/stamps`
- 스탬프 적립 → `POST /api/v1/stamps/add`
- 스탬프 개별 조회 → `GET /api/v1/stamps/{stampId}`
- 스탬프 삭제 → `DELETE /api/v1/stamps/{stampId}`

---

### 4.3 QRCode 관련 기능

- QR 스캔 → `POST /api/v1/qr/scan`
- QR 생성 → `GET /api/v1/qr/generate`

---

### 4.4 Store 매장 관련 기능

- 매장 전체 조회 → `GET /api/v1/stores`
- 가게 상세 홈 조회 → `GET /api/v1/stores/{storeId}`
- 매장 공유 링크 생성 → `GET /api/v1/stores/{storeId}/share`
- 가게 상세 리뷰 탭 조회 → `GET /api/v1/stores/{storeId}/reviews`
- 매장 검색 → `GET /api/v1/stores/search`

---

### 4.5 Coupon 관련 기능

- 쿠폰 사용 확인 → `POST /api/v1/coupons/{couponId}/confirm`
- 쿠폰 개별 조회 -> GET /api/v1/coupons/{couponId}

---

### 4.6 점주 계정 정보 관련 기능

- 점주 계정 정보 조회 → `GET /api/v1/managers/account`

---

### 4.7 Store Map (매장 지도 및 검색) 기능

- 매장 검색 및 거리순 정렬 → `GET /api/v1/stores/search-distance`
- 내 주변 가까운 매장 조회 → `GET /api/v1/stores/nearby`

---

### 4.8 EventStore (이벤트 · 신청 매장 조회) 관련 기능

- 진행중인 이벤트 개별 조회(점주/유저 공통) → `GET /api/v1/events/eventstores/ongoing/{eventType}`
- 지난 이벤트 목록 조회 → `GET /api/v1/events/eventstores/ended`

---

### 4.9 AI 관련 기능

- AI 호출 → `POST /api/v1/ai/call`

---

### 4.10 EventBoard (유저 : 이벤트 조회) 관련 기능

- 스탬프 페이지 내 진행중 이벤트 리스트 조회 → `GET /api/v1/events/board`

---

### 4.11 Mypage (유저 프로필 · 계정 정보 관련 기능)

- 유저 설정 수정 → `PATCH /api/v1/mypage`
- 유저 설정 조회 → `GET /api/v1/mypage/settings`
- 유저 프로필 조회 → `GET /api/v1/mypage/profile`
- 유저 계정 정보 조회 → `GET /api/v1/mypage/account`

---

### 4.12 Review(리뷰 관련) 기능

- 리뷰 작성 → `POST /api/v1/reviews`
- 리뷰어 프로필 조회 → `GET /api/v1/reviews/profile/{userId}`

---

### 4.13 FavStore(즐겨찾는 매장) 기능

- 매장 즐겨찾기 등록 → `POST /api/v1/favstores/{storeId}`
- 매장 즐겨찾기 취소 → `DELETE /api/v1/favstores/{storeId}`
- 매장 즐겨찾기 리스트 조회 → `GET /api/v1/favstores`

---

### 4.14 Reward(리워드/랭킹 대시보드) 기능

- 리워드 대시보드 조회 → `GET /api/v1/rewards/dashboard`

---

### 4.15 User(유저 정보 관련) 기능

- 유저의 동네 매장 리스트 조회 → `GET /api/v1/users/stores/local`
- 유저의 현재 스탬프 목록 조회 → `GET /api/v1/users/stamps`
- 내 스탬프 히스토리 조회 → `GET /api/v1/users/stamps/history`
- 내 쿠폰 조회 → `GET /api/v1/users/coupons`

---

### 4.16 Event(점주 · 이벤트 신청 관련) 기능

- 현재 열려있는 이벤트 신청 → `POST /api/v1/events/{eventType}/apply`
- 신청 가능한 이벤트 카테고리 조회 → `GET /api/v1/events/categories`

---

### 4.17 점주 페이지 기능

- 점주 설정 조회 → `GET /api/v1/stamps/manager/settings`
- 점주 설정 수정 → `POST /api/v1/stamps/manager/settings`
- 스탬프 개수 증가(수동 적립) → `POST /api/v1/stamps/manager/addByNum`
- 주간 이벤트 통계 조회 → `GET /api/v1/stamps/manager/weekly/event`
- 주간 고객 통계 조회 → `GET /api/v1/stamps/manager/weekly/customers`
- 전체 통계 조회 → `GET /api/v1/stamps/manager/totals`
- 전체 지표 조회 → `GET /api/v1/stamps/manager/statics`
- 성별 기반 주간 통계 → `GET /api/v1/stamps/manager/gender/weekly`
- 점주 고객 리스트 조회 → `GET /api/v1/stamps/manager/customers`

---

### 4.18 Auth 관련 기능

#### 4.18.1 유저 인증 · 계정

- 유저 온보딩 → `POST /api/v1/auth/user/onboarding`
- 유저 회원가입 → `POST /api/v1/auth/user/join`
- 유저 비밀번호 찾기 → `POST /api/v1/auth/user/findPassword`
- 유저 아이디 찾기 → `POST /api/v1/auth/user/findId`
- 토큰 재발급 → `POST /api/v1/auth/token`

---

#### 4.18.2 점주 인증 · 계정

- 점주 온보딩 → `POST /api/v1/auth/manager/onboarding`
- 점주 회원가입 → `POST /api/v1/auth/manager/join`
- 점주 비밀번호 찾기 → `POST /api/v1/auth/manager/findPassword`
- 점주 아이디 찾기 → `POST /api/v1/auth/manager/findId`
- 비밀번호 재설정 → `PATCH /api/v1/auth/resetPassword`
- 카카오 로그인 페이지 조회 → `GET /api/v1/auth/kakao`

---

#### 4.18.3 공통 인증

- 로그아웃 → `POST /api/v1/auth/logout`
- 로그인 페이지 조회 → `GET /api/v1/auth/login`
- 로그인 → `POST /api/v1/auth/login`

---

#### 4.18.4 이메일 인증

- 이메일 인증번호 검증 → `POST /api/v1/auth/email/verify`
- 이메일 인증번호 발송 → `POST /api/v1/auth/email/send`

---

### 4.19 Manager(매장 프로필) 관련 기능

- 매장 프로필 조회 → `GET /api/v1/managers/profile`
- 매장 프로필 수정 → `PATCH /api/v1/managers/profile`

---

## 5. CI/CD

GitHub Actions → **NCP 서버(Docker Compose)** 자동 배포

main 브랜치 push 시 **빌드 → 서버 접속 → Docker Compose 재배포**까지 전 과정이 자동화

### 파이프라인 개요

```
Developer (push main)
        └── GitHub Actions
             ├─ Gradle build (prod 프로파일)
             ├─ NCP Container Registry 로그인
             ├─ Spring 이미지 빌드/푸시
             ├─ Nginx 이미지 빌드/푸시
             ├─ docker-compose-prod.yml 서버로 전송
             └── NCP 서버 SSH 접속 → docker login / pull / up -d

```


## 6. Contributors

| 이름 | 역할 | 담당 기능 |
| --- | --- | --- |
| [@Mymyseoyoung](https://github.com/Mymyseoyoung) | 백엔드 | **스탬프 시스템 전반**(스탬프 즐겨찾기, 등록·적립 로직), **매장 기능**(조회·검색·공유), **쿠폰 기능**(사용 처리·코드 검증), **이벤트 조회**(신청 매장 조회, 유저/점주 이벤트 조회), 즐겨찾는 매장, 유저 정보 및 히스토리 조회, Swagger 설정 |
| [@alissa159](https://github.com/alissa159) | 백엔드 | **인증(Auth)**(회원가입, 이메일 인증, JWT/Security), **사업자등록번호 인증**, **마이페이지** 기능 구현, 지도 페이지 API, 리워드 페이지 API |
| [@rossenzii](https://github.com/rossenzii) | 백엔드 | **CI/CD 설정 및 서버 배포**, **S3 이미지 업로드 설정**, **QR 생성 및 검증 로직**, 점주 페이지 통계 API, **AI 모델 생성·추천 기능 구현**, **카카오 로그인 구현** |

---
