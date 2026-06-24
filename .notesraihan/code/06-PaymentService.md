# PaymentService.java — Logika pembayaran versi web (mirip BillingManager)
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/app/services/payment/PaymentService.java` · Bagian: Raihan (Payment & Billing) · Layer: Web — Service (Spring + JPA)

## 1. Untuk apa file ini?

Bayangkan kamu lagi bikin aplikasi laundry yang jalan di browser. Ada halaman "Pembayaran" yang menampilkan daftar transaksi, ringkasan pendapatan, dan tombol **Bayar** untuk menandai sebuah transaksi jadi **LUNAS**. Nah, file `PaymentService.java` ini adalah **otak / mesin logika** di balik halaman itu.

Beberapa hal penting biar kamu paham posisinya:

- Ini adalah sebuah **class** Java bernama `PaymentService`, ditandai dengan anotasi `@Service`. Anotasi `@Service` artinya: "Hai Spring, class ini berisi logika bisnis, tolong buatkan satu objeknya dan kelola otomatis." Jadi kita tidak pernah menulis `new PaymentService(...)` sendiri — Spring yang membuatnya.
- Tugas class ini **bukan** menggambar halaman (itu tugas HTML/template) dan **bukan** menerima klik tombol dari browser (itu tugas `PaymentController`). Tugasnya murni **mengolah data pembayaran**: mencari transaksi, menghitung total pendapatan, menghitung jumlah yang sudah/belum lunas, dan memproses pembayaran.
- Class ini "ngobrol" dengan database lewat sebuah perantara bernama `TransaksiRepository`. Service tidak menulis query SQL mentah; ia cukup memanggil method-method di repository.

Di proyek ini ada **dua versi** kode pembayaran:
1. **Modul standalone** (versi sederhana, dipakai untuk latihan PBO murni) yang memakai class seperti `BillingManager` dan menyimpan uang dalam tipe `double`.
2. **Layer web** (versi aplikasi Spring sungguhan) — yaitu file inilah. Versi web memakai tipe **`BigDecimal`** untuk uang, bukan `double`. `BigDecimal` dipilih karena lebih akurat untuk hitungan uang (tidak ada error pembulatan aneh seperti pada `double`).

Komentar di baris 14 kode pun menegaskan ini: *"Logika pembayaran versi web. Mirip BillingManager di modul standalone."* Jadi `PaymentService` adalah "sepupu web" dari `BillingManager`.

> Catatan: di file **ini sendiri tidak ada perhitungan diskon**. Perhitungan subtotal dan diskon member (5%) terjadi saat transaksi dibuat, lalu hasilnya disimpan di kolom `subtotal`, `diskon`, dan `totalBayar` milik objek `Transaksi`. `PaymentService` di sini hanya **menjumlahkan `totalBayar`** dan **mengubah status menjadi LUNAS**.

## 2. Kode lengkap

```java
package com.laundry.smartlaundry.app.services.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.laundry.smartlaundry.app.enums.PaymentStatus;
import com.laundry.smartlaundry.app.models.Transaksi;
import com.laundry.smartlaundry.app.repositories.TransaksiRepository;

// Logika pembayaran versi web. Mirip BillingManager di modul standalone.
@Service
public class PaymentService {

    private final TransaksiRepository transaksiRepository;

    public PaymentService(TransaksiRepository transaksiRepository) {
        this.transaksiRepository = transaksiRepository;
    }

    // kosong -> tampilkan semua; ada isi -> cari lewat invoice / nama
    public List<Transaksi> cariTransaksi(String kataKunci) {
        if (kataKunci == null || kataKunci.isBlank()) {
            return transaksiRepository.findAllByOrderByCreatedAtDesc();
        }
        return transaksiRepository
                .findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc(
                        kataKunci, kataKunci);
    }

    // total semua transaksi yang lunas
    public BigDecimal hitungTotalPendapatan() {
        BigDecimal total = BigDecimal.ZERO;
        for (Transaksi trx : transaksiRepository.findByPaymentStatus(PaymentStatus.LUNAS)) {
            total = total.add(trx.getTotalBayar());
        }
        return total;
    }

    public long jumlahLunas() {
        return transaksiRepository.countByPaymentStatus(PaymentStatus.LUNAS);
    }

    public long jumlahBelumLunas() {
        return transaksiRepository.countByPaymentStatus(PaymentStatus.BELUM_LUNAS);
    }

    // tandai transaksi jadi lunas (sekali aja)
    @Transactional
    public void prosesPembayaran(Long id) {
        Transaksi trx = transaksiRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Transaksi tidak ditemukan"));

        if (trx.getPaymentStatus() == PaymentStatus.LUNAS) {
            return; // udah lunas, stop
        }

        trx.setPaymentStatus(PaymentStatus.LUNAS);
        trx.setPaidAt(LocalDateTime.now());
        transaksiRepository.save(trx);
    }
}

```

## 3. Penjelasan detail per bagian

### a. Bagian `package` dan `import`

```java
package com.laundry.smartlaundry.app.services.payment;
```

- **Untuk apa:** menyatakan "alamat folder" tempat class ini tinggal, yaitu di paket `...app.services.payment`. Paket ini ibarat map/folder yang mengelompokkan kode menurut perannya (`services` = lapisan logika bisnis).
- **Cara kerja:** baris `package` selalu jadi baris pertama. Lalu daftar `import` di bawahnya "memanggil" class dari tempat lain agar bisa dipakai di file ini.
- **Import yang dipakai dan kegunaannya:**
  - `java.math.BigDecimal` → tipe angka presisi tinggi untuk **uang**.
  - `java.time.LocalDateTime` → tipe tanggal+jam, dipakai untuk mencatat **kapan** transaksi dibayar.
  - `java.util.List` → tipe daftar/koleksi, untuk menampung banyak `Transaksi`.
  - `org.springframework.stereotype.Service` → anotasi `@Service`.
  - `org.springframework.transaction.annotation.Transactional` → anotasi `@Transactional`.
  - `...enums.PaymentStatus` → enum status bayar (`BELUM_LUNAS` / `LUNAS`).
  - `...models.Transaksi` → class entitas (1 baris data transaksi).
  - `...repositories.TransaksiRepository` → perantara ke database.

### b. Anotasi `@Service` dan deklarasi class

```java
@Service
public class PaymentService {
```

- **Untuk apa:** menjadikan class ini sebagai **komponen layanan (service bean)** yang dikelola Spring.
- **Cara kerja:** saat aplikasi mulai jalan, Spring memindai kode, melihat `@Service`, lalu otomatis membuat satu objek `PaymentService` dan menyimpannya di "wadah" (container). Objek inilah yang nanti diberikan ke `PaymentController` tanpa kita harus membuatnya manual.
- **Nanti dipakai untuk apa / dipanggil siapa:** objek hasil bentukan Spring ini "disuntikkan" ke `PaymentController` (lihat bagian 5).
- **Data masuk → keluar:** tidak ada (ini deklarasi class, bukan method).

### c. Field `transaksiRepository`

```java
private final TransaksiRepository transaksiRepository;
```

- **Untuk apa:** ini satu-satunya **atribut (field)** di class ini. Isinya adalah "remote control" untuk mengakses tabel `transaksi` di database. Lewat dia kita bisa cari, hitung, dan simpan data transaksi.
- **Cara kerja:**
  - `private` → hanya boleh diakses dari dalam class ini (prinsip *encapsulation* di PBO).
  - `final` → nilainya hanya boleh diisi **satu kali** (di constructor) dan setelah itu tidak bisa diganti. Ini membuat objek lebih aman/stabil.
  - Tipenya `TransaksiRepository`, yaitu sebuah `interface` yang merupakan turunan dari `JpaRepository`. Spring Data JPA otomatis membuatkan implementasi (kode nyata) di belakang layar, jadi kita cukup memanggil method-methodnya.
- **Nanti dipakai untuk apa / dipanggil siapa:** dipakai di hampir semua method (`cariTransaksi`, `hitungTotalPendapatan`, `jumlahLunas`, `jumlahBelumLunas`, `prosesPembayaran`).
- **Data masuk → keluar:** tidak ada (ini penyimpan referensi, bukan method).

### d. Constructor `PaymentService(...)`

```java
public PaymentService(TransaksiRepository transaksiRepository) {
    this.transaksiRepository = transaksiRepository;
}
```

- **Untuk apa:** "pintu masuk" pembuatan objek. Tugasnya mengisi field `transaksiRepository` dengan objek repository yang dikirim dari luar.
- **Cara kerja:** ini contoh **Constructor Injection** (penyuntikan lewat constructor). Saat Spring membuat `PaymentService`, Spring melihat constructor butuh sebuah `TransaksiRepository`, lalu Spring mencari objek repository yang sudah ia siapkan dan menyodorkannya ke parameter `transaksiRepository`. Baris `this.transaksiRepository = transaksiRepository;` menyalin objek itu ke field class (kata kunci `this` membedakan field class dari parameter yang namanya sama).
- **Nanti dipakai untuk apa / dipanggil siapa:** dipanggil otomatis oleh Spring sekali saja saat aplikasi start. Kita tidak memanggilnya manual.
- **Data masuk → keluar:** masuk = objek `TransaksiRepository`. Keluar = objek `PaymentService` yang siap pakai (constructor tidak mengembalikan nilai biasa, tapi menghasilkan instance).

### e. Method `cariTransaksi(String kataKunci)`

```java
public List<Transaksi> cariTransaksi(String kataKunci) {
    if (kataKunci == null || kataKunci.isBlank()) {
        return transaksiRepository.findAllByOrderByCreatedAtDesc();
    }
    return transaksiRepository
            .findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc(
                    kataKunci, kataKunci);
}
```

- **Untuk apa:** mengambil daftar transaksi untuk ditampilkan di halaman. Bisa menampilkan **semua** transaksi, atau hanya yang **cocok** dengan kata kunci pencarian.
- **Cara kerja, langkah demi langkah:**
  1. Cek dulu: `if (kataKunci == null || kataKunci.isBlank())`. Artinya: kalau kata kunci-nya kosong (belum diisi / hanya spasi). `null` = tidak ada nilai sama sekali; `isBlank()` = string kosong atau cuma spasi.
  2. Jika kosong → panggil `findAllByOrderByCreatedAtDesc()`: ambil **semua** transaksi, diurutkan dari yang paling **baru dibuat** ke yang paling lama (`Desc` = menurun berdasarkan `createdAt`).
  3. Jika ada isinya → panggil method panjang `findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc(kataKunci, kataKunci)`. Nama method-nya panjang tapi mudah dibaca jika dipenggal:
     - `findBy InvoiceNumber Containing IgnoreCase` → cari transaksi yang **nomor invoice-nya mengandung** kata kunci, **tanpa peduli huruf besar/kecil**.
     - `Or PelangganNama Containing IgnoreCase` → **ATAU** nama pelanggannya mengandung kata kunci.
     - `OrderByCreatedAtDesc` → hasil diurutkan dari yang terbaru.
     - Kata kunci dikirim **dua kali** karena dipakai untuk dua kolom sekaligus (invoice dan nama).
- **Nanti dipakai untuk apa / dipanggil siapa:** dipanggil oleh `PaymentController.index(...)` lewat `paymentService.cariTransaksi(search)`. Hasilnya dimasukkan ke `model` dengan nama `transaksiList`, lalu ditampilkan sebagai tabel di template `payment/index`.
- **Data masuk → keluar:**
  - Masuk: `String kataKunci` (boleh `null`/kosong).
  - Keluar: `List<Transaksi>` — daftar objek transaksi.

### f. Method `hitungTotalPendapatan()`

```java
public BigDecimal hitungTotalPendapatan() {
    BigDecimal total = BigDecimal.ZERO;
    for (Transaksi trx : transaksiRepository.findByPaymentStatus(PaymentStatus.LUNAS)) {
        total = total.add(trx.getTotalBayar());
    }
    return total;
}
```

- **Untuk apa:** menghitung **total uang masuk** dari semua transaksi yang statusnya sudah **LUNAS**.
- **Cara kerja, langkah demi langkah:**
  1. Buat penampung `total` dan isi awal `BigDecimal.ZERO` (yaitu angka 0 dalam bentuk `BigDecimal`).
  2. `findByPaymentStatus(PaymentStatus.LUNAS)` mengambil **daftar semua transaksi** yang berstatus LUNAS dari database.
  3. Loop `for (Transaksi trx : ...)` berjalan satu per satu pada setiap transaksi lunas.
  4. Untuk tiap transaksi: `total = total.add(trx.getTotalBayar());`. Perhatikan — pada `BigDecimal` kita **tidak boleh** pakai tanda `+`. Penjumlahan dilakukan dengan method `.add(...)`, dan hasilnya **harus** ditampung lagi ke `total` (karena `BigDecimal` bersifat *immutable* / tidak bisa diubah isinya, setiap operasi menghasilkan objek baru).
  5. Setelah loop selesai, `return total;` mengembalikan jumlah akhir.
- **Catatan tipe uang:** di sinilah terlihat layer web memakai **`BigDecimal`** (akurat untuk uang), berbeda dari modul standalone yang memakai `double`.
- **Nanti dipakai untuk apa / dipanggil siapa:** dipanggil `PaymentController.index(...)` dan disimpan ke model sebagai `totalPendapatan`, lalu ditampilkan di kartu ringkasan halaman.
- **Data masuk → keluar:**
  - Masuk: tidak ada parameter.
  - Keluar: `BigDecimal` total pendapatan (0 jika belum ada yang lunas).

### g. Method `jumlahLunas()`

```java
public long jumlahLunas() {
    return transaksiRepository.countByPaymentStatus(PaymentStatus.LUNAS);
}
```

- **Untuk apa:** menghitung **berapa banyak** transaksi yang sudah LUNAS (jumlah baris, bukan jumlah uang).
- **Cara kerja:** langsung memanggil `countByPaymentStatus(PaymentStatus.LUNAS)`. Method `countBy...` ini disediakan otomatis oleh Spring Data JPA dan mengembalikan angka cacah berupa `long`.
- **Nanti dipakai untuk apa / dipanggil siapa:** dipanggil `PaymentController.index(...)`, disimpan ke model sebagai `jumlahLunas`, ditampilkan di ringkasan.
- **Data masuk → keluar:** Masuk: tidak ada. Keluar: `long` (banyak transaksi lunas).

### h. Method `jumlahBelumLunas()`

```java
public long jumlahBelumLunas() {
    return transaksiRepository.countByPaymentStatus(PaymentStatus.BELUM_LUNAS);
}
```

- **Untuk apa:** kebalikan dari method sebelumnya — menghitung banyak transaksi yang **BELUM_LUNAS**.
- **Cara kerja:** memanggil `countByPaymentStatus(PaymentStatus.BELUM_LUNAS)`.
- **Nanti dipakai untuk apa / dipanggil siapa:** dipanggil `PaymentController.index(...)`, disimpan ke model sebagai `jumlahBelumLunas`, ditampilkan di ringkasan.
- **Data masuk → keluar:** Masuk: tidak ada. Keluar: `long` (banyak transaksi belum lunas).

### i. Method `prosesPembayaran(Long id)` (dengan `@Transactional`)

```java
@Transactional
public void prosesPembayaran(Long id) {
    Transaksi trx = transaksiRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Transaksi tidak ditemukan"));

    if (trx.getPaymentStatus() == PaymentStatus.LUNAS) {
        return; // udah lunas, stop
    }

    trx.setPaymentStatus(PaymentStatus.LUNAS);
    trx.setPaidAt(LocalDateTime.now());
    transaksiRepository.save(trx);
}
```

- **Untuk apa:** inilah method paling penting — **menandai sebuah transaksi menjadi LUNAS**, dan memastikan itu hanya terjadi sekali.
- **Anotasi `@Transactional`:** membungkus seluruh isi method dalam satu **transaksi database**. Maksudnya: kalau di tengah jalan ada error, semua perubahan dibatalkan (rollback) agar data tidak setengah jadi. Kalau lancar, semua perubahan disimpan bersamaan.
- **Cara kerja, langkah demi langkah:**
  1. `transaksiRepository.findById(id)` mencari transaksi berdasarkan `id`. Hasilnya berupa `Optional<Transaksi>` (bisa ada, bisa kosong).
  2. `.orElseThrow(() -> new IllegalArgumentException("Transaksi tidak ditemukan"))` → kalau ditemukan, ambil objeknya; kalau **tidak** ada, **lempar error** dengan pesan "Transaksi tidak ditemukan". Bagian `() -> ...` ini disebut *lambda* (cara singkat menulis "fungsi pembuat error").
  3. Cek `if (trx.getPaymentStatus() == PaymentStatus.LUNAS)`. Kalau transaksi **sudah** LUNAS → `return;` langsung berhenti tanpa melakukan apa-apa. Ini **pengaman anti-bayar-ganda** (idempoten): membayar dua kali tidak menggandakan apa pun.
  4. Kalau belum lunas → ubah statusnya: `trx.setPaymentStatus(PaymentStatus.LUNAS);`.
  5. Catat waktu pembayaran sekarang: `trx.setPaidAt(LocalDateTime.now());`.
  6. `transaksiRepository.save(trx);` menyimpan perubahan ke database.
- **Nanti dipakai untuk apa / dipanggil siapa:** dipanggil `PaymentController.pay(...)` saat user menekan tombol **Bayar** (request `POST /payments/{id}/pay`). Jika sukses, controller menampilkan pesan "Pembayaran berhasil! Transaksi sekarang LUNAS."; jika error (misalnya id tidak ada), controller menampilkan pesan gagal.
- **Data masuk → keluar:**
  - Masuk: `Long id` (ID transaksi yang mau dibayar).
  - Keluar: `void` (tidak mengembalikan nilai). Efeknya adalah perubahan data di database (status jadi LUNAS + `paidAt` terisi).

## 4. Contoh alur nyata

Misalkan **Andi** seorang **member**, membawa cucian **3 kg** dengan tarif **Rp8.000/kg**:

1. **Saat transaksi dibuat** (di bagian lain aplikasi, bukan di file ini):
   - Subtotal = 3 kg × Rp8.000 = **Rp24.000**.
   - Karena Andi member, ada **diskon member 5%** → 5% × 24.000 = **Rp1.200**.
   - Total bayar = 24.000 − 1.200 = **Rp22.800**.
   - Nilai-nilai ini disimpan di objek `Transaksi` (kolom `subtotal` = 24.000, `diskon` = 1.200, `totalBayar` = 22.800), dengan `paymentStatus = BELUM_LUNAS`.

2. **Di halaman Pembayaran**, kasir membuka `/payments`:
   - `PaymentController.index(...)` memanggil `cariTransaksi(null)` (karena belum mengetik pencarian) → semua transaksi muncul, termasuk transaksi Andi dengan status BELUM_LUNAS.
   - Ringkasan ikut tampil: `hitungTotalPendapatan()`, `jumlahLunas()`, `jumlahBelumLunas()`.

3. **Kasir mencari "Andi"**:
   - `cariTransaksi("Andi")` memanggil method pencarian `findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc("Andi", "Andi")`.
   - Transaksi Andi muncul karena nama pelanggannya mengandung "Andi" (tidak peduli huruf besar/kecil).

4. **Kasir menekan tombol Bayar** pada transaksi Andi (id misalnya 7):
   - Request `POST /payments/7/pay` → `PaymentController.pay(7, ...)` → `prosesPembayaran(7)`.
   - Di dalam `prosesPembayaran`: transaksi id 7 ditemukan, statusnya masih BELUM_LUNAS, jadi diubah → `LUNAS`, `paidAt` diisi waktu sekarang, lalu `save`.

5. **Halaman dimuat ulang** (redirect ke `/payments`):
   - Sekarang transaksi Andi berstatus LUNAS.
   - `hitungTotalPendapatan()` menjumlahkan semua `totalBayar` yang LUNAS — termasuk **Rp22.800** milik Andi. Jika sebelumnya total pendapatan Rp100.000, sekarang menjadi **Rp122.800**.
   - `jumlahLunas()` bertambah 1, `jumlahBelumLunas()` berkurang 1.

6. **Jika tombol Bayar ditekan lagi** untuk transaksi Andi:
   - `prosesPembayaran(7)` melihat statusnya **sudah** LUNAS → langsung `return`. Tidak ada perubahan, total pendapatan **tidak** bertambah ganda. Aman.

## 5. Hubungan dengan file lain

**File ini MEMAKAI / memanggil:**
- `TransaksiRepository` (`...repositories.TransaksiRepository`) — perantara ke tabel `transaksi`. Method yang dipakai: `findAllByOrderByCreatedAtDesc()`, `findByInvoiceNumberContainingIgnoreCaseOrPelangganNamaContainingIgnoreCaseOrderByCreatedAtDesc(...)`, `findByPaymentStatus(...)`, `countByPaymentStatus(...)`, `findById(...)`, dan `save(...)`.
- `Transaksi` (`...models.Transaksi`) — class entitas satu baris transaksi. Field yang relevan di sini: `totalBayar`, `paymentStatus`, dan `paidAt`. Method getter/setter (`getTotalBayar()`, `getPaymentStatus()`, `setPaymentStatus(...)`, `setPaidAt(...)`) dihasilkan otomatis oleh Lombok (`@Getter`/`@Setter`). Objek `Transaksi` sendiri menghubungkan ke `Pelanggan`, `Layanan`, dan `User` (staff) lewat relasi `@ManyToOne`.
- `PaymentStatus` (`...enums.PaymentStatus`) — enum dengan dua nilai: `BELUM_LUNAS` dan `LUNAS`.
- Kelas bawaan Java: `BigDecimal` (uang), `LocalDateTime` (waktu bayar), `List` (daftar).
- Anotasi Spring: `@Service` dan `@Transactional`.

**File ini DIPAKAI / dipanggil oleh:**
- `PaymentController` (`...controllers.payment.PaymentController`) — pengendali halaman `/payments`. Constructor-nya menerima `PaymentService` (disuntikkan Spring). Method `index(...)` memanggil `cariTransaksi`, `hitungTotalPendapatan`, `jumlahLunas`, `jumlahBelumLunas`; method `pay(...)` memanggil `prosesPembayaran`. Hasilnya dikirim ke template `payment/index` untuk ditampilkan ke user.

**Analoginya dengan struktur PBO yang lain:**
- Di **modul standalone**, `BillingManager` memakai objek `Transaksi`, dan `Transaksi` sendiri memakai `Layanan` & `Pelanggan`. Di **layer web**, `PaymentService` ini berperan sebagai "BillingManager versi web", `PaymentController` memanggil `PaymentService`, dan `PaymentService` memakai `TransaksiRepository` untuk mengakses entitas `Transaksi`. Perbedaan utama: layer web memakai database + `BigDecimal`, sedangkan modul standalone memakai data di memori + `double`.
