# Dokumentasi Lengkap Kode — Bagian Raihan (Payment & Billing)

> Tugas Besar PBO Kelompok 2 — *Smart Laundry*.
> Dokumen ini menjelaskan **seluruh kode milik Raihan** secara detail: cara kerja
> logika, konsep PBO/teknologi yang dipakai, alur data, sampai cara menjalankan & menguji.
> Ditulis agar mudah dipahami pemula. Lihat juga ringkasan singkat di
> [`Modul-Payment-Billing-Raihan.md`](Modul-Payment-Billing-Raihan.md) dan
> alur algoritma di [`Pseudocode-Payment-Billing.md`](Pseudocode-Payment-Billing.md).

---

## 0. Gambaran Umum

Bagian Raihan adalah **Payment & Billing**, dengan **2 fitur inti**:

1. **Billing Logic** — menghitung total biaya otomatis dari **berat** cucian,
   **jenis layanan** (harga/kg), dan **status member** (member dapat diskon).
2. **Transaction History** — menyimpan & mencari data transaksi yang sudah **LUNAS**.

Kode Raihan hidup di **DUA lapisan (layer)** yang berbeda tujuannya:

| Layer | Lokasi | Tujuan | Teknologi |
|-------|--------|--------|-----------|
| **A. Modul standalone** | `module/payment/` | Demo konsep PBO murni untuk dinilai dosen | Java murni, uang `double`, ada `Main` sendiri |
| **B. Fitur web** | `app/.../payment/` + template | Menu "Pembayaran" di aplikasi web nyata | Spring Boot MVC + JPA, uang `BigDecimal`, database |

Keduanya **logikanya sama** (hitung diskon 5% untuk member, simpan & cari transaksi lunas),
hanya beda "baju": yang satu Java polos di terminal, yang satu aplikasi web dengan database.

### Peta file milik Raihan

```
LAYER A — Modul standalone (Java murni)
src/main/java/com/laundry/smartlaundry/module/payment/
├── Pelanggan.java        # data pelanggan + cekStatusMember()
├── Transaksi.java        # BILLING LOGIC: hitungBiaya(), terapkanDiskon(), cetakStruk()
├── BillingManager.java   # orkestrator + TRANSACTION HISTORY (simpan & cari)
├── Main.java             # demo kedua fitur (jalankan ini)
└── package-info.java     # dokumentasi paket
            (memakai ulang module/servicecatalog/Layanan.java milik Shellyn)

LAYER B — Fitur web (Spring Boot MVC)
src/main/java/com/laundry/smartlaundry/app/
├── services/payment/PaymentService.java       # "BillingManager versi web"
└── controllers/payment/PaymentController.java  # route /payments
src/main/resources/templates/
├── payment/index.html                          # tampilan halaman Pembayaran
└── layout/sidebar.html                         # menu "Pembayaran" diaktifkan
            (memakai ulang entity app/models/Transaksi.java + TransaksiRepository)
```

---

# BAGIAN A — Modul Standalone (`module/payment/`)

Ini Java **murni**: tanpa Spring, tanpa database, tanpa Lombok. Uang pakai tipe
`double` supaya gampang dipahami. Tujuannya menunjukkan konsep **Pemrograman
Berorientasi Objek (PBO)**.

## A.1. Konsep PBO yang ditunjukkan

| Konsep PBO | Minggu | Di mana | Penjelasan singkat |
|-----------|:------:|---------|--------------------|
| **Class & Object** | M01 | `Pelanggan`, `Transaksi`, `Layanan` | Cetakan (class) → dibuat objek nyata |
| **Encapsulation** | M02 | semua atribut `private` + getter/setter | Data disembunyikan, diakses lewat method |
| **Constructor** | M01–02 | `new Transaksi(...)`, `new Pelanggan(...)` | Mengisi data awal objek |
| **Aggregation / Reuse** | M03 | `Transaksi` menyimpan `Pelanggan` + `Layanan` | Satu objek "menyatukan" objek lain |
| **Separation of concerns** | M03 | `Transaksi` (data) vs `BillingManager` (pengelola) | Data dan logika pengelola dipisah |
| **Konstanta (static)** | M06 | `DISKON_MEMBER` | Aturan bisnis ditaruh di satu tempat |
| **Collection** | M06 | `List`/`ArrayList`/`Optional`/`stream` di `BillingManager` | Menyimpan & mengelola banyak transaksi |

## A.2. Class `Pelanggan` — data pelanggan

**Atribut (semua `private` = encapsulation):**
```
idMember : String    // contoh "MBR-01"
nama     : String
noTelp   : String    // dipakai untuk pencarian
member   : boolean    // true = dapat diskon
```

**Method penting:**
- `cekStatusMember()` → mengembalikan `member` (`true`/`false`). Inilah yang dipakai
  proses billing untuk memutuskan diskon.
- Getter/setter biasa (`getNama()`, `getNoTelp()`, dst.).

> Catatan: class ini **sederhana sengaja**. Tidak ada fitur poin loyalitas
> (sempat ada di draft lama, tapi dibuang agar kode tetap minimalis).

## A.3. Class `Transaksi` — inti BILLING LOGIC

Satu objek `Transaksi` menyatukan **3 data utama**: `Pelanggan` (untuk tahu member),
`Layanan` (untuk tahu harga/kg), dan `berat` (untuk dikalikan).

**Konstanta aturan bisnis:**
```java
public static final double DISKON_MEMBER = 0.05; // 5%
```
> Diskon ditaruh sebagai konstanta supaya kalau berubah (mis. jadi 8%), cukup
> ganti **satu baris** ini, dan semua perhitungan ikut berubah.

**Atribut hasil hitung:** `subtotal`, `diskon`, `totalBayar`, `lunas`, `sudahDihitung`.

**Constructor** — saat objek dibuat, ada **validasi** sederhana:
```
JIKA berat <= 0  → tampilkan peringatan, berat diatur 0 (transaksi tak bisa dibayar)
lunas = false    → transaksi baru selalu BELUM LUNAS
```

**Cara kerja Billing Logic — 2 langkah berurutan:**

```
Langkah 1: hitungBiaya()
    subtotal = berat × layanan.getHargaPerKg()

Langkah 2: terapkanDiskon()
    JIKA pelanggan.cekStatusMember()  → diskon = subtotal × 0.05   (member)
    SELAIN ITU                        → diskon = 0                 (bukan member)
    totalBayar    = subtotal − diskon
    sudahDihitung = true              (penanda biaya sudah dihitung)
```

**Method lain:**
- `prosesPembayaran()` → set `lunas = true`.
- `cetakStruk()` → cetak struk rapi ke layar; persentase diskon di struk = `(int)(DISKON_MEMBER*100)` = **5%** untuk member, 0% untuk bukan member.
- `getStatusBayar()` → mengembalikan teks `"LUNAS"` atau `"BELUM LUNAS"`.
- `isSudahDihitung()` → dipakai `BillingManager` untuk mencegah transaksi "kosong" dibayar.

## A.4. Class `BillingManager` — orkestrator + TRANSACTION HISTORY

Class ini "otak"-nya. Menyimpan **satu daftar**:
```java
private List<Transaksi> riwayatLunas; // hanya transaksi LUNAS yang masuk sini
```
Karena hanya transaksi lunas yang disimpan, daftar ini **sekaligus** menjadi
"Riwayat Transaksi" (Transaction History).

### Fitur 1 — Billing Logic

**`buatTagihan(idOrder, pelanggan, layanan, berat, tglMasuk)`**
```
1. trx = Transaksi baru
2. trx.hitungBiaya()      ← langkah 1 (subtotal)
3. trx.terapkanDiskon()   ← langkah 2 (diskon → total)
4. tampilkan rincian, kembalikan trx (status: BELUM LUNAS)
```

**`prosesPembayaran(trx)`** — punya **2 penjaga (guard)** supaya data tetap benar:
```
Penjaga 1: kalau trx sudah lunas       → batal (jangan bayar dua kali)
Penjaga 2: kalau biaya belum dihitung  → batal (cegah total Rp0 masuk riwayat)
           ATAU total <= 0
Kalau lolos: trx.prosesPembayaran() (LUNAS) → simpan ke riwayatLunas → cetak info
```

### Fitur 2 — Transaction History (pencarian)

| Method | Cara kerja | Hasil |
|--------|-----------|-------|
| `cariByInvoice(idOrder)` | `stream().filter(...equalsIgnoreCase...)` | `Optional<Transaksi>` (1 / kosong) |
| `cariByPelanggan(kataKunci)` | loop, cocokkan nama **atau** noTelp (`contains`, huruf besar/kecil diabaikan) | `List<Transaksi>` |
| `tampilkanSemuaRiwayat()` | loop cetak semua + total | tampil ke layar |
| `hitungTotalPendapatan()` | jumlahkan `totalBayar` semua transaksi lunas | `double` (dipakai modul Reporting/Herdian) |

## A.5. Alur demo (`Main.java`)

```
1. Buat BillingManager
2. Siapkan data: 2 Layanan, 2 Pelanggan (Andi = member, Budi = bukan)
3. FITUR 1: buatTagihan untuk Andi & Budi → biaya dihitung otomatis
4. prosesPembayaran keduanya → masuk riwayat
5. cetakStruk Andi
6. FITUR 2: tampilkanSemuaRiwayat, cariByInvoice("INV-002"), cariByPelanggan("Andi")
```

**Contoh hasil hitung (diskon member 5%):**
- Andi (member), 3 kg @ Rp8.000 → subtotal 24.000 − diskon **1.200** = **22.800**
- Budi (bukan), 2 kg @ Rp12.000 → subtotal 24.000 − diskon 0 = **24.000**
- Total pendapatan riwayat = **46.800**

## A.6. Reuse class `Layanan` (kerja sama antar-modul)

`Transaksi` **tidak** membuat ulang class layanan, tetapi memakai
`module.servicecatalog.Layanan` (punya Shellyn). Ini contoh **agregasi**:
`Transaksi` "memilih" sebuah `Layanan` dan memanggil `layanan.getHargaPerKg()`.

---

# BAGIAN B — Fitur Web (Menu "Pembayaran")

Versi web dari modul di atas, terpasang di aplikasi Spring Boot nyata, memakai
**database** (transaksi asli dari menu Order). Inilah yang membuat tombol
"Pembayaran" di sidebar **hidup**.

## B.1. Arsitektur: pola MVC berlapis

Spring memakai pola **MVC** (Model–View–Controller) yang dipecah jadi lapisan:

```
 Browser
   │  (HTTP request: GET /payments  atau  POST /payments/3/pay)
   ▼
 Controller  ── PaymentController   (terima request, atur alur, pilih halaman)
   │  panggil
   ▼
 Service     ── PaymentService      (LOGIKA BISNIS: cari, hitung, proses bayar)
   │  panggil
   ▼
 Repository  ── TransaksiRepository (akses database, otomatis dibuat Spring Data JPA)
   │
   ▼
 Database (tabel transaksi)  ←→  Entity Transaksi (gambaran 1 baris tabel)
   │
   ▼
 View        ── templates/payment/index.html  (Thymeleaf merakit HTML → kirim ke browser)
```

Aturan mudahnya: **Controller** tipis (cuma "polisi lalu lintas"), **Service**
berisi otak logika, **Repository** urusan database, **Template** urusan tampilan.

## B.2. Konsep / teknologi Spring yang dipakai

| Anotasi / fitur | Dipakai di | Fungsinya |
|-----------------|-----------|-----------|
| `@Service` | `PaymentService` | Menandai class berisi logika bisnis (dikelola Spring) |
| `@Controller` | `PaymentController` | Menandai class penerima request web |
| **Constructor Injection** | constructor kedua class | Spring otomatis "menyuntikkan" objek yang dibutuhkan (Dependency Injection) |
| `@RequestMapping("/payments")` | `PaymentController` | Awalan URL untuk semua method di class itu |
| `@GetMapping` / `@PostMapping` | method controller | Menangani request GET / POST |
| `@RequestParam` | parameter `search` | Mengambil nilai dari query string (`?search=...`) |
| `@PathVariable` | parameter `id` | Mengambil bagian URL (`/payments/{id}/pay`) |
| `Model` | `index()` | Wadah data yang dikirim ke template |
| `RedirectAttributes` (flash) | `pay()` | Pesan sukses/gagal yang muncul sekali setelah redirect |
| `@Transactional` | `prosesPembayaran()` | Operasi tulis DB jadi satu kesatuan (semua sukses, atau batal semua) |
| **Spring Data JPA derived query** | `TransaksiRepository` | Method dibuatkan Spring **otomatis** dari namanya |
| **Thymeleaf** | `payment/index.html` | Mesin template: menyatukan data + HTML |
| **CSRF protection** | form di template | Token keamanan otomatis disisipkan Thymeleaf |

## B.3. Entity `Transaksi` (JPA) + enum `PaymentStatus`

`app/models/Transaksi.java` adalah gambaran satu baris tabel `transaksi`. Berbeda
dari modul standalone, di sini uang pakai **`BigDecimal`** (bukan `double`) supaya
akurat untuk uang. Field penting yang dipakai fitur Pembayaran:

```
invoiceNumber : String        // nomor invoice unik
pelanggan     : Pelanggan      // relasi @ManyToOne
layanan       : Layanan        // relasi @ManyToOne
subtotal, diskon, totalBayar : BigDecimal
paymentStatus : PaymentStatus  // enum: BELUM_LUNAS / LUNAS
paidAt        : LocalDateTime  // waktu dibayar (diisi saat LUNAS)
```

`PaymentStatus` hanya punya 2 nilai: `BELUM_LUNAS` dan `LUNAS`.

> **Kenapa `BigDecimal` di web tapi `double` di modul?** `double` bisa membulatkan
> tidak presisi (mis. `0.1 + 0.2 != 0.3`). Untuk uang sungguhan (di database) dipakai
> `BigDecimal` yang presisi. Di modul demo, `double` dipilih biar **mudah dipahami pemula**.

## B.4. `TransaksiRepository` — query yang dipakai ulang

Tidak ada query baru dibuat. Saya **pakai ulang** method yang sudah ada. Spring Data
JPA membuat isi method ini **otomatis** dari namanya:

| Method | Arti dari namanya |
|--------|-------------------|
| `findAllByOrderByCreatedAtDesc()` | ambil semua, urut terbaru dulu |
| `findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc(a, b)` | cari yang invoice **atau** nama pelanggan mengandung kata kunci (abaikan besar/kecil) → ini = `cariByInvoice` + `cariByPelanggan` versi web |
| `findByPaymentStatus(status)` | ambil semua dengan status tertentu |
| `countByPaymentStatus(status)` | hitung jumlah dengan status tertentu |
| `findById(id)` | ambil satu berdasarkan id |
| `save(entity)` | simpan/update ke database |

## B.5. `PaymentService` — "BillingManager versi web"

Class otak logika. Tiap method dan padanannya di modul standalone:

| Method (web) | Cara kerja | Padanan di modul |
|--------------|-----------|------------------|
| `cariTransaksi(kataKunci)` | kalau kosong → semua transaksi; kalau ada isi → cari invoice/nama | `cariByInvoice` + `cariByPelanggan` |
| `hitungTotalPendapatan()` | loop semua transaksi LUNAS, jumlahkan `totalBayar` | `hitungTotalPendapatan` |
| `jumlahLunas()` / `jumlahBelumLunas()` | `countByPaymentStatus(...)` | (info tambahan) |
| `prosesPembayaran(id)` | ambil transaksi; **kalau sudah LUNAS → berhenti**; selain itu set `LUNAS` + `paidAt = sekarang` + simpan | `BillingManager.prosesPembayaran` |

Contoh inti `prosesPembayaran` (perhatikan penjaga "jangan bayar dua kali", sama
seperti modul standalone):
```java
@Transactional
public void prosesPembayaran(Long id) {
    Transaksi trx = transaksiRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transaksi tidak ditemukan"));
    if (trx.getPaymentStatus() == PaymentStatus.LUNAS) return; // sudah lunas → stop
    trx.setPaymentStatus(PaymentStatus.LUNAS);
    trx.setPaidAt(LocalDateTime.now());
    transaksiRepository.save(trx);
}
```

## B.6. `PaymentController` — pintu masuk request

Dua "pintu" (endpoint):

**1. `GET /payments`** → menampilkan halaman.
```
index(search, model):
    model["transaksiList"]   = paymentService.cariTransaksi(search)
    model["search"]          = search
    model["totalPendapatan"] = paymentService.hitungTotalPendapatan()
    model["jumlahLunas"]     = paymentService.jumlahLunas()
    model["jumlahBelumLunas"]= paymentService.jumlahBelumLunas()
    return "payment/index"   ← nama file template
```

**2. `POST /payments/{id}/pay`** → memproses pembayaran lalu kembali.
```
pay(id):
    coba  → paymentService.prosesPembayaran(id); pesan sukses (flash)
    gagal → pesan error (flash)
    return "redirect:/payments"   ← muat ulang halaman daftar
```
> Pola **POST → redirect → GET** (PRG) mencegah pembayaran ter-submit dua kali saat
> halaman di-refresh.

## B.7. Template `payment/index.html` (Thymeleaf)

Struktur halaman (meniru gaya halaman Order yang sudah ada):

1. **Layout** — memanggil potongan (fragment) bersama: `head`, `sidebar`, `navbar`.
   ```html
   <div th:replace="~{layout/sidebar :: sidebar}"></div>
   ```
2. **Kartu ringkasan** — Total Pendapatan, jumlah Lunas, jumlah Belum Lunas.
   ```html
   <p th:text="${'Rp ' + #numbers.formatDecimal(totalPendapatan, 0, 'COMMA', 0, 'POINT')}">Rp 0</p>
   ```
3. **Kotak pencarian** — form `GET /payments?search=...`.
4. **Tabel transaksi** — perulangan tiap transaksi:
   ```html
   <tr th:each="trx : ${transaksiList}"> ... </tr>
   ```
   - Badge status: `th:if="${trx.paymentStatus.name() == 'LUNAS'}"`.
   - **Tombol Bayar** hanya muncul kalau `BELUM_LUNAS`, berupa form `POST /payments/{id}/pay`.
   - Kalau sudah `LUNAS` → tampil tanda centang "✓ Lunas".
5. **Flash message** — pakai SweetAlert2 (popup) untuk pesan sukses/gagal.

**Keamanan (CSRF):** form memakai `th:action="@{...}"`, sehingga Thymeleaf **otomatis**
menambahkan input tersembunyi `_csrf`. Tanpa token ini, Spring Security menolak POST.
Jadi kita tidak perlu menulis token manual.

## B.8. Menu sidebar diaktifkan

Sebelumnya menu "Pembayaran" adalah `<span>` mati (disabled). Diubah menjadi link aktif:
```html
<a th:href="@{/payments}"
   th:classappend="${#strings.startsWith(currentPath, '/payments')} ? ' menu-item-active' : ' menu-item-inactive'"
   class="menu-item group"> ... <span class="menu-item-text">Pembayaran</span></a>
```
`th:classappend` membuat menu **menyala** (highlight) saat URL sedang di `/payments`.

## B.9. Keamanan / hak akses

`SecurityConfig` berakhir dengan `.anyRequest().authenticated()`, artinya **semua URL
yang belum diatur khusus** (termasuk `/payments`) **boleh diakses siapa saja yang sudah
login** (ADMIN atau STAFF). Jadi tidak perlu menambah aturan baru di security.

---

## C. Cara Menjalankan & Hasil Pengujian

### C.1. Modul standalone (Layer A)
Compile + jalankan class `Main` di paket `module.payment` (butuh JDK; di mesin ini
dipakai JDK 25 bawaan BurpSuite). Output membuktikan kedua fitur: hitung diskon
otomatis lalu simpan & cari riwayat.

### C.2. Aplikasi web (Layer B)
1. Jalankan database MySQL/MariaDB di `localhost:3306` (db `smart_laundry`).
2. Jalankan: `./mvnw spring-boot:run` (port 8081 bila 8080 dipakai).
3. Login `admin` / `admin123`, klik menu **Pembayaran** di sidebar.

### C.3. Hasil verifikasi nyata (sudah dites end-to-end)
```
GET  /payments              → HTTP 200, judul "Pembayaran - Smart Laundry", tanpa error
     Total Pendapatan        → Rp 39.600 (dihitung oleh PaymentService)
POST /payments/3/pay         → HTTP 302 → redirect /payments  (sukses)
     Lunas: 2 → 3 ,  Total Pendapatan: Rp 39.600 → Rp 69.600  (+Rp 30.000)
```
Artinya: halaman tampil benar, data dari database mengalir, tombol **Bayar** benar-benar
mengubah status transaksi menjadi LUNAS dan ringkasan ikut ter-update.

---

## D. Ringkasan: konsep apa dipakai di mana

| Konsep | Layer A (modul) | Layer B (web) |
|--------|-----------------|----------------|
| Class & Object | `Pelanggan`, `Transaksi` | Entity `Transaksi`, Service, Controller |
| Encapsulation | atribut `private` + getter/setter | sama (Lombok `@Getter/@Setter`) |
| Aggregation/Reuse | `Transaksi` simpan `Pelanggan`+`Layanan` | relasi `@ManyToOne` + reuse `TransaksiRepository` |
| Separation of concerns | data vs `BillingManager` | Controller / Service / Repository / View |
| Diskon member 5% | konstanta `DISKON_MEMBER` | `new BigDecimal("0.05")` di OrderService |
| Cari transaksi | `cariByInvoice` / `cariByPelanggan` | derived query JPA |
| Total pendapatan | `hitungTotalPendapatan()` | `PaymentService.hitungTotalPendapatan()` |
| Proses bayar (anti dobel) | guard `isLunas()` | guard `paymentStatus == LUNAS` |

**Kesimpulan:** logika billing & riwayat transaksi Raihan satu konsep, ditulis dua kali
dengan gaya berbeda — Java polos untuk belajar PBO, dan Spring MVC untuk aplikasi web nyata.

---

## E. Penanda Kurikulum Kuliah (Minggu 01–14) — apa saja & ada berapa

Tabel ini **menandai** tiap konsep mingguan: ada/tidaknya di kode Raihan.
Keterangan status: ✅ = sudah ada, ⚠️ = sebagian / tidak langsung, ❌ = belum ada.

| Minggu | Topik | Status | Ditandai di kode Raihan |
|--------|-------|:------:|--------------------------|
| 01 | Pengenalan OOP (class & object) | ✅ | `Pelanggan`, `Transaksi`, `BillingManager`; objek dibuat di `Main` |
| 02 | Enkapsulasi | ✅ | atribut `private` + getter/setter di semua class; `cekStatusMember()` |
| 03 | Relasi & Diagram Kelas | ✅ | agregasi `Transaksi`→`Pelanggan`+`Layanan`; `BillingManager`→`List<Transaksi>` |
| 04 | Inheritance | ❌ | belum ada `extends` di kode Raihan |
| 05 | Kelas Abstrak & Interface | ❌ | Raihan belum mendefinisikan `abstract`/`interface` sendiri |
| 06 | Static Modifier | ✅ | `Transaksi.DISKON_MEMBER` (`static final`), `Main.main` |
| 06 | Collection | ✅ | `List`/`ArrayList`/`Optional`/`stream` di `BillingManager` |
| 06 | Polimorfisme | ❌ | belum ada `@Override` / overloading |
| 07 | Exception | ⚠️ | ADA di web: `try/catch` (`PaymentController`), `orElseThrow`+`IllegalArgumentException` (`PaymentService`). BELUM di modul standalone (validasi pakai `if`+`println`) |
| 09–11 | MVC | ✅ | `PaymentController` (C) + `PaymentService` (logika/M) + `payment/index.html` (V) |
| 11–13 | JDBC | ⚠️ | dipakai lewat **Spring Data JPA** (`TransaksiRepository`), bukan JDBC mentah (`Connection`/`PreparedStatement`) |

> Minggu 08 (UTS) & Minggu 14 (Suplemen) bukan konsep kode, jadi tidak dihitung.

### Rekap jumlah (dari 11 konsep inti yang dinilai)

- ✅ **Lengkap: 6** → OOP, Enkapsulasi, Relasi, Static, Collection, MVC
- ⚠️ **Sebagian: 2** → Exception (baru ada di web), JDBC (lewat JPA, bukan mentah)
- ❌ **Belum: 3** → Inheritance, Kelas Abstrak/Interface, Polimorfisme

**Total: 6 lengkap + 2 sebagian + 3 belum = 11 konsep.**

Untuk menutup yang ❌ dan ⚠️ sekaligus, usulan: buat **interface/abstract** `MetodePembayaran`
→ diturunkan (**inheritance**) jadi `PembayaranTunai` & `PembayaranQris` yang meng-`@Override`
(**polimorfisme**), plus **custom exception** `PembayaranException` menggantikan `println`
validasi. Itu menutup M04, M05, M06-polimorfisme, dan M07 dalam satu tema laundry yang masuk akal.
