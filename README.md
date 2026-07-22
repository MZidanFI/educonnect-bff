# EduConnect — Studi Kasus Microservices dengan BFF Pattern

Platform kursus online sederhana yang dibangun untuk mendemonstrasikan arsitektur
**Backend for Frontend (BFF)**, dilengkapi dengan **API Gateway**, **Service Discovery
(Eureka)**, **JWT Authentication**, dan **Swagger/OpenAPI Documentation**.

---

## Daftar Isi

- [Arsitektur](#arsitektur)
- [Daftar Service & Port](#daftar-service--port)
- [Prasyarat](#prasyarat)
- [Cara Menjalankan](#cara-menjalankan)
- [Dependency per Service](#dependency-per-service)
- [Dokumentasi API (Swagger)](#dokumentasi-api-swagger)
- [Alur Testing (Postman)](#alur-testing-postman)
- [Troubleshooting](#troubleshooting)

---

## Arsitektur

```
                          Website
                             │
                             ▼
                    <<EduConnect>>
                 ┌───────────────────┐
                 │   API Gateway       │  (8080)
                 └──────────┬──────────┘
                             │
                             ▼
                 ┌───────────────────┐
                 │   Website BFF        │  (8090)
                 └──────────┬──────────┘
                             │
              ┌──────────────┴──────────────┐
              ▼                              ▼
    «Internal Microservices»
   ┌───────────────┐            ┌───────────────┐
   │ Course Service   │            │ User Service     │
   │     (8082)        │            │     (8083)        │
   └───────────────┘            └───────────────┘

   Auth Service (8081) — login & JWT, diakses via Gateway
   Eureka Server (8079) — service discovery untuk semua service di atas
```

**Alur BFF:** Client memanggil `GET /web/dashboard/{userId}` ke BFF. BFF memanggil
`course-service` dan `user-service` secara paralel via **WebClient**, menggabungkan
hasilnya jadi satu response yang sudah diformat untuk kebutuhan frontend web.

---

## Daftar Service & Port

| Service | Port | Fungsi |
|---|---|---|
| **eureka-server** | 8079 | Service registry — semua service lain mendaftar ke sini |
| **api-gateway** | 8080 | Pintu masuk tunggal, routing ke service yang sesuai |
| **auth-service** | 8081 | Login, register, generate & validasi JWT |
| **course-service** | 8082 | CRUD kursus & data enrollment (progress belajar) |
| **user-service** | 8083 | CRUD data profil user |
| **website-bff** | 8090 | Menggabungkan data Course + User untuk dashboard web |

> **Catatan port:** Hindari port di bawah 1024 dan port yang masuk daftar
> "unsafe ports" browser (mis. 69, 87, 65).

---

## Prasyarat

- **Java 17+**
- **Maven** (atau gunakan `./mvnw` yang sudah include di tiap project)
- **MongoDB** — bisa lokal (`mongodb://localhost:27017`) atau MongoDB Atlas (cloud)
- **Postman** (untuk testing API)
- Semua service dibangun dengan **Spring Boot 4.1.0**

---

## Cara Menjalankan

### Urutan WAJIB diikuti (service bergantung satu sama lain):

1. **eureka-server**
   ```bash
   cd eureka-server
   ./mvnw spring-boot:run
   ```
   Cek: buka `http://localhost:8761` → harus muncul dashboard Eureka.

2. **course-service**
   ```bash
   cd course-service
   ./mvnw spring-boot:run
   ```

3. **user-service**
   ```bash
   cd user-service
   ./mvnw spring-boot:run
   ```

4. **auth-service**
   ```bash
   cd auth-service
   ./mvnw spring-boot:run
   ```

5. **website-bff**
   ```bash
   cd website-bff
   ./mvnw spring-boot:run
   ```

6. **api-gateway** (paling akhir)
   ```bash
   cd api-gateway
   ./mvnw spring-boot:run
   ```

### Verifikasi Semua Service Terdaftar

Buka `http://localhost:8079` — harus muncul 5 service (semua kecuali eureka-server
sendiri) dengan status **UP**:
- API-GATEWAY
- AUTH-SERVICE
- COURSE-SERVICE
- USER-SERVICE
- WEBSITE-BFF

---

## Dependency per Service

| Service | Dependency Utama |
|---|---|
| eureka-server | `spring-cloud-starter-netflix-eureka-server` |
| api-gateway | `spring-cloud-starter-gateway`, `spring-cloud-starter-netflix-eureka-client` |
| auth-service | `spring-boot-starter-webmvc`, `spring-boot-starter-data-mongodb`, `spring-boot-starter-security`, `jjwt-*`, `springdoc-openapi-starter-webmvc-ui` |
| course-service | sama seperti auth-service + `spring-cloud-starter-netflix-eureka-client` |
| user-service | sama seperti course-service |
| website-bff | `spring-boot-starter-webflux`, `spring-cloud-starter-loadbalancer`, `spring-cloud-starter-netflix-eureka-client`, `springdoc-openapi-starter-webflux-ui` |

**Versi kunci:**
- `springdoc-openapi` → **3.0.3** (versi 2.x tidak kompatibel dengan Spring Boot 4.x)
- `jjwt` → 0.11.5
- `spring-cloud-dependencies` (BOM) → 2025.1.2

---

## Dokumentasi API (Swagger)

Setiap service (kecuali eureka-server & api-gateway) menyediakan dokumentasi
interaktif otomatis:

| Service | Swagger UI |
|---|---|
| auth-service | `http://localhost:8081/swagger-ui.html` |
| course-service | `http://localhost:8082/swagger-ui.html` |
| user-service | `http://localhost:8083/swagger-ui.html` |
| website-bff | `http://localhost:8090/swagger-ui.html` |

**Cara pakai fitur Authorize (untuk endpoint yang butuh JWT):**
1. Login dulu lewat `POST /auth/login` (via Swagger auth-service atau Postman)
2. Copy `token` dari response
3. Di halaman Swagger service lain, klik tombol **Authorize**
4. Paste token (tanpa kata "Bearer", cukup tokennya saja) → **Authorize** → **Close**
5. Sekarang endpoint yang butuh token bisa langsung dicoba lewat **Try it out**

---

## Alur Testing (Postman)

Semua request dikirim lewat **API Gateway**: `http://localhost:8080`

| # | Request | Ekspektasi |
|---|---|---|
| 1 | `POST /internal/users` — buat data user | 200, simpan `id` sebagai `USER_ID` |
| 2 | `POST /internal/courses` — buat data course | 200, simpan `id` sebagai `COURSE_ID` |
| 3 | Insert manual collection `enrollments` di MongoDB (`userId`, `courseId`, `progress`) | - |
| 4 | `POST /auth/register` — daftar akun login (`userId`, `username`, `password`, `role`) | 200 |
| 5 | `POST /auth/login` (`username`, `password`) | 200, simpan `token` |
| 6 | `GET /internal/courses` tanpa header | 403 (ditolak, sesuai desain) |
| 7 | `GET /internal/courses` + `Authorization: Bearer <token>` | 200 |
| 8 | `GET /web/dashboard/{USER_ID}` + `Authorization: Bearer <token>` | 200, data gabungan Course + User |

---

## Troubleshooting

| Gejala | Kemungkinan Penyebab | Solusi |
|---|---|---|
| `ERR_UNSAFE_PORT` di browser | Port yang dipakai masuk daftar port terlarang browser (mis. 69, 87) | Ganti ke port di atas 8000 |
| `403 Forbidden` di endpoint POST | `SecurityConfig` belum permitAll untuk POST, atau service belum di-restart | Cek `SecurityConfig.java`, restart service |
| `401 Unauthorized` di BFF walau sudah login | BFF tidak meneruskan header `Authorization` ke service lain | Pastikan `.header(HttpHeaders.AUTHORIZATION, token)` ada di `ServiceDashboard` |
| `503 Service Unavailable` dari BFF | Dependency `spring-cloud-starter-loadbalancer` belum ditambahkan | Tambahkan dependency tsb di `website-bff` |
| `Connection refused: getsockopt` ke Eureka | `eureka-server` belum jalan / belum siap saat service lain start | Pastikan eureka-server jalan duluan, tunggu dashboard muncul sebelum start service lain |
| Import `io.swagger.*` tidak resolve | Versi springdoc tidak kompatibel dengan Spring Boot 4.x | Gunakan `springdoc-openapi` versi **3.0.3**, bukan 2.x |
| Swagger UI 403/401 | Endpoint `/swagger-ui/**` belum di-permitAll (MVC) atau belum dikecualikan dari filter JWT (WebFlux/BFF) | Tambahkan pengecualian path swagger di `SecurityConfig`/`JwtAuthWebFilter` |
| Semua dependency di `pom.xml` tidak ke-load | Tag `</dependencies>` tidak menutup dengan benar sebelum `<build>` | Cek struktur XML, pastikan setiap tag pembuka punya penutup |

---

## Struktur Folder

```
educonnect/
│
├── eureka-server/       (8079)
├── api-gateway/          (8080)
├── auth-service/          (8081)
├── course-service/        (8082)
├── user-service/           (8083)
└── website-bff/             (8090)
```

Setiap folder adalah project Spring Boot independen dengan struktur:
```
service-name/
├── src/main/java/com/edu/service_name/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── model/
│   ├── dto/
│   ├── config/       (SecurityConfig, OpenApiConfig, dll)
│   └── util/          (JwtUtil, dll)
├── src/main/resources/
│   └── application.yml
└── pom.xml
```

---

## Catatan Pengembangan Lanjutan

Beberapa hal yang bisa ditingkatkan untuk kebutuhan produksi (di luar cakupan
tugas ini):
- Password di `auth-service` masih plain text — perlu hash dengan `BCryptPasswordEncoder`
- Belum ada endpoint `POST` khusus untuk `enrollments` — masih insert manual via MongoDB
- Belum ada validasi input (field kosong masih bisa tersimpan sebagai `null`)
- Secret JWT sebaiknya disimpan di environment variable, bukan hardcode di `application.yml`
