# Smart Laundry

Smart Laundry adalah aplikasi manajemen operasional laundry berbasis Spring Boot. Aplikasi ini menyediakan autentikasi role `ADMIN` dan `STAFF`, dashboard operasional, manajemen staff, manajemen member/pelanggan, migrasi database, dan seeder data awal.

## Features

- Login dengan Spring Security.
- Role access untuk `ADMIN` dan `STAFF`.
- Manajemen staff untuk admin.
- Manajemen member/pelanggan untuk admin dan staff.
- Migrasi database dengan Flyway.
- Seeder data awal untuk akun admin, akun staff, layanan, dan inventaris.
- Styling UI dengan Tailwind CSS.

## Requirements

- Java JDK 21 atau lebih baru.
- MySQL server.
- Node.js dan npm.
- Git.
- Terminal:
  - Windows: PowerShell atau Command Prompt.
  - macOS: Terminal/zsh.

## Dependencies

Backend:

- Spring Boot 4.0.6
- Spring Data JPA
- Spring Security
- Spring MVC
- Thymeleaf
- Flyway + Flyway MySQL
- MySQL Connector/J
- Bean Validation
- Lombok

Frontend:

- Tailwind CSS 4
- `@tailwindcss/cli`

## Clone Project

Ganti URL repo sesuai repository GitHub final.

```bash
git clone https://github.com/bagasalfanto/java-smart-laundry.git
cd java-smart-laundry
```

## Environment Setup

Buat file `.env` dari `.env.example`.

Windows:

```powershell
Copy-Item .env.example .env
```

macOS:

```bash
cp .env.example .env
```

Isi `.env` default:

```properties
APP_NAME=Smart Laundry
APP_ENV=dev
APP_PORT=8080

DB_HOST=localhost
DB_PORT=3306
DB_NAME=smart_laundry
DB_USERNAME=root
DB_PASSWORD=
DB_TIMEZONE=Asia/Jakarta

APP_SEED=true
SEED_ADMIN_USERNAME=admin
SEED_ADMIN_PASSWORD=admin123
SEED_STAFF_USERNAME=staff
SEED_STAFF_PASSWORD=staff123
SEED_DEMO_DATA=false
```

Sesuaikan `DB_USERNAME` dan `DB_PASSWORD` dengan konfigurasi MySQL lokal masing-masing.

## Install Frontend Dependencies

```bash
npm install
```

Build CSS:

```bash
npm run css:build
```

Mode watch saat development:

```bash
npm run css:watch
```

## Database Migration and Seeder

Pastikan MySQL sudah berjalan sebelum migrasi.

Windows:

```powershell
.\mvnw.cmd migrate
.\mvnw.cmd db:seed
```

Atau langsung migrasi + seeder:

```powershell
.\mvnw.cmd migrate:seed
```

macOS:

```bash
chmod +x ./mvnw
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.command=migrate
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.command=db:seed
```

Atau langsung migrasi + seeder:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.command=migrate:seed
```

## Run the Application

Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

macOS:

```bash
./mvnw spring-boot:run
```

Buka aplikasi:

```text
http://localhost:8080
```

Jika port `8080` sudah dipakai, ubah `APP_PORT` di `.env`.

## Default Accounts

Jika seeder sudah dijalankan, akun default mengikuti `.env`.

Admin:

```text
Username: admin
Password: admin123
```

Staff:

```text
Username: staff
Password: staff123
```

Ganti password default di `.env` sebelum dipakai di environment yang dibagikan.

## Useful Commands

Windows:

```powershell
.\mvnw.cmd migrate
.\mvnw.cmd db:seed
.\mvnw.cmd migrate:seed
.\mvnw.cmd -DskipTests package
.\mvnw.cmd spring-boot:run
```

macOS:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.command=migrate
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.command=db:seed
./mvnw spring-boot:run -Dspring-boot.run.arguments=--app.command=migrate:seed
./mvnw -DskipTests package
./mvnw spring-boot:run
```

Frontend:

```bash
npm install
npm run css:build
npm run css:watch
```

## Folder Structure

```text
smartlaundry/
|-- src/main/java/com/laundry/smartlaundry/
|   |-- app/
|   |   |-- controllers/      # Controller web berdasarkan fitur/role
|   |   |-- dto/              # Form object dan DTO request/view
|   |   |-- enums/            # Enum domain seperti role dan status
|   |   |-- models/           # Entity JPA
|   |   |-- repositories/     # Spring Data JPA repositories
|   |   |-- security/         # Auth user details, login attempt, listener auth
|   |   `-- services/         # Business logic aplikasi
|   |-- config/               # Konfigurasi Spring Security dan aplikasi
|   |-- console/              # Command runner untuk migrate/seed
|   `-- database/seeders/     # Seeder data awal
|-- src/main/resources/
|   |-- db/migration/         # File SQL Flyway migration
|   |-- static/
|   |   |-- css/              # Tailwind input/output CSS
|   |   `-- js/               # JavaScript UI shell
|   |-- templates/            # Thymeleaf templates
|   `-- application.properties
|-- .env.example              # Contoh konfigurasi environment
|-- mvnw / mvnw.cmd           # Maven Wrapper
|-- package.json              # Script dan dependency Tailwind
`-- pom.xml                   # Dependency dan build config Maven
```

## Notes for Contributors

- Jangan commit `.env`, `target/`, `node_modules/`, atau file log.
- Jalankan `npm run css:build` setelah mengubah class Tailwind di template/CSS.
- Jalankan `.\mvnw.cmd -DskipTests package` atau `./mvnw -DskipTests package` sebelum commit.
- Migration baru harus ditaruh di `src/main/resources/db/migration`.
- Untuk fitur baru, ikuti pola folder `app/controllers`, `app/services`, `app/dto`, `app/models`, dan `app/repositories`.
