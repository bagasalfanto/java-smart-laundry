# Smart Laundry - Project Plan Spring Boot

## 1. Ringkasan Project

Smart Laundry adalah aplikasi manajemen operasional laundry berbasis Java Spring Boot. Project ini mengikuti kebutuhan dari PDF `TUBES_Diagram_Kelompok2.pdf`, dengan fokus pada pengelolaan staff, member, layanan laundry, transaksi, pembayaran, stok, laporan, dan struk.

Target implementasi yang disarankan adalah MVP lengkap untuk demo tugas kuliah:

- Aplikasi web monolith memakai Spring Boot MVC dan Thymeleaf.
- Database MySQL.
- Login role Admin dan Staff.
- CRUD data master.
- Flow transaksi laundry dari estimasi harga sampai pembayaran lunas.
- Laporan pendapatan harian dan export PDF.
- Konfigurasi database dan app memakai file `.env` seperti Laravel.
- Schema database dikontrol oleh Flyway migration.
- Data awal dibuat lewat seeder yang idempotent.

## 2. Scope Fitur

### Admin

Admin memiliki akses penuh untuk:

- Login dan logout.
- Mengelola data staff.
- Mengelola data member/pelanggan.
- Mengelola paket layanan dan harga per kilogram.
- Mengelola stok bahan laundry.
- Melihat riwayat transaksi.
- Melihat laporan pendapatan.
- Export laporan ke PDF.
- Melihat activity log.

### Staff

Staff memiliki akses operasional untuk:

- Login dan logout.
- Mencari member berdasarkan nomor telepon.
- Membuat estimasi harga.
- Input order baru.
- Melihat antrean order.
- Update status cucian.
- Menghitung total biaya.
- Proses pembayaran.
- Cetak atau export struk.
- Mencari riwayat transaksi.

### User / Pelanggan

User tidak login langsung ke sistem. User direpresentasikan sebagai data pelanggan/member yang diproses oleh Staff.

## 3. Teknologi dan Dependensi

Gunakan Spring Initializr dengan konfigurasi:

- Project: Maven
- Language: Java
- Spring Boot: versi stabil terbaru
- Java: 17 atau 21
- Packaging: Jar

Dependensi utama:

- Spring Web
- Thymeleaf
- Spring Data JPA
- Spring Security
- Validation
- Lombok
- MySQL Driver
- Flyway
- Spring Boot DevTools

Contoh dependency tambahan OpenPDF di `pom.xml`:

```xml
<dependency>
    <groupId>com.github.librepdf</groupId>
    <artifactId>openpdf</artifactId>
    <version>1.3.39</version>
</dependency>
```

## 4. Konfigurasi Env Ala Laravel

Gunakan file `.env` untuk konfigurasi lokal. File `.env` tidak boleh dikomit ke repository. Yang dikomit adalah `.env.example`.

Tambahkan ke `.gitignore`:

```gitignore
.env
target/
```

Contoh `.env.example`:

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

Konfigurasi `application.properties`:

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

Catatan:

- `spring.jpa.hibernate.ddl-auto=validate` membuat Hibernate hanya memvalidasi schema, bukan membuat atau mengubah tabel.
- Schema database dibuat oleh Flyway migration.
- Jika `.env` tidak ada, Spring tetap memakai default dari placeholder.

## 5. Struktur Folder

Struktur package utama:

```text
src/main/java/com/smartlaundry
├── SmartLaundryApplication.java
├── config
│   └── SecurityConfig.java
├── controller
│   ├── AuthController.java
│   ├── DashboardController.java
│   ├── StaffController.java
│   ├── MemberController.java
│   ├── LayananController.java
│   ├── OrderController.java
│   ├── PaymentController.java
│   ├── InventoryController.java
│   └── ReportController.java
├── dto
│   ├── EstimasiHargaRequest.java
│   ├── OrderRequest.java
│   └── PaymentRequest.java
├── entity
│   ├── User.java
│   ├── StaffProfile.java
│   ├── AdminProfile.java
│   ├── Pelanggan.java
│   ├── Layanan.java
│   ├── Transaksi.java
│   ├── Inventaris.java
│   ├── Laporan.java
│   └── ActivityLog.java
├── enums
│   ├── Role.java
│   ├── OrderStatus.java
│   └── PaymentStatus.java
├── repository
│   ├── UserRepository.java
│   ├── PelangganRepository.java
│   ├── LayananRepository.java
│   ├── TransaksiRepository.java
│   ├── InventarisRepository.java
│   ├── LaporanRepository.java
│   └── ActivityLogRepository.java
├── seeder
│   ├── DatabaseSeeder.java
│   ├── UserSeeder.java
│   ├── LayananSeeder.java
│   ├── InventarisSeeder.java
│   └── DemoDataSeeder.java
├── service
│   ├── UserService.java
│   ├── PelangganService.java
│   ├── LayananService.java
│   ├── OrderService.java
│   ├── PaymentService.java
│   ├── InventoryService.java
│   ├── ReportService.java
│   └── PdfService.java
└── util
    └── InvoiceNumberGenerator.java
```

Struktur resources:

```text
src/main/resources
├── application.properties
├── db
│   └── migration
│       ├── V1__create_user_and_master_tables.sql
│       ├── V2__create_transaction_tables.sql
│       └── V3__create_reporting_and_logs.sql
├── static
│   ├── css
│   └── js
└── templates
    ├── layout
    │   ├── base.html
    │   ├── navbar.html
    │   └── sidebar.html
    ├── auth
    │   └── login.html
    ├── dashboard
    │   └── index.html
    ├── staff
    ├── member
    ├── layanan
    ├── order
    ├── inventory
    └── report
```

## 6. Model dan Database

### Rekomendasi Desain User

Class diagram PDF menunjukkan inheritance `User -> Admin` dan `User -> Staff`. Untuk Spring Security dan database, pendekatan yang lebih aman adalah:

- Satu tabel `users` untuk login semua role.
- Role dibedakan memakai enum `ADMIN` dan `STAFF`.
- Detail khusus disimpan di tabel profile jika diperlukan.

Pendekatan ini lebih sederhana untuk autentikasi, authorization, dan query.

### Daftar Entity

#### User

Menyimpan data login.

Kolom utama:

- `id`
- `username`
- `password`
- `role`
- `active`
- `created_at`
- `updated_at`

#### StaffProfile

Menyimpan detail staff.

Kolom utama:

- `id`
- `user_id`
- `nama`
- `jumlah_shift`

#### AdminProfile

Menyimpan detail admin.

Kolom utama:

- `id`
- `user_id`
- `nama`
- `kode_otoritas`

#### Pelanggan

Menyimpan data pelanggan dan status member.

Kolom utama:

- `id`
- `nama`
- `no_telp`
- `is_member`
- `poin`
- `created_at`
- `updated_at`

#### Layanan

Menyimpan paket layanan laundry.

Kolom utama:

- `id`
- `nama_paket`
- `harga_per_kg`
- `estimasi_waktu`
- `active`
- `created_at`
- `updated_at`

#### Inventaris

Menyimpan stok bahan.

Kolom utama:

- `id`
- `nama_barang`
- `stok`
- `satuan`
- `created_at`
- `updated_at`

#### Transaksi

Menyimpan order dan pembayaran.

Kolom utama:

- `id`
- `invoice_number`
- `pelanggan_id`
- `layanan_id`
- `staff_id`
- `berat`
- `subtotal`
- `diskon`
- `total_bayar`
- `order_status`
- `payment_status`
- `tanggal_masuk`
- `tanggal_selesai`
- `paid_at`
- `created_at`
- `updated_at`

#### Laporan

Menyimpan snapshot laporan.

Kolom utama:

- `id`
- `periode`
- `tanggal_mulai`
- `tanggal_selesai`
- `total_pendapatan`
- `created_at`

#### ActivityLog

Menyimpan aktivitas penting.

Kolom utama:

- `id`
- `user_id`
- `activity`
- `ip_address`
- `created_at`

## 7. Enum

`Role`:

```java
public enum Role {
    ADMIN,
    STAFF
}
```

`OrderStatus`:

```java
public enum OrderStatus {
    ANTRIAN,
    PROSES,
    SELESAI
}
```

`PaymentStatus`:

```java
public enum PaymentStatus {
    BELUM_LUNAS,
    LUNAS
}
```

## 8. Migration Flyway

Migration menjadi sumber kebenaran struktur database.

### Validasi Kesesuaian Migration

Ya, rancangan migration ini sudah sesuai untuk MVP Smart Laundry berdasarkan PDF. Tabel yang dibuat sudah menutup kebutuhan utama:

- Login dan role: `users`, `admin_profiles`, `staff_profiles`.
- Master data: `pelanggan`, `layanan`, `inventaris`.
- Operasional laundry: `transaksi`.
- Laporan dan audit: `laporan`, `activity_logs`.

Catatan desain yang dipilih:

- `users.role` memakai nilai `ADMIN` dan `STAFF`, bukan membuat tabel login terpisah untuk Admin dan Staff.
- `transaksi` menjadi pusat relasi karena menghubungkan pelanggan, layanan, staff, status cucian, dan pembayaran.
- `laporan` disimpan sebagai snapshot, tetapi sumber perhitungan tetap dari transaksi `LUNAS`.
- Enum Java disimpan sebagai `VARCHAR` di database agar mudah dibaca di phpMyAdmin.
- Uang memakai `DECIMAL`, bukan `DOUBLE`, supaya hasil pembayaran tidak bermasalah karena pembulatan floating point.

Struktur migration ini cukup untuk demo, CRUD, transaksi, pembayaran, laporan, dan seeder. Jika nanti ada fitur lebih detail seperti banyak item inventaris per order, metode pembayaran, atau diskon custom per member, buat migration tambahan baru, jangan mengubah migration lama yang sudah dijalankan.

### V1__create_user_and_master_tables.sql

Isi:

- `users`
- `staff_profiles`
- `admin_profiles`
- `pelanggan`
- `layanan`
- `inventaris`

### V2__create_transaction_tables.sql

Isi:

- `transaksi`
- foreign key ke `pelanggan`, `layanan`, dan `users`
- index untuk `invoice_number`, `order_status`, `payment_status`, dan `paid_at`

### V3__create_reporting_and_logs.sql

Isi:

- `laporan`
- `activity_logs`

Aturan migration:

- Jangan ubah file migration lama setelah pernah dijalankan.
- Jika ada perubahan schema, buat migration baru, misalnya `V4__add_discount_config.sql`.
- Hindari `ddl-auto=update` karena sulit dikontrol saat tugas diperiksa di komputer lain.

### Draft SQL V1__create_user_and_master_tables.sql

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_users_role CHECK (role IN ('ADMIN', 'STAFF'))
);

CREATE TABLE admin_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    nama VARCHAR(150) NOT NULL,
    kode_otoritas VARCHAR(50) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE staff_profiles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    nama VARCHAR(150) NOT NULL,
    jumlah_shift INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_staff_profiles_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
);

CREATE TABLE pelanggan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nama VARCHAR(150) NOT NULL,
    no_telp VARCHAR(30) NOT NULL UNIQUE,
    is_member BOOLEAN NOT NULL DEFAULT FALSE,
    poin INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE layanan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nama_paket VARCHAR(100) NOT NULL UNIQUE,
    harga_per_kg DECIMAL(12, 2) NOT NULL,
    estimasi_waktu INT NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_layanan_harga CHECK (harga_per_kg >= 0),
    CONSTRAINT chk_layanan_estimasi CHECK (estimasi_waktu >= 0)
);

CREATE TABLE inventaris (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nama_barang VARCHAR(100) NOT NULL UNIQUE,
    stok INT NOT NULL DEFAULT 0,
    satuan VARCHAR(30) NOT NULL DEFAULT 'pcs',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT chk_inventaris_stok CHECK (stok >= 0)
);
```

### Draft SQL V2__create_transaction_tables.sql

```sql
CREATE TABLE transaksi (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(50) NOT NULL UNIQUE,
    pelanggan_id BIGINT NOT NULL,
    layanan_id BIGINT NOT NULL,
    staff_id BIGINT NOT NULL,
    berat DECIMAL(8, 2) NOT NULL,
    subtotal DECIMAL(12, 2) NOT NULL,
    diskon DECIMAL(12, 2) NOT NULL DEFAULT 0,
    total_bayar DECIMAL(12, 2) NOT NULL,
    order_status VARCHAR(20) NOT NULL DEFAULT 'ANTRIAN',
    payment_status VARCHAR(20) NOT NULL DEFAULT 'BELUM_LUNAS',
    tanggal_masuk DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tanggal_selesai DATETIME NULL,
    paid_at DATETIME NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_transaksi_pelanggan
        FOREIGN KEY (pelanggan_id) REFERENCES pelanggan(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_layanan
        FOREIGN KEY (layanan_id) REFERENCES layanan(id)
        ON DELETE RESTRICT,
    CONSTRAINT fk_transaksi_staff
        FOREIGN KEY (staff_id) REFERENCES users(id)
        ON DELETE RESTRICT,
    CONSTRAINT chk_transaksi_berat CHECK (berat > 0),
    CONSTRAINT chk_transaksi_subtotal CHECK (subtotal >= 0),
    CONSTRAINT chk_transaksi_diskon CHECK (diskon >= 0),
    CONSTRAINT chk_transaksi_total CHECK (total_bayar >= 0),
    CONSTRAINT chk_transaksi_order_status CHECK (order_status IN ('ANTRIAN', 'PROSES', 'SELESAI')),
    CONSTRAINT chk_transaksi_payment_status CHECK (payment_status IN ('BELUM_LUNAS', 'LUNAS'))
);

CREATE INDEX idx_transaksi_order_status ON transaksi(order_status);
CREATE INDEX idx_transaksi_payment_status ON transaksi(payment_status);
CREATE INDEX idx_transaksi_paid_at ON transaksi(paid_at);
CREATE INDEX idx_transaksi_tanggal_masuk ON transaksi(tanggal_masuk);
```

### Draft SQL V3__create_reporting_and_logs.sql

```sql
CREATE TABLE laporan (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    periode VARCHAR(30) NOT NULL,
    tanggal_mulai DATE NOT NULL,
    tanggal_selesai DATE NOT NULL,
    total_pendapatan DECIMAL(12, 2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_laporan_total CHECK (total_pendapatan >= 0)
);

CREATE TABLE activity_logs (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NULL,
    activity VARCHAR(255) NOT NULL,
    ip_address VARCHAR(45) NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_activity_logs_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_laporan_periode ON laporan(periode);
CREATE INDEX idx_laporan_tanggal ON laporan(tanggal_mulai, tanggal_selesai);
CREATE INDEX idx_activity_logs_user_id ON activity_logs(user_id);
CREATE INDEX idx_activity_logs_created_at ON activity_logs(created_at);
```

### Catatan Saat Dijalankan di MySQL

- Pastikan MySQL versi 8 atau lebih baru agar constraint `CHECK` benar-benar didukung.
- Jika memakai MySQL lama yang mengabaikan `CHECK`, validasi tetap wajib dibuat di layer service dan DTO.
- Jalankan migration dari database kosong agar urutan `V1`, `V2`, dan `V3` bersih.
- Setelah migration berhasil, cek tabel `flyway_schema_history` di phpMyAdmin.
- Untuk reset database development, drop database lalu buat ulang dari phpMyAdmin, kemudian jalankan aplikasi lagi. Jangan lakukan ini di production.

## 9. Seeder Database

Seeder dibuat untuk mengisi data awal secara otomatis saat aplikasi start.

Seeder hanya jalan jika:

```properties
APP_SEED=true
```

### DatabaseSeeder

Class utama yang memanggil semua seeder:

- `UserSeeder`
- `LayananSeeder`
- `InventarisSeeder`
- `DemoDataSeeder` jika `SEED_DEMO_DATA=true`

### UserSeeder

Membuat akun default dari env:

- Admin dari `SEED_ADMIN_USERNAME` dan `SEED_ADMIN_PASSWORD`
- Staff dari `SEED_STAFF_USERNAME` dan `SEED_STAFF_PASSWORD`

Password wajib di-hash memakai BCrypt.

Seeder harus cek username dulu agar tidak membuat user duplikat.

### LayananSeeder

Data awal:

- Cuci Kering
- Cuci Setrika
- Express

Seeder cek berdasarkan `nama_paket`.

### InventarisSeeder

Data awal:

- Deterjen
- Pewangi
- Plastik

Seeder cek berdasarkan `nama_barang`.

### DemoDataSeeder

Opsional dan default nonaktif.

Dipakai hanya jika ingin mengisi:

- Beberapa pelanggan/member contoh.
- Beberapa transaksi contoh.
- Data pembayaran lunas untuk demo laporan.

## 10. Business Rules

### Estimasi Harga

Formula dasar:

```text
subtotal = beratKg * hargaPerKg
diskon = isMember ? subtotal * 10% : 0
total = subtotal - diskon
```

Diskon member default adalah `10%`. Nilai ini bisa dipindahkan ke konfigurasi jika ingin fleksibel.

### Order Baru

Saat staff membuat order:

- Pelanggan wajib dipilih atau dibuat.
- Layanan wajib aktif.
- Berat harus lebih dari 0.
- Status awal order adalah `ANTRIAN`.
- Status pembayaran awal adalah `BELUM_LUNAS`.
- Invoice number dibuat otomatis.
- Stok bahan berkurang otomatis.

### Update Status Cucian

Urutan status:

```text
ANTRIAN -> PROSES -> SELESAI
```

Status tidak boleh mundur kecuali admin diberi fitur koreksi khusus.

### Pembayaran

Pembayaran hanya bisa diproses jika:

- Transaksi belum lunas.
- Total bayar sudah dihitung.

Setelah pembayaran:

- `payment_status = LUNAS`
- `paid_at` terisi
- transaksi masuk riwayat pembayaran
- struk bisa dicetak/export

### Laporan

Laporan menghitung transaksi dengan:

- `payment_status = LUNAS`
- `paid_at` berada dalam rentang tanggal laporan

Transaksi belum lunas tidak masuk pendapatan.

## 11. Alur Pembuatan Project

1. Buat project Spring Boot dari Spring Initializr.
2. Tambahkan semua dependency utama.
3. Buat `.env.example`, `.gitignore`, dan `application.properties`.
4. Buat database MySQL `smart_laundry`.
5. Buat migration Flyway untuk tabel user, master, transaksi, laporan, dan log.
6. Buat entity JPA yang sesuai dengan migration.
7. Buat repository untuk semua entity.
8. Buat seeder idempotent dan aktifkan lewat `APP_SEED`.
9. Implement Spring Security:
   - login form
   - BCrypt password encoder
   - role-based access
   - redirect dashboard sesuai role
10. Buat layout Thymeleaf:
    - base layout
    - navbar
    - sidebar
    - halaman login
11. Implement fitur Admin:
    - CRUD staff
    - CRUD member
    - CRUD layanan
    - CRUD inventaris
    - laporan
12. Implement fitur Staff:
    - cari member
    - estimasi harga
    - input order
    - antrean order
    - update status
    - pembayaran
    - struk
13. Implement laporan harian dan export PDF.
14. Tambahkan test service.
15. Jalankan manual test end-to-end.

## 12. Alur Demo

Alur demo yang paling mudah dipahami:

1. Login sebagai Admin.
2. Tunjukkan data layanan dan inventaris.
3. Tambahkan member baru.
4. Login sebagai Staff.
5. Cari member berdasarkan nomor telepon.
6. Buat estimasi harga.
7. Input order baru.
8. Tunjukkan order masuk antrean.
9. Update status dari `ANTRIAN` ke `PROSES`, lalu `SELESAI`.
10. Proses pembayaran.
11. Cetak/export struk.
12. Login Admin lagi.
13. Tampilkan laporan pendapatan harian.
14. Export laporan ke PDF.

## 13. Test Plan

### Konfigurasi

- Aplikasi bisa start memakai konfigurasi dari `.env`.
- Jika `.env` tidak ada, aplikasi tetap memakai default dari `application.properties`.
- Database MySQL berhasil terkoneksi.

### Migration

- Flyway berhasil membuat semua tabel dari database kosong.
- `spring.jpa.hibernate.ddl-auto=validate` tidak error.
- Entity JPA cocok dengan schema database.

### Seeder

- Admin default berhasil dibuat.
- Staff default berhasil dibuat.
- Layanan default berhasil dibuat.
- Inventaris default berhasil dibuat.
- Seeder tidak membuat data duplikat meskipun aplikasi dijalankan berkali-kali.

### Security

- Admin bisa login.
- Staff bisa login.
- Admin bisa akses halaman master dan laporan.
- Staff tidak bisa akses CRUD staff atau halaman admin-only.
- Logout berhasil mengakhiri session.

### Transaksi

- Estimasi harga non-member benar.
- Estimasi harga member mendapat diskon.
- Order baru masuk status `ANTRIAN`.
- Status order bisa berubah sesuai alur.
- Pembayaran mengubah status menjadi `LUNAS`.
- Transaksi lunas muncul di riwayat.

### Laporan

- Transaksi belum lunas tidak masuk laporan.
- Transaksi lunas masuk laporan sesuai tanggal pembayaran.
- Total pendapatan sesuai jumlah transaksi lunas.
- Export PDF berhasil dibuat.

## 14. Acceptance Criteria

Project dianggap selesai untuk MVP jika:

- Aplikasi bisa dijalankan lokal dengan `mvn spring-boot:run`.
- Konfigurasi database diambil dari `.env`.
- Database schema dibuat oleh Flyway.
- Seeder membuat minimal satu Admin, satu Staff, tiga layanan, dan tiga inventaris.
- Admin dan Staff bisa login sesuai role.
- Admin bisa mengelola master data.
- Staff bisa membuat order dan menyelesaikan pembayaran.
- Sistem menghitung biaya otomatis berdasarkan berat dan layanan.
- Member mendapat diskon otomatis.
- Laporan pendapatan harian menampilkan transaksi lunas.
- Struk atau laporan dapat diekspor ke PDF.

## 15. Evaluasi dan Pendekatan yang Lebih Baik

Plan yang lebih baik dibanding meniru class diagram mentah adalah memakai pendekatan Spring yang lebih praktis:

- Gunakan satu tabel `users` dengan enum role, bukan inheritance database yang rumit.
- Gunakan Flyway migration agar schema konsisten di semua komputer.
- Gunakan `.env.example` agar setup mirip Laravel dan mudah dibagikan.
- Gunakan seeder idempotent agar data awal selalu tersedia tanpa insert manual.
- Kerjakan MVP lengkap dulu sebelum mempercantik UI atau menambah fitur production.

Hal yang sebaiknya tidak dilakukan di awal:

- Jangan langsung membuat frontend terpisah karena scope tugas akan membesar.
- Jangan memakai `ddl-auto=update` sebagai cara utama membuat database.
- Jangan menyimpan password default dalam kode Java.
- Jangan membuat export PDF sebelum flow transaksi dan laporan HTML berjalan.
- Jangan membuat fitur terlalu kompleks sebelum modul utama PDF selesai.

## 16. Prioritas Implementasi

Prioritas pertama:

- Env config
- Migration
- Seeder
- Login role
- CRUD layanan dan member
- Order dan pembayaran

Prioritas kedua:

- Inventaris otomatis berkurang
- Riwayat transaksi
- Laporan harian
- Struk PDF

Prioritas ketiga:

- Activity log detail
- Export laporan PDF
- Dashboard statistik
- UI polishing
