# 🛒 Wearly (Play Mate)

---

## 1. 프로젝트 개요
**Wearly**는 다양한 패션 아이템 큐레이션과 정기 멤버십 혜택을 제공하며, 사용자 간의 매칭 및 판매자 정산 시스템을 갖춘 커머스 커뮤니티 플랫폼입니다.

---

## 2. 기술 스택

| 구분 | 기술 스택 | 비고 |
| :--- | :--- | :--- |
| **백엔드** | Spring Boot 3.x | Java 기반 비즈니스 로직 및 API |
| **프론트엔드** | React (Vite) | TypeScript 기반 UI/UX |
| **데이터베이스** | MySQL 8.0 | 데이터 영속성 관리 |
| **프록시** | Nginx | 리버스 프록시 및 정적 파일 서빙 |
| **컨테이너** | Docker, Docker Compose | 환경 격리 및 배포 자동화 |
| **배포** | AWS EC2 | 클라우드 인프라 |
| **실시간 통신** | WebSocket, SSE | 실시간 알림 및 상태 업데이트 |
| **보안** | Spring Security, JWT | 사용자 인증 및 권한(RBAC) 관리 |

---

## 📄 API 명세
[03-api-spec.md](docs/03-api-spec.md)

---

## 3. 디렉토리 구조

프로젝트 루트 디렉토리(`applied-project`)를 기준으로 한 구조입니다.

```text
applied-project
├─ docs/                        # 프로젝트 설계 및 API 상세 문서
│  ├─ 01-project-design.md
│  ├─ 02-dependencies.md
│  ├─ 03-api-spec.md
│  └─ README.md
├─ nginx/                       # Nginx 설정 파일
├─ wearly-backend/              # Spring Boot 백엔드 소스 코드
│  └─ wearly/
│     ├─ src/
│     ├─ build.gradle
│     └─ Dockerfile
├─ wearly-frontend-v1/          # React 프론트엔드 소스 코드
│  ├─ src/
│  │  ├─ api/                   # HTTP 클라이언트 로직
│  │  ├─ components/            # 공통 및 도메인 컴포넌트
│  │  ├─ pages/                 # SettlementPage 등 화면 컴포넌트
│  │  └─ App.tsx                # 라우팅 설정
│  ├─ index.html
│  └─ Dockerfile
├─ .env                         # 환경 변수 관리
└─ docker-compose.yml           # 시스템 통합 실행 설정