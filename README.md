# 🖥️ Paperless_Admin_Project — 전자 민원 관리 시스템

**Paperless_Admin_Project**는  
무서류(페이퍼리스) 전자 민원 서비스의 **내부 관리자 전용 시스템(백오피스)** 입니다.  

관리자는 이 시스템을 통해 민원(신문고)을 확인하고 처리하며,  
공지사항 등록 및 전자문서(Paperless) 관리도 수행할 수 있습니다.

> “민원 현황을 한눈에, 처리 과정을 투명하게”
<img width="1800" height="500" alt="image" src="https://github.com/user-attachments/assets/f090c8b3-133e-447e-ae92-240b6bb23370" />


---

## 🌟 주요 기능

### 📊 관리자 대시보드
- 전체 민원 건수, 처리 현황, 최근 등록 리스트 표시  
- 공지사항/민원/전자문서 데이터 통합 현황  
- 역할별 접근 메뉴 구분 (Admin / Manager / Director)

> 구현: **황금성**

---

### 📮 민원(신문고) 관리
- 접수된 민원 목록 / 상세 열람  
- 상태 변경 (`접수`, `처리중`, `완료`, `반려`)  
- 담당자 지정 및 답변 작성  
- 반려 사유 등록 및 처리 이력 자동 기록  

> 구현: **황금성**

---

### 📢 공지사항 관리
- 관리자 전용 공지 CRUD (등록, 수정, 삭제)  
- 등록일 / 작성자 표시  
- 공개 여부 제어 및 정렬 기능  

> 구현: **황금성**

---

### 📄 전자문서 (Paperless)
- 사용자로부터 제출된 전자 신청서 관리  
- 문서 상태(`대기`, `처리중`, `완료`) 변경 가능  
- 첨부파일 확인 및 다운로드 (MinIO 연동)  

> 구현: **최원창**

---

## 🧱 기술 스택

| 구분 | 기술 |
|------|------|
| **Backend** | Spring Boot 3.x / Spring MVC / Spring Security 6 / Spring Data JPA |
| **Database** | Oracle 19c |
| **Template** | Thymeleaf |
| **Storage** | MinIO (파일 업로드, 다운로드, 미리보기) |
| **Validation & Utils** | Jakarta Validation / Lombok |
| **Build Tool** | Maven |
| **Language** | Java 17+ |

---

## ⚙️ 환경 설정 예시

```properties
# ⚙️ Application Settings
server.port=9040
spring.application.name=Paperless_Admin_Project

# 🗄️ Oracle Database
spring.datasource.url=jdbc:oracle:thin:@<HOST>:1521:<SID>
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver
spring.datasource.username=<USERNAME>
spring.datasource.password=<PASSWORD>

# 🧩 JPA Settings
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# 📎 File Upload Limits
spring.servlet.multipart.max-file-size=100MB
spring.servlet.multipart.max-request-size=100MB

# ☁️ MinIO Storage
storage.minio.endpoint=http://<MINIO_HOST>:9000
storage.minio.access-key=<ACCESS_KEY>
storage.minio.secret-key=<SECRET_KEY>
storage.minio.bucket=attachments
storage.minio.secure=false
```

### 🗂️ 프로젝트 구조
```text
Paperless_Admin_Project/
├─ controller/
│  ├─ get/           # GET 요청 처리
│  ├─ post/          # POST 요청 처리
│  ├─ rest/          # 비동기/REST API
│
├─ entity/           # JPA 엔티티
├─ dto/              # DTO 클래스
├─ repository/       # JpaRepository 인터페이스
├─ service/          # 비즈니스 로직
├─ security/         # 인증/인가 관련 설정
├─ resources/
│  ├─ templates/     # Thymeleaf 템플릿 (공지, 민원, 문서 등)
│  ├─ static/        # CSS, JS, 이미지
│  └─ application.properties
└─ pom.xml
```

## 🔐 보안

- Spring Security 6 기반 로그인 및 접근 제어
- 직급(Role)에 따른 메뉴 접근 제한
- 로그인 성공/실패 시 리다이렉트 처리
- CSRF 토큰 자동 주입 (Thymeleaf form)

## 🚀 향후 개선 예정

- 대시보드 통계 시각화 (Chart.js / Recharts)
- 처리 상태별 그래프 추가
- 알림 기능 (신규 민원 발생 시 팝업)
- 첨부파일 미리보기 개선

## 👥 기여자 (Contributors)

| 이름 | 담당 기능 |
|------|------------|
| **황금성** | 관리자 페이지 전반 구현 (대시보드, 공지사항, 신문고, 보안 구조 등) |
| **최원창** | 전자문서(Paperless) 모듈 및 첨부파일 연동 |

## 📜 라이선스
이 프로젝트는 Apache License 2.0하에 배포됩니다.
```pgsql

Copyright 2025 Paperless
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0
```
<h3 align="center">✨ Paperless_Admin_Project ✨</h3> <p align="center"> 내부 직원이 민원을 관리하고 처리 현황을 통합적으로 확인할 수 있는<br/> <b>전자 민원 백오피스 시스템</b> </p>
