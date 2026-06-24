# Main.java — Demo membuktikan kedua fitur
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/module/payment/Main.java` · Bagian: Raihan (Payment & Billing) · Layer: Modul standalone (Java murni)

## 1. Untuk apa file ini?

File `Main.java` ini adalah **titik mulai program (entry point)** untuk modul Payment & Billing bagian Raihan. Bayangkan ini seperti "tombol play" — kalau kamu menjalankan file ini, dia akan memperagakan (demo) seluruh fitur yang dibuat di modul ini, dari awal sampai akhir, lengkap dengan hasil cetakan di layar (console/terminal).

Kenapa file seperti ini perlu ada? Karena modul ini adalah **modul standalone** (berdiri sendiri, pakai Java murni, belum nyambung ke aplikasi web). Saat tugas kuliah PBO, kita perlu cara cepat untuk **membuktikan** bahwa kode kita benar-benar jalan dan menghasilkan angka yang benar, tanpa harus membuka aplikasi web yang besar. Jadi `Main.java` ini berperan sebagai "skenario pertunjukan": dia membuat data contoh (2 layanan + 2 pelanggan), lalu menjalankan dua fitur utama:

1. **Fitur 1 – Billing Logic:** menghitung biaya laundry secara otomatis (termasuk diskon member 5%).
2. **Fitur 2 – Transaction History:** menyimpan semua transaksi lalu mencarinya kembali (cari lewat nomor invoice dan cari lewat nama pelanggan).

Class ini hanya punya satu method, yaitu `main(...)`. Method `main` adalah method khusus di Java yang otomatis dipanggil pertama kali ketika program dijalankan. Jadi semua kode di dalam file ini dijalankan dari atas ke bawah secara berurutan.

> Catatan untuk pemula: file ini **tidak menyimpan data** dan **tidak punya logika perhitungan sendiri**. Tugasnya hanya "menyutradarai" — memanggil class lain (`BillingManager`, `Transaksi`, `Pelanggan`, `Layanan`) dan menampilkan hasilnya. Otak perhitungan ada di file lain.

## 2. Kode lengkap

```java
package com.laundry.smartlaundry.module.payment;

import java.util.List;
import java.util.Optional;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;

// Demo modul Payment & Billing (bagian Raihan). Jalankan ini buat lihat kedua fitur.
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEM MANAJEMEN OPERASIONAL LAUNDRY DIGITAL ===");
        System.out.println("Modul: Payment & Billing (Bagian: Raihan)\n");

        BillingManager billing = new BillingManager();

        // data layanan (pinjam dari modul Shellyn) & 2 pelanggan: 1 member, 1 bukan
        Layanan cuciSetrika = new Layanan("LYN-02", "Cuci Setrika", 8000.0, 3);
        Layanan paketExpress = new Layanan("LYN-03", "Express 1 Hari", 12000.0, 1);

        Pelanggan andi = new Pelanggan("MBR-01", "Andi Wijaya", "081234567890", true);
        Pelanggan budi = new Pelanggan("MBR-02", "Budi Santoso", "082199998888", false);

        // --- Fitur 1: hitung biaya otomatis ---
        System.out.println(">>> FITUR 1: BILLING LOGIC (perhitungan biaya otomatis)\n");

        System.out.println("[Skenario] Andi (member) menyetrika 3 kg pakaian.");
        Transaksi trxAndi = billing.buatTagihan("INV-001", andi, cuciSetrika, 3.0, "2026-06-24");

        System.out.println("\n[Skenario] Budi (bukan member) ambil paket Express 2 kg.");
        Transaksi trxBudi = billing.buatTagihan("INV-002", budi, paketExpress, 2.0, "2026-06-24");

        // bayar keduanya, lalu cetak struk Andi
        System.out.println("\n>>> PROSES PEMBAYARAN\n");
        billing.prosesPembayaran(trxAndi);
        billing.prosesPembayaran(trxBudi);

        System.out.println("\n[Skenario] Andi minta dicetakkan struk:");
        trxAndi.cetakStruk();

        // --- Fitur 2: simpan & cari ---
        System.out.println("\n>>> FITUR 2: TRANSACTION HISTORY (penyimpanan & pencarian)\n");

        billing.tampilkanSemuaRiwayat();

        // cari lewat invoice
        System.out.println("\n[Skenario] Cari transaksi dengan invoice 'INV-002':");
        Optional<Transaksi> hasilInvoice = billing.cariByInvoice("INV-002");
        if (hasilInvoice.isPresent()) {
            Transaksi t = hasilInvoice.get();
            System.out.println("Ditemukan: " + t.getPelanggan().getNama()
                    + " - Rp" + String.format("%,.2f", t.getTotalBayar()));
        } else {
            System.out.println("Invoice tidak ditemukan.");
        }

        // cari lewat nama
        System.out.println("\n[Skenario] Cari riwayat transaksi atas nama 'Andi':");
        List<Transaksi> hasilNama = billing.cariByPelanggan("Andi");
        if (hasilNama.isEmpty()) {
            System.out.println("Tidak ada transaksi atas nama tersebut.");
        } else {
            for (Transaksi t : hasilNama) {
                System.out.println("Ditemukan: " + t.getIdOrder()
                        + " - Rp" + String.format("%,.2f", t.getTotalBayar()));
            }
        }

        System.out.println("\n=== DEMONSTRASI MODUL SELESAI ===");
    }
}

```

## 3. Penjelasan detail per bagian

File ini cuma punya **satu class** (`Main`) dengan **satu method** (`main`). Tapi di dalam `main` ada banyak langkah penting. Supaya gampang dipelajari, kita pecah jadi beberapa bagian.

### Bagian: `package` dan `import`

```java
package com.laundry.smartlaundry.module.payment;

import java.util.List;
import java.util.Optional;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;
```

- **Untuk apa:** Baris `package` menentukan "alamat folder" tempat file ini tinggal (`...module.payment`). Baris `import` adalah cara memberitahu Java: "tolong sediakan class-class dari tempat lain supaya bisa dipakai di sini."
- **Cara kerja:**
  - `import java.util.List;` → memungkinkan kita pakai tipe `List` (daftar/koleksi banyak data). Dipakai nanti untuk menampung hasil pencarian by nama.
  - `import java.util.Optional;` → memungkinkan kita pakai `Optional`, yaitu "kotak" yang isinya bisa ada atau bisa kosong. Dipakai untuk hasil pencarian by invoice (karena invoice bisa ketemu atau tidak).
  - `import com.laundry.smartlaundry.module.servicecatalog.Layanan;` → meminjam class `Layanan` dari **modul lain** (modul Service Catalog, bagian Shellyn). Ini bukti modul Raihan kerja sama dengan modul teman.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dibaca oleh compiler Java sebelum program jalan. Tanpa import ini, baris `Optional<Transaksi> ...`, `List<Transaksi> ...`, dan `new Layanan(...)` akan error.
- **Data masuk → keluar:** Tidak ada (ini bukan method, hanya deklarasi).

### Bagian: deklarasi class dan method `main`

```java
public class Main {
    public static void main(String[] args) {
```

- **Untuk apa:** `main` adalah method spesial yang dicari Java saat program dijalankan. Inilah pintu masuk eksekusi.
- **Cara kerja:**
  - `public` → bisa diakses dari luar (wajib untuk `main`).
  - `static` → bisa dijalankan tanpa harus membuat objek `Main` dulu.
  - `void` → method ini tidak mengembalikan nilai apa pun.
  - `String[] args` → tempat menampung argumen dari command line. Di file ini `args` **tidak dipakai sama sekali**, tapi tetap wajib ditulis karena itu aturan baku method `main` di Java.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil otomatis oleh JVM (mesin Java) ketika kita menjalankan class `Main`. Tidak ada method lain yang memanggilnya secara manual.
- **Data masuk → keluar:** Masuk: `args` (array string, di sini diabaikan). Keluar: tidak ada (`void`).

### Bagian: judul/header ke layar

```java
System.out.println("=== SISTEM MANAJEMEN OPERASIONAL LAUNDRY DIGITAL ===");
System.out.println("Modul: Payment & Billing (Bagian: Raihan)\n");
```

- **Untuk apa:** Mencetak judul demo supaya orang yang menjalankan tahu ini program apa dan punya siapa.
- **Cara kerja:** `System.out.println(...)` menulis teks ke console lalu pindah baris. Karakter `\n` menambah satu baris kosong ekstra biar tampilan lebih rapi.
- **Nanti dipakai untuk apa / dipanggil siapa:** Hanya tampilan; tidak memengaruhi logika.
- **Data masuk → keluar:** Masuk: teks. Keluar: tulisan di layar.

### Bagian: membuat `BillingManager`

```java
BillingManager billing = new BillingManager();
```

- **Untuk apa:** Membuat satu objek `BillingManager`, yaitu "manajer" yang mengurus pembuatan tagihan, pembayaran, penyimpanan riwayat, dan pencarian.
- **Cara kerja:** `new BillingManager()` memanggil constructor `BillingManager`. Hasilnya disimpan ke variabel bernama `billing`. Mulai sekarang, semua perintah billing dilakukan lewat variabel `billing` ini.
- **Nanti dipakai untuk apa / dipanggil siapa:** Variabel `billing` akan dipakai berkali-kali di bawah: `billing.buatTagihan(...)`, `billing.prosesPembayaran(...)`, `billing.tampilkanSemuaRiwayat()`, `billing.cariByInvoice(...)`, `billing.cariByPelanggan(...)`.
- **Data masuk → keluar:** Masuk: tidak ada. Keluar: objek `BillingManager` baru.

### Bagian: membuat data Layanan

```java
Layanan cuciSetrika = new Layanan("LYN-02", "Cuci Setrika", 8000.0, 3);
Layanan paketExpress = new Layanan("LYN-03", "Express 1 Hari", 12000.0, 1);
```

- **Untuk apa:** Menyiapkan dua jenis layanan laundry contoh yang akan dibeli pelanggan.
- **Cara kerja:** Memanggil constructor `Layanan` dari modul Shellyn. Berdasarkan urutan nilai yang dikirim: kode layanan (`"LYN-02"`), nama layanan (`"Cuci Setrika"`), harga per satuan (`8000.0`, bertipe `double`), dan satu angka lagi (`3` / `1`). Harga `8000.0` ditulis pakai titik desimal karena **modul standalone memakai uang bertipe `double`** (berbeda dengan layer web yang pakai `BigDecimal`).
- **Nanti dipakai untuk apa / dipanggil siapa:** `cuciSetrika` dipakai untuk tagihan Andi; `paketExpress` dipakai untuk tagihan Budi. Keduanya dikirim sebagai parameter ke `billing.buatTagihan(...)`.
- **Data masuk → keluar:** Masuk: kode, nama, harga, dan satu nilai numerik. Keluar: dua objek `Layanan`.

### Bagian: membuat data Pelanggan

```java
Pelanggan andi = new Pelanggan("MBR-01", "Andi Wijaya", "081234567890", true);
Pelanggan budi = new Pelanggan("MBR-02", "Budi Santoso", "082199998888", false);
```

- **Untuk apa:** Menyiapkan dua pelanggan contoh — satu member, satu bukan member — supaya bisa membandingkan apakah diskon benar-benar bekerja.
- **Cara kerja:** Memanggil constructor `Pelanggan` dengan urutan: id (`"MBR-01"`), nama (`"Andi Wijaya"`), nomor HP (`"081234567890"`), dan status member berupa `boolean`. Andi `true` (member), Budi `false` (bukan member). Status `true/false` inilah yang nanti menentukan dapat diskon 5% atau tidak.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dikirim ke `billing.buatTagihan(...)` sebagai pemilik transaksi. Status member dipakai oleh logika billing untuk menghitung diskon.
- **Data masuk → keluar:** Masuk: id, nama, no HP, status member. Keluar: dua objek `Pelanggan`.

### Bagian: Fitur 1 — membuat tagihan (`buatTagihan`)

```java
System.out.println("[Skenario] Andi (member) menyetrika 3 kg pakaian.");
Transaksi trxAndi = billing.buatTagihan("INV-001", andi, cuciSetrika, 3.0, "2026-06-24");

System.out.println("\n[Skenario] Budi (bukan member) ambil paket Express 2 kg.");
Transaksi trxBudi = billing.buatTagihan("INV-002", budi, paketExpress, 2.0, "2026-06-24");
```

- **Untuk apa:** Inti dari Fitur 1. Membuat dua tagihan: satu untuk Andi (member), satu untuk Budi (bukan member). Di sinilah biaya dihitung otomatis.
- **Cara kerja:** Memanggil `billing.buatTagihan(...)` dengan parameter berurutan: nomor invoice, objek pelanggan, objek layanan, jumlah/berat (`double`, mis. `3.0` kg), dan tanggal (`String`). `BillingManager` akan membuat objek `Transaksi`, menghitung subtotal dan diskon, lalu mengembalikan objek `Transaksi` itu. Hasilnya disimpan ke `trxAndi` dan `trxBudi`.
- **Nanti dipakai untuk apa / dipanggil siapa:** `trxAndi` dan `trxBudi` dipakai untuk langkah berikutnya: diproses pembayarannya, dicetak struknya, dan disimpan ke riwayat.
- **Data masuk → keluar:** Masuk: invoice, pelanggan, layanan, jumlah, tanggal. Keluar: objek `Transaksi`.

> Penting (aturan diskon): pelanggan member mendapat **diskon 5%** (konstanta `DISKON_MEMBER = 0.05` di logika billing). Bukan 10%. Andi member → dapat diskon; Budi bukan member → tidak dapat diskon. Tidak ada fitur poin loyalitas di modul ini.

### Bagian: proses pembayaran (`prosesPembayaran`)

```java
billing.prosesPembayaran(trxAndi);
billing.prosesPembayaran(trxBudi);
```

- **Untuk apa:** Menandai kedua transaksi sebagai sudah dibayar (mengubah status pembayaran transaksi).
- **Cara kerja:** Memanggil `billing.prosesPembayaran(...)` dengan mengirim objek transaksi yang tadi dibuat. Manajer billing akan memproses pembayaran transaksi tersebut dan biasanya mencetak konfirmasi ke layar.
- **Nanti dipakai untuk apa / dipanggil siapa:** Setelah ini, transaksi dianggap lunas, sehingga saat struk dicetak statusnya sudah benar.
- **Data masuk → keluar:** Masuk: objek `Transaksi`. Keluar: tidak ada nilai (efeknya mengubah status transaksi + cetakan layar).

### Bagian: cetak struk (`cetakStruk`)

```java
System.out.println("\n[Skenario] Andi minta dicetakkan struk:");
trxAndi.cetakStruk();
```

- **Untuk apa:** Menampilkan struk/rincian transaksi Andi ke layar (nama, layanan, subtotal, diskon, total, dsb).
- **Cara kerja:** Memanggil method `cetakStruk()` langsung pada objek `trxAndi` (objek `Transaksi`). Perhatikan: ini dipanggil pada `trxAndi`, **bukan** pada `billing`, karena yang punya data detail strukn adalah objek transaksi itu sendiri.
- **Nanti dipakai untuk apa / dipanggil siapa:** Hanya untuk ditampilkan ke pengguna sebagai bukti rincian biaya.
- **Data masuk → keluar:** Masuk: tidak ada. Keluar: tidak ada (efeknya cetakan struk di layar).

### Bagian: Fitur 2 — tampilkan semua riwayat (`tampilkanSemuaRiwayat`)

```java
billing.tampilkanSemuaRiwayat();
```

- **Untuk apa:** Awal Fitur 2. Menampilkan seluruh transaksi yang sudah tersimpan di `BillingManager`.
- **Cara kerja:** Memanggil `billing.tampilkanSemuaRiwayat()`. Manajer billing akan menelusuri daftar transaksi yang sudah ia simpan (Andi + Budi) lalu mencetak ringkasannya.
- **Nanti dipakai untuk apa / dipanggil siapa:** Membuktikan bahwa transaksi benar-benar tersimpan setelah dibuat, sebelum dicari.
- **Data masuk → keluar:** Masuk: tidak ada. Keluar: tidak ada (efeknya cetakan daftar di layar).

### Bagian: cari transaksi by invoice (`cariByInvoice` + `Optional`)

```java
System.out.println("\n[Skenario] Cari transaksi dengan invoice 'INV-002':");
Optional<Transaksi> hasilInvoice = billing.cariByInvoice("INV-002");
if (hasilInvoice.isPresent()) {
    Transaksi t = hasilInvoice.get();
    System.out.println("Ditemukan: " + t.getPelanggan().getNama()
            + " - Rp" + String.format("%,.2f", t.getTotalBayar()));
} else {
    System.out.println("Invoice tidak ditemukan.");
}
```

- **Untuk apa:** Mencari satu transaksi berdasarkan nomor invoice (`"INV-002"`, milik Budi).
- **Cara kerja:**
  - `billing.cariByInvoice("INV-002")` mengembalikan `Optional<Transaksi>` — "kotak" yang isinya bisa ada atau kosong.
  - `hasilInvoice.isPresent()` mengecek apakah kotaknya berisi. Kalau ada (`true`), `hasilInvoice.get()` mengambil objek `Transaksi`-nya dan disimpan ke variabel `t`.
  - Lalu mencetak nama pelanggan via `t.getPelanggan().getNama()` (mengambil objek pelanggan dari transaksi, lalu mengambil namanya) dan total bayar via `t.getTotalBayar()`.
  - `String.format("%,.2f", ...)` memformat angka uang: `%,` menambahkan pemisah ribuan, dan `.2f` menampilkan 2 angka di belakang koma. Contoh: `24000.0` jadi `24,000.00`.
  - Kalau kotaknya kosong, dijalankan blok `else`: mencetak "Invoice tidak ditemukan."
- **Nanti dipakai untuk apa / dipanggil siapa:** Membuktikan fitur pencarian invoice bekerja, baik saat ketemu maupun tidak ketemu.
- **Data masuk → keluar:** Masuk: nomor invoice (`String`). Keluar dari `cariByInvoice`: `Optional<Transaksi>` (boleh kosong).

### Bagian: cari transaksi by nama pelanggan (`cariByPelanggan` + `List`)

```java
System.out.println("\n[Skenario] Cari riwayat transaksi atas nama 'Andi':");
List<Transaksi> hasilNama = billing.cariByPelanggan("Andi");
if (hasilNama.isEmpty()) {
    System.out.println("Tidak ada transaksi atas nama tersebut.");
} else {
    for (Transaksi t : hasilNama) {
        System.out.println("Ditemukan: " + t.getIdOrder()
                + " - Rp" + String.format("%,.2f", t.getTotalBayar()));
    }
}
```

- **Untuk apa:** Mencari semua transaksi yang nama pelanggannya cocok dengan `"Andi"`. Karena satu nama bisa punya banyak transaksi, hasilnya berupa `List` (daftar), bukan satu objek.
- **Cara kerja:**
  - `billing.cariByPelanggan("Andi")` mengembalikan `List<Transaksi>` (bisa kosong, bisa berisi banyak).
  - `hasilNama.isEmpty()` mengecek apakah daftarnya kosong. Kalau kosong → cetak "Tidak ada transaksi atas nama tersebut."
  - Kalau ada isinya, `for (Transaksi t : hasilNama)` adalah loop yang berjalan satu kali untuk setiap transaksi di daftar. Untuk tiap `t`, dicetak id order via `t.getIdOrder()` dan total bayar via `t.getTotalBayar()` (diformat dengan `String.format("%,.2f", ...)`).
- **Nanti dipakai untuk apa / dipanggil siapa:** Membuktikan fitur pencarian berdasarkan nama dapat menemukan dan menampilkan transaksi-transaksi yang cocok.
- **Data masuk → keluar:** Masuk: potongan nama (`String`). Keluar dari `cariByPelanggan`: `List<Transaksi>` (boleh kosong).

### Bagian: penutup

```java
System.out.println("\n=== DEMONSTRASI MODUL SELESAI ===");
```

- **Untuk apa:** Menandai bahwa seluruh demo sudah selesai dijalankan.
- **Cara kerja:** Mencetak teks penutup ke layar.
- **Nanti dipakai untuk apa / dipanggil siapa:** Sekadar penanda akhir; setelah baris ini method `main` berakhir dan program berhenti.
- **Data masuk → keluar:** Masuk: teks. Keluar: tulisan di layar.

## 4. Contoh alur nyata

Ikuti perjalanan data **Andi** (member) saat program dijalankan:

1. Dibuat objek layanan `cuciSetrika` dengan harga **Rp8.000** per kg, dan objek pelanggan `andi` dengan status member = `true`.
2. Dipanggil `billing.buatTagihan("INV-001", andi, cuciSetrika, 3.0, "2026-06-24")`.
   - Andi menyetrika **3 kg**, harga **Rp8.000/kg**.
   - Subtotal = 3 × 8.000 = **24.000**.
   - Karena Andi member, dapat **diskon 5%** (`DISKON_MEMBER = 0.05`): diskon = 5% × 24.000 = **1.200**.
   - Total = 24.000 − 1.200 = **22.800**.
   - Method mengembalikan objek `Transaksi` yang disimpan ke `trxAndi`.
3. Dipanggil `billing.prosesPembayaran(trxAndi)` → status transaksi Andi jadi "sudah dibayar".
4. Dipanggil `trxAndi.cetakStruk()` → menampilkan rincian: subtotal 24.000, diskon 1.200, total 22.800.
5. Saat pencarian by nama `billing.cariByPelanggan("Andi")` dijalankan, transaksi Andi ditemukan, lalu dicetak:
   `Ditemukan: INV-001 - Rp22,800.00` (angka diformat oleh `String.format("%,.2f", ...)`).

Sebagai pembanding, **Budi** bukan member (status `false`): paket Express **Rp12.000**, 2 kg → subtotal 24.000, **tidak ada diskon**, total tetap **24.000**. Saat dicari lewat invoice `"INV-002"`, hasilnya `Ditemukan: Budi Santoso - Rp24,000.00`.

> Inilah inti pembuktiannya: dengan subtotal sama-sama 24.000, Andi (member) bayar 22.800 sedangkan Budi (bukan member) bayar 24.000. Selisih 1.200 = persis diskon 5% — bukti logika diskon member bekerja.

## 5. Hubungan dengan file lain

File `Main.java` ini posisinya paling "atas" (pemanggil), dia menggunakan banyak file lain tapi **tidak ada file lain yang memanggilnya** (kecuali JVM saat program dijalankan):

- **Memakai `BillingManager`** (`module/payment/BillingManager.java`): semua aksi billing lewat objek `billing`, yaitu `buatTagihan(...)`, `prosesPembayaran(...)`, `tampilkanSemuaRiwayat()`, `cariByInvoice(...)`, dan `cariByPelanggan(...)`. `BillingManager` inilah otak yang menyimpan riwayat dan menghitung biaya/diskon.
- **Memakai `Transaksi`** (`module/payment/Transaksi.java`): hasil dari `buatTagihan(...)` adalah objek `Transaksi`. `Main` memanggil method-nya secara langsung seperti `cetakStruk()`, `getPelanggan()`, `getTotalBayar()`, dan `getIdOrder()`.
- **Memakai `Pelanggan`** (`module/payment/Pelanggan.java`): dibuat objek `andi` dan `budi`. Status member (`boolean`) di sini menentukan diskon. Method `getNama()` dipanggil saat menampilkan hasil pencarian invoice.
- **Memakai `Layanan`** (`module/servicecatalog/Layanan.java`, modul Shellyn): "dipinjam" lewat `import`. Menyediakan data harga & nama layanan yang dipakai untuk menghitung subtotal. Ini bukti modul Payment & Billing (Raihan) berinteraksi dengan modul Service Catalog (Shellyn).
- **Memakai kelas bawaan Java**: `Optional` (hasil `cariByInvoice`) dan `List` (hasil `cariByPelanggan`) dari `java.util`, serta `String.format(...)` untuk memformat angka uang.

Catatan layer: file-file di atas adalah **modul standalone** (Java murni) sehingga uang memakai tipe `double`. Berbeda dengan versi di **layer web** aplikasi yang memakai `BigDecimal` untuk uang dan dipanggil melalui rantai Controller → Service. `Main.java` ini hanya hidup di dunia modul standalone, sebagai demo/pembuktian, bukan bagian dari alur web.
