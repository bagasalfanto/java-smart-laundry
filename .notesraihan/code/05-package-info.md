# package-info.java — Dokumentasi paket
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/module/payment/package-info.java` · Bagian: Raihan (Payment & Billing) · Layer: Modul standalone (Java murni)

## 1. Untuk apa file ini?

`package-info.java` itu file Java yang **spesial**. Beda dari file biasa, dia **tidak berisi class, atribut, atau method** sama sekali. Tugasnya cuma satu: **memberi dokumentasi (penjelasan) untuk satu paket (package)**.

Bayangkan kamu punya satu folder berisi banyak file kode (`Pelanggan.java`, `Transaksi.java`, `BillingManager.java`, `Main.java`). Nah, supaya orang lain (atau kamu sendiri di masa depan) langsung paham "folder ini isinya tentang apa sih?", kamu bisa menaruh sebuah komentar penjelasan resmi di file `package-info.java`. Inilah fungsinya: jadi **halaman pengantar / sampul depan** untuk seluruh paket `com.laundry.smartlaundry.module.payment`.

Kenapa namanya harus persis `package-info.java`? Karena itu aturan baku dari Java. Compiler dan tool dokumentasi otomatis (namanya **Javadoc**) hanya mengenali file dengan nama itu untuk mengambil deskripsi paket. Kalau diberi nama lain, penjelasannya tidak akan dianggap sebagai dokumentasi paket.

Kapan & di mana dipakai?
- File ini **tidak pernah dijalankan** seperti program. Tidak ada `main()`, tidak ada logika apa pun.
- Dia dibaca oleh **tool Javadoc** ketika kamu membuat dokumentasi HTML otomatis dari kode. Deskripsi di sini akan muncul di halaman ringkasan paket.
- Dia juga membantu **editor/IDE** (seperti VS Code atau IntelliJ): saat kamu mengarahkan kursor ke nama paket, penjelasan ini bisa muncul sebagai tooltip.
- Buat teman sekelas yang baru buka project ini, file ini berfungsi sebagai "peta singkat" isi modul Payment & Billing punya Raihan.

Singkatnya: ini bukan kode yang bekerja, tapi **catatan resmi** yang menempel pada paket.

## 2. Kode lengkap

```java
/**
 * Modul Payment & Billing (bagian Raihan).
 * Dua fitur: Billing Logic (hitung biaya otomatis) dan
 * Transaction History (simpan & cari transaksi lunas).
 * Jalankan Main buat lihat demonya.
 */
package com.laundry.smartlaundry.module.payment;

```

## 3. Penjelasan detail per bagian

File ini cuma punya dua bagian: **blok komentar Javadoc** dan **deklarasi package**. Tidak ada field, constructor, atau method. Jadi kita bahas dua blok itu satu per satu.

### Bagian A — Blok komentar Javadoc (`/** ... */`)

```java
/**
 * Modul Payment & Billing (bagian Raihan).
 * Dua fitur: Billing Logic (hitung biaya otomatis) dan
 * Transaction History (simpan & cari transaksi lunas).
 * Jalankan Main buat lihat demonya.
 */
```

- **Untuk apa:** Ini adalah teks dokumentasi yang menjelaskan isi paket. Perhatikan komentar ini diawali `/**` (dua bintang), **bukan** `/*` (satu bintang) atau `//`. Awalan `/**` itu penting karena menandakan ini **komentar Javadoc** — jenis komentar khusus yang akan ditangkap oleh tool dokumentasi otomatis, bukan komentar biasa yang diabaikan.
- **Cara kerja:** Tiap baris di dalamnya dibuka dengan tanda bintang `*` (ini cuma gaya penulisan rapi, bukan keharusan). Isi teksnya menjelaskan tiga hal:
  1. Baris 1: identitas modul — "Modul Payment & Billing (bagian Raihan)". Memberi tahu pembaca ini wilayah kerja Raihan.
  2. Baris 2-3: menyebut **dua fitur utama** modul ini, yaitu **Billing Logic** (menghitung biaya secara otomatis) dan **Transaction History** (menyimpan serta mencari transaksi yang sudah lunas).
  3. Baris 4: petunjuk praktis — "Jalankan Main buat lihat demonya", artinya kalau mau melihat cara kerjanya, jalankan class `Main`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Komentar ini tidak "dipanggil" oleh kode mana pun (komentar bukan instruksi yang dijalankan). Yang membacanya adalah **tool Javadoc** saat membuat halaman dokumentasi, dan **IDE** saat menampilkan info paket. Karena posisinya tepat sebelum baris `package`, Javadoc otomatis menganggap teks ini sebagai deskripsi resmi untuk paket `com.laundry.smartlaundry.module.payment`.
- **Data masuk → keluar:** Tidak ada. Komentar tidak menerima parameter dan tidak mengembalikan nilai apa pun. Ia murni teks penjelasan.

### Bagian B — Deklarasi package (`package ...;`)

```java
package com.laundry.smartlaundry.module.payment;
```

- **Untuk apa:** Baris ini menyatakan **paket tempat file ini berada**. Di file `package-info.java`, baris `package` inilah satu-satunya baris kode "sungguhan". Tugasnya: menjadi "alamat" yang dipakai Javadoc untuk tahu komentar di atasnya itu milik paket yang mana.
- **Cara kerja:** Nama paket `com.laundry.smartlaundry.module.payment` mengikuti struktur folder: `com/laundry/smartlaundry/module/payment/`. Jadi semua file di folder `payment` (yaitu `Pelanggan.java`, `Transaksi.java`, `BillingManager.java`, `Main.java`, dan `package-info.java` sendiri) sama-sama berada di paket ini. Karena komentar Javadoc di Bagian A ditulis **tepat sebelum** baris `package` ini, komentar tersebut otomatis "menempel" jadi deskripsi paket.
- **Nanti dipakai untuk apa / dipanggil siapa:** Nama paket ini dipakai oleh file lain ketika mereka ingin mengimpor class dari sini. Contohnya, kalau ada file di paket lain butuh class `BillingManager`, ia akan menulis `import com.laundry.smartlaundry.module.payment.BillingManager;`. Jadi deklarasi `package` adalah pondasi sistem penamaan (namespace) di Java.
- **Data masuk → keluar:** Tidak ada parameter dan tidak ada nilai kembalian. Ini deklarasi, bukan method.

## 4. Contoh alur nyata

Karena `package-info.java` tidak mengeksekusi logika apa pun, "alur nyata" di sini bukan soal data yang dihitung, melainkan **bagaimana isinya dipakai sebagai pengantar** sebelum logika sebenarnya berjalan di class lain dalam paket yang sama.

Contoh urutan yang masuk akal saat teman sekelas pertama kali membuka modul Payment & Billing:

1. Teman membuka folder `payment/` dan melihat `package-info.java`. Dari komentarnya ia langsung tahu: "Oh, ini modul Payment & Billing punya Raihan, ada 2 fitur (Billing Logic & Transaction History), dan kalau mau lihat demo tinggal jalankan `Main`."
2. Mengikuti petunjuk itu, ia menjalankan `Main`. Di dalam `Main` terjadi alur Billing Logic yang nyata, misalnya:
   - **Andi** (member) cuci setrika **3 kg @ Rp8.000** → subtotal = 3 × 8.000 = **Rp24.000**.
   - Karena Andi member, dipotong **diskon member 5%** (konstanta `DISKON_MEMBER = 0.05`) → diskon = 24.000 × 0,05 = **Rp1.200**.
   - Total bayar = 24.000 − 1.200 = **Rp22.800**.
   - Setelah dibayar, transaksi masuk ke Transaction History dan bisa dicari lewat invoice atau nama.

Jadi `package-info.java` adalah "papan petunjuk" yang mengarahkan pembaca ke demo, sedangkan perhitungan angka di atas terjadi di class `Transaksi`, `BillingManager`, dan `Main`. Catatan: di modul standalone ini nilai uang memakai tipe **`double`** (misal `8000.0`), berbeda dengan layer web yang memakai `BigDecimal`.

## 5. Hubungan dengan file lain

`package-info.java` **tidak memanggil** dan **tidak diimpor** oleh file kode mana pun — ia tidak punya class atau method untuk dipanggil. Hubungannya bersifat "dokumentasi", bukan "eksekusi". Tetapi paket yang dijelaskannya berisi file-file yang saling terhubung:

- **`Pelanggan.java`** — class data pelanggan (nama, no. telp, status member). Dipakai oleh `Transaksi` untuk mengecek apakah pelanggan member (lewat `cekStatusMember()`), yang menentukan dapat diskon 5% atau tidak.
- **`Transaksi.java`** — satu transaksi laundry; di sinilah Billing Logic berada. Memakai `Pelanggan` (untuk status member) dan `Layanan` (untuk harga per kg). Menyimpan konstanta `DISKON_MEMBER = 0.05`, lalu menghitung subtotal, diskon, dan total bayar.
- **`BillingManager.java`** — pengelola pembayaran sekaligus Transaction History. Memakai `Transaksi`: ia memanggil `buatTagihan()` (yang membuat `Transaksi` lalu menjalankan `hitungBiaya()` dan `terapkanDiskon()`), `prosesPembayaran()` (menandai lunas dan menyimpan ke riwayat), serta menyediakan pencarian `cariByInvoice()` dan `cariByPelanggan()`.
- **`Main.java`** — class demo yang dirujuk langsung oleh komentar di file ini ("Jalankan Main buat lihat demonya"). `Main` membuat `BillingManager`, beberapa `Pelanggan`, dan meminjam class **`Layanan`** dari modul lain (`com.laundry.smartlaundry.module.servicecatalog`, bagian Shellyn) untuk menjalankan kedua fitur dari awal sampai akhir.

Singkatnya rantai pemakaian di paket ini: **`Main` → `BillingManager` → `Transaksi` → memakai `Pelanggan` & `Layanan`**. Dan `package-info.java` berdiri di luar rantai itu sebagai sampul penjelasan untuk seluruh paket.

OK C:/Users/raihan/Documents/GitHub/java-smart-laundry/.notesraihan/code/05-package-info.md
