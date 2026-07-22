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

>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
# Pembagian Tugas Kelompok — EduConnect (5 Anggota)

Pembagian ini dibuat supaya setiap anggota punya tanggung jawab yang jelas,
proporsional, dan saling terhubung — sehingga tiap orang paham alur sistem
secara keseluruhan, bukan cuma bagian sendiri.

---

## Prinsip Pembagian

1. **Tiap orang pegang minimal 1 service utuh** (dari model sampai controller),
   supaya masing-masing benar-benar paham siklus penuh Spring Boot.
2. **Topik utama (BFF) dan fitur pendukung (Gateway, JWT) dipisah ke orang
   yang berbeda**, supaya beban kerja merata dan tidak ada 1 orang yang
   mengerjakan bagian paling kompleks sendirian.
3. **Semua anggota tetap wajib paham keseluruhan alur**, karena saat sidang/
   presentasi, dosen bisa tanya ke siapa saja soal bagian manapun.

---

## Pembagian per Anggota

### 👤 Anggota 1 — Course Service + Enrollment
**Tanggung jawab:**
- Setup project `course-service` dari Spring Initializr
- Model `Course` & `Enrollment`, Repository, Service, Controller
- Endpoint: `GET/POST /internal/courses`, `GET /internal/courses/enrolled`
- Koneksi ke MongoDB (`course_db`)
- Swagger documentation untuk service ini

**Yang harus dipahami untuk presentasi:**
- Bagaimana data Course & Enrollment saling berelasi
- Kenapa dipisah jadi 2 collection, bukan digabung
- Cara kerja `MongoRepository` dan query method (`findByUserId`)

---

### 👤 Anggota 2 — User Service
**Tanggung jawab:**
- Setup project `user-service` dari Spring Initializr
- Model `User`, Repository, Service, Controller
- Endpoint: `GET/POST /internal/users`, `GET /internal/users/{id}/profile`
- Koneksi ke MongoDB (`user_db`)
- Swagger documentation untuk service ini

**Yang harus dipahami untuk presentasi:**
- Struktur data profil user
- Kenapa User Service terpisah dari Auth Service (pemisahan concern: profil vs kredensial login)
- Cara kerja endpoint `GET /{id}/profile`

---

### 👤 Anggota 3 — Auth Service (JWT)
**Tanggung jawab:**
- Setup project `auth-service` dari Spring Initializr
- Model `Account`, Repository, Service, Controller
- `JwtUtil` (generate & validasi token)
- Endpoint: `POST /auth/register`, `POST /auth/login`
- `SecurityConfig` untuk auth-service
- Swagger documentation untuk service ini
- **Distribusikan `JwtUtil` dan secret yang sama ke Anggota 1, 2, dan 4**
  (karena course-service, user-service, dan website-bff butuh validasi
  token yang konsisten)

**Yang harus dipahami untuk presentasi:**
- Apa itu JWT, struktur token (header-payload-signature)
- Alur login: username/password → validasi → generate token
- Kenapa secret harus sama di semua service
- Perbedaan Authentication vs Authorization

---

### 👤 Anggota 4 — Website BFF (Topik Utama)
**Tanggung jawab:**
- Setup project `website-bff` dari Spring Initializr (WebFlux)
- `ConfigWebClient` (`@LoadBalanced`)
- DTO gabungan (`DTODashboard`, `DTOEnrolledCourse`, `DTOUserProfile`)
- `ServiceDashboard` — logic `Mono.zip` menggabungkan 2 service
- `ControllerDashboard` — endpoint `/web/dashboard/{userId}`
- `JwtAuthWebFilter` — proteksi token di level BFF
- Swagger documentation untuk service ini

**Yang harus dipahami untuk presentasi (paling penting, karena ini topik utama):**
- **Apa itu BFF dan kenapa dipakai** — masalah apa yang diselesaikan
- Kenapa pakai `WebClient` + `Mono.zip`, bukan panggil satu-satu berurutan
- Bagaimana transformasi data terjadi (data mentah dari 2 service → 1 response gabungan)
- Kenapa endpoint BFF (`/web/dashboard/{userId}`) berbeda dari endpoint backend asli
- Bagaimana token diteruskan dari BFF ke service lain

---

### 👤 Anggota 5 — Infrastruktur: Eureka + API Gateway
**Tanggung jawab:**
- Setup project `eureka-server`
- Setup project `api-gateway` (routing ke semua service)
- Konfigurasi `eureka.client.service-url.defaultZone` di **semua** service
  (koordinasi dengan Anggota 1-4 supaya semua konsisten)
- Testing akhir: pastikan semua service ter-register di dashboard Eureka
- Menyusun dan menjalankan **seluruh alur testing Postman** end-to-end
  (karena posisinya paling pas untuk lihat semua service saling terhubung)

**Yang harus dipahami untuk presentasi:**
- Apa itu Service Discovery dan kenapa dibutuhkan (dibanding hardcode alamat)
- Cara kerja `lb://nama-service` di Gateway
- Bagaimana Gateway menentukan routing berdasarkan `Path predicate`
- Kenapa API Gateway ini dihitung sebagai salah satu "fitur pendukung"

---

## Timeline Kerja yang Disarankan

| Tahap | Kegiatan | Siapa |
|---|---|---|
| 1 | Setup awal: semua bikin project masing-masing, test jalan sendiri-sendiri (belum terhubung) | Semua (paralel) |
| 2 | Anggota 5 setup Eureka & Gateway, minta semua daftar service-name yang konsisten | Anggota 5 → koordinasi ke semua |
| 3 | Anggota 1 & 2 selesaikan Course & User Service, test manual pakai Postman langsung ke port masing-masing | Anggota 1, 2 |
| 4 | Anggota 3 selesaikan Auth Service, bagikan `JwtUtil` + secret ke semua | Anggota 3 → distribusi ke 1, 2, 4 |
| 5 | Semua terapkan JWT filter di service masing-masing (`SecurityConfig`) | Anggota 1, 2, 4 |
| 6 | Anggota 4 bangun BFF setelah Course & User Service sudah stabil | Anggota 4 |
| 7 | Integrasi semua lewat Gateway, testing end-to-end bareng-bareng | Semua, dikoordinir Anggota 5 |
| 8 | Tambah Swagger di semua service | Masing-masing di service sendiri |
| 9 | Susun laporan & siapkan presentasi | Semua |

---

## Pembagian Laporan/Dokumentasi

Supaya adil, laporan juga dibagi sesuai bagian masing-masing:

| Bagian Laporan | Penanggung Jawab |
|---|---|
| Pendahuluan & latar belakang studi kasus | Anggota 5 (paling paham gambaran besar) |
| Penjelasan arsitektur BFF + diagram | Anggota 4 |
| Course Service & Enrollment | Anggota 1 |
| User Service | Anggota 2 |
| Auth Service & JWT | Anggota 3 |
| API Gateway & Eureka | Anggota 5 |
| Hasil testing (screenshot Postman/Swagger) | Semua (masing-masing service sendiri) |
| Kesimpulan & kendala | Semua (diskusi bareng, ditulis salah satu) |

---

## Tips Supaya Kerja Kelompok Lancar

1. **Sepakati dulu nama package & konvensi penamaan** sebelum mulai coding
   (misal semua pakai `com.edu.{nama-service}`, gaya `ServiceCourse` bukan
   `CourseService`, dst) — supaya tidak keteteran waktu integrasi.
2. **Sepakati JWT secret dari awal**, taruh di grup chat, supaya semua
   service pakai secret yang sama persis.
3. **Push kode secara berkala** (kalau pakai Git) supaya progress terlihat
   dan mudah digabung.
4. **Jadwalkan sesi integrasi bareng** (semua nyalakan laptop, jalankan
   service masing-masing sekaligus) — ini fase paling penting karena di
   sinilah biasanya ketahuan port bentrok, secret beda, atau path salah.
5. **Setiap orang wajib bisa jelaskan bagian orang lain secara garis besar**
   — minimal paham alur, walau detail implementasi beda orang.