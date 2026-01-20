# 🛒 Wearly (Play Mate)

---

## 1. 프로젝트 개요
**Wearly**는 다양한 패션 아이템 큐레이션과 정기 멤버십 혜택을 제공하며, 사용자 간의 매칭 및 판매자 정산 시스템을 갖춘 커머스 커뮤니티 플랫폼입니다.

---

## 2. 기술 스택

| 구분         | 기술 스택 | 비고 |
|:-----------| :--- | :--- |
| **백엔드**    | Spring Boot 3.x | Java 기반 비즈니스 로직 및 API |
| **프론트엔드**  | React (Vite) | TypeScript 기반 UI/UX |
| **데이터베이스** | MySQL 8.0 | 데이터 영속성 관리 |
| **프록시**    | Nginx | 리버스 프록시 및 정적 파일 서빙 |
| **컨테이너**   | Docker, Docker Compose | 환경 격리 및 배포 자동화 |
| **배포**     | AWS EC2 | 클라우드 인프라 |
| **스토리지**   | AWS S3 | 이미지 저장소 |
| **실시간 통신** | WebSocket, SSE | 실시간 알림 및 상태 업데이트 |
| **보안**     | Spring Security, JWT | 사용자 인증 및 권한(RBAC) 관리 |

---

## 📄 API 명세
[03-api-spec.md](docs/03-api-spec.md)

---

## 3. 디렉토리 구조

프로젝트 루트 디렉토리(`applied-project`)를 기준으로 한 구조입니다.

```text
applied-project
├─ .github/   
│  └─ workflows/                # GitHub Action CI/CD
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

---
# 로컬 개발 환경 실행 가이드

이 프로젝트는 Docker를 사용하여 로컬 환경을 빠르고 일관되게 구축할 수 있도록 구성되었습니다. 각 파트별 또는 전체 시스템 실행 방법은 다음과 같습니다.

## 1. 백엔드 개발 환경 실행
백엔드 서버와 데이터베이스를 실행

```bash
# 백엔드 디렉토리로 이동
cd wearly-backend/wearly

# 프로젝트 빌드 및 컨테이너 실행
docker compose up --build
```

## 1. 프론트엔드 개발 환경 실행
```bash
# 프론트엔드 디렉토리로 이동
cd wearly-frontend-v1

# 도커 이미지 빌드
docker build -t wearly-frontend:dev .

# 컨테이너 실행 (로컬 70번 포트 연결)
docker run --rm -p 70:70 wearly-frontend:dev
```

## 2. 전체 시스템 통합 실행
```bash
# 프로젝트 루트(applied-project) 디렉토리에서 실행
docker compose up --build
```
### 접근 주소 : http://localhost:70/