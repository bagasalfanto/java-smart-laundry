# Agent Guide - Smart Laundry

Panduan ini diringkas dari `SMART_LAUNDRY_PLAN.md` dan disesuaikan dengan kondisi repo saat ini. Gunakan file plan sebagai sumber detail utama jika ada keputusan implementasi yang belum jelas.

## Project Context

Smart Laundry adalah aplikasi manajemen operasional laundry berbasis Java Spring Boot. Targetnya adalah MVP web monolith untuk demo tugas kuliah dengan Spring MVC, Thymeleaf, Spring Security, JPA, MySQL, Flyway migration, dan seeder idempotent.

Repo saat ini masih berada di tahap awal:

- Maven project Spring Boot.
- Java version: 21.
- Package aktual: `com.laundry.smartlaundry`.
- Entry point: `src/main/java/com/laundry/smartlaundry/SmartlaundryApplication.java`.
- Konfigurasi awal baru berisi `spring.application.name=smartlaundry`.

Jangan membuat package `com.smartlaundry` kecuali semua source dipindahkan secara sengaja. Ikuti package aktual `com.laundry.smartlaundry`.

## Main Goal

Bangun MVP lengkap dengan kemampuan berikut:

- Login/logout untuk role `ADMIN` dan `STAFF`.
- CRUD master data untuk staff, pelanggan/member, layanan, dan inventaris.
- Flow transaksi laundry dari estimasi harga, input order, update status cucian, sampai pembayaran lunas.
- Riwayat transaksi, laporan pendapatan harian, dan export/cetak struk atau laporan PDF.
- Konfigurasi lokal memakai `.env` dan contoh `.env.example`.
- Schema dikontrol oleh Flyway, bukan Hibernate auto-update.
- Seeder membuat data awal tanpa duplikasi.

## Tech Stack

Gunakan dependensi yang sudah ada di `pom.xml`:

- Spring Boot 4.0.6.
- Java 21.
- Maven.
- Spring Web MVC.
- Thymeleaf.
- Spring Data JPA.
- Spring Security.
- Validation.
- Lombok.
- MySQL Connector/J.
- Flyway + Flyway MySQL.
- Thymeleaf Spring Security extras.

Tambahkan OpenPDF hanya saat mulai mengerjakan export PDF:

```xml
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.39</version>
</dependency>
```

## Configuration Rules

Implementasikan konfigurasi env seperti Laravel:

- `.env` untuk konfigurasi lokal dan tidak dikomit.
- `.env.example` dikomit.
- `application.properties` harus import `.env` secara opsional.
- Pakai default placeholder agar aplikasi tetap bisa start tanpa `.env`.
- Hibernate harus memakai `spring.jpa.hibernate.ddl-auto=validate`.
- Flyway menjadi sumber kebenaran schema.

Target `application.properties`:

```properties
spring.config.import=optional:file:.env[.properties]

spring.application.name=${APP_NAME:Smart Laundry}
server.port=${APP_PORT:8080}

spring.datasource.url=jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME:smart_laundry}?createDatabaseIfNotExist=true&serverTimezone=${DB_TIMEZONE:Asia/Jakarta}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:}

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.clean-disabled=true

spring.thymeleaf.cache=false
```

Target `.env.example`:

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

Pastikan `.gitignore` memuat:

```gitignore
.env
target/
```

## Package Structure

Buat struktur Laravel-inspired di bawah `src/main/java/com/laundry/smartlaundry`:

```text
app
  controllers
    admin
    auth
    dashboard
    staff
  models
  repositories
  services
    admin
    auth
    staff
  enums
  security
config
console
database
  seeders
```

Mapping tanggung jawab package:

- `app.controllers.auth`: login, logout redirect, and authentication pages.
- `app.controllers.dashboard`: shared dashboard entrypoint.
- `app.controllers.admin`: Admin-only MVC controllers.
- `app.controllers.staff`: Staff operational MVC controllers.
- `app.models`: JPA entity.
- `app.repositories`: Spring Data JPA repository.
- `app.services.auth`: authentication and login protection services.
- `app.services.admin`: Admin-only business services.
- `app.services.staff`: Staff operational business services.
- `app.enums`: enum domain.
- `app.security`: listener/helper security.
- `config`: konfigurasi Spring.
- `console`: command runner seperti `migrate`, `db:seed`, dan `migrate:seed`.
- `database.seeders`: seeder database.

Resources yang ditargetkan:

```text
src/main/resources/db/migration
src/main/resources/static/css
src/main/resources/static/js
src/main/resources/templates/layout
src/main/resources/templates/auth
src/main/resources/templates/dashboard
src/main/resources/templates/staff
src/main/resources/templates/member
src/main/resources/templates/layanan
src/main/resources/templates/order
src/main/resources/templates/inventory
src/main/resources/templates/report
```

## Domain Model

Gunakan satu tabel login `users` dengan enum role. Jangan implement inheritance database untuk Admin dan Staff.

Entity utama:

- `User`: login, password hash, role, active.
- `AdminProfile`: detail admin.
- `StaffProfile`: detail staff.
- `Pelanggan`: pelanggan/member.
- `Layanan`: paket laundry dan harga per kilogram.
- `Inventaris`: stok bahan laundry.
- `Transaksi`: order dan pembayaran.
- `Laporan`: snapshot laporan.
- `ActivityLog`: audit aktivitas penting.

Enum:

```java
public enum Role {
    ADMIN,
    STAFF
}
```

```java
public enum OrderStatus {
    ANTRIAN,
    PROSES,
    SELESAI
}
```

```java
public enum PaymentStatus {
    BELUM_LUNAS,
    LUNAS
}
```

## Database And Migration Rules

Migration Flyway wajib menjadi sumber kebenaran schema:

- `V1__create_user_and_master_tables.sql`
- `V2__create_transaction_tables.sql`
- `V3__create_reporting_and_logs.sql`

Aturan penting:

- Jangan ubah migration lama setelah pernah dijalankan.
- Jika schema berubah, buat migration baru seperti `V4__add_discount_config.sql`.
- Simpan enum Java sebagai `VARCHAR`.
- Simpan uang sebagai `DECIMAL`, bukan `DOUBLE`.
- Gunakan validasi service/DTO selain constraint database.
- Target MySQL 8+ agar constraint `CHECK` didukung.

Relasi inti:

- `transaksi.pelanggan_id` -> `pelanggan.id`.
- `transaksi.layanan_id` -> `layanan.id`.
- `transaksi.staff_id` -> `users.id`.
- `admin_profiles.user_id` dan `staff_profiles.user_id` -> `users.id`.

## Seeder Rules

Seeder hanya berjalan jika `APP_SEED=true`.

Buat `DatabaseSeeder` sebagai orchestrator:

- `UserSeeder`
- `LayananSeeder`
- `InventarisSeeder`
- `DemoDataSeeder` jika `SEED_DEMO_DATA=true`

Seeder harus idempotent:

- Cek username sebelum membuat user.
- Cek `nama_paket` sebelum membuat layanan.
- Cek `nama_barang` sebelum membuat inventaris.
- Password default wajib di-hash dengan BCrypt.

Data minimum:

- Admin default dari env.
- Staff default dari env.
- Layanan: `Cuci Kering`, `Cuci Setrika`, `Express`.
- Inventaris: `Deterjen`, `Pewangi`, `Plastik`.

## Business Rules

Estimasi harga:

```text
subtotal = beratKg * hargaPerKg
diskon = isMember ? subtotal * 10% : 0
total = subtotal - diskon
```

Order baru:

- Pelanggan wajib dipilih atau dibuat.
- Layanan wajib aktif.
- Berat harus lebih dari 0.
- Status awal `ANTRIAN`.
- Payment awal `BELUM_LUNAS`.
- Invoice number dibuat otomatis.
- Stok bahan berkurang otomatis jika fitur inventaris sudah aktif.

Update status cucian:

```text
ANTRIAN -> PROSES -> SELESAI
```

Status tidak boleh mundur kecuali nanti ada fitur koreksi admin.

Pembayaran:

- Hanya transaksi yang belum lunas dapat dibayar.
- Setelah bayar, set `payment_status = LUNAS`.
- Isi `paid_at`.
- Transaksi lunas masuk laporan dan riwayat pembayaran.

Laporan:

- Hitung hanya transaksi `LUNAS`.
- Filter memakai `paid_at` dalam rentang tanggal.
- Transaksi belum lunas tidak boleh masuk pendapatan.

## Implementation Priority

Kerjakan secara bertahap:

1. Env config, `.env.example`, dan `.gitignore`.
2. Migration Flyway.
3. Entity JPA dan repository.
4. Seeder idempotent.
5. Spring Security: login form, BCrypt, role access, redirect dashboard.
6. Layout Thymeleaf: base, navbar, sidebar, login, dashboard.
7. CRUD layanan dan pelanggan/member.
8. CRUD staff dan inventaris.
9. Estimasi harga, order baru, antrean order, update status.
10. Pembayaran dan struk.
11. Riwayat transaksi dan laporan harian.
12. Export PDF.
13. Activity log, statistik dashboard, dan UI polishing.

Prioritas utama MVP:

- Env config.
- Migration.
- Seeder.
- Login role.
- CRUD layanan dan member.
- Order dan pembayaran.

## UI Guidance

Gunakan Thymeleaf server-rendered pages. Tidak perlu frontend terpisah.

UI harus fokus untuk operasional:

- Navigasi jelas untuk Admin dan Staff.
- Dashboard langsung menampilkan pekerjaan yang relevan.
- Form transaksi mudah dipakai berulang.
- Tabel data master mudah dicari dan discan.
- Jangan membuat landing page marketing.

## Testing And Verification

Minimal verifikasi sebelum menganggap fitur selesai:

- `mvn test` berjalan.
- Aplikasi bisa start dengan `mvn spring-boot:run`.
- Flyway membuat schema dari database kosong.
- Hibernate validate tidak error.
- Seeder tidak membuat data duplikat saat aplikasi dijalankan ulang.
- Admin dan Staff bisa login.
- Role Staff tidak bisa mengakses halaman Admin-only.
- Estimasi harga member dan non-member benar.
- Payment lunas muncul di laporan harian.

## Demo Flow

Alur demo target:

1. Login Admin.
2. Tampilkan layanan dan inventaris.
3. Tambah member baru.
4. Login Staff.
5. Cari member berdasarkan nomor telepon.
6. Buat estimasi harga.
7. Input order baru.
8. Tampilkan order di antrean.
9. Update status `ANTRIAN` ke `PROSES`, lalu `SELESAI`.
10. Proses pembayaran.
11. Cetak/export struk.
12. Login Admin.
13. Tampilkan laporan pendapatan harian.
14. Export laporan ke PDF.

## Do Not Do

- Jangan memakai `ddl-auto=update` untuk membuat schema utama.
- Jangan menyimpan password default di kode Java.
- Jangan membuat tabel login terpisah untuk Admin dan Staff.
- Jangan membuat frontend terpisah sebelum MVP selesai.
- Jangan mengerjakan export PDF sebelum flow transaksi dan laporan HTML berjalan.
- Jangan mengubah migration lama yang sudah pernah dijalankan; tambah migration baru.
