# BillingManager.java — Orkestrator + Transaction History (simpan & cari)
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/module/payment/BillingManager.java` · Bagian: Raihan (Payment & Billing) · Layer: Modul standalone (Java murni)

## 1. Untuk apa file ini?

`BillingManager` itu **otak pengatur (orkestrator)** dari modul Payment & Billing. Kalau `Transaksi` adalah "satu lembar nota" yang tahu cara menghitung biayanya sendiri, maka `BillingManager` adalah "kasir + buku catatan toko" yang mengatur urutan kerjanya dan menyimpan semua nota yang sudah lunas.

Tugas utamanya ada tiga:
1. **Membuat tagihan** — bikin objek `Transaksi` baru lalu langsung menyuruhnya menghitung subtotal dan diskon (lihat method `buatTagihan`).
2. **Memproses pembayaran** — memeriksa apakah transaksi boleh dibayar, lalu menandainya lunas dan menyimpannya ke daftar riwayat (lihat method `prosesPembayaran`).
3. **Menyimpan & mencari riwayat** — menyimpan semua transaksi yang **sudah lunas saja** ke dalam sebuah list, lalu menyediakan cara untuk mencari, menampilkan, dan menjumlahkan pendapatannya.

Kapan dipakai? Setiap kali ada pelanggan datang menyetor cucian dan membayar, alur programnya melewati `BillingManager`. Bayangkan begini: kasir tidak menghitung sendiri di kepala, dia memakai "mesin billing" ini supaya semua transaksi konsisten dan tercatat rapi.

Kenapa perlu ada class terpisah seperti ini? Supaya **tanggung jawab terbagi jelas** (prinsip dasar PBO): `Transaksi` cuma mengurus dirinya sendiri (data + hitung), sedangkan `BillingManager` mengurus banyak transaksi sekaligus (kumpulan, pencarian, total pendapatan). Pemisahan ini bikin kode lebih mudah dibaca dan dirawat.

> Catatan penting: ini **modul standalone** (Java murni), jadi nilai uang pakai tipe `double`. Di layer web (Spring) nanti uang biasanya pakai `BigDecimal` supaya lebih presisi — tapi di file ini kita masih `double`.

## 2. Kode lengkap

```java
package com.laundry.smartlaundry.module.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;

// Pengelola pembayaran + riwayat. Cuma transaksi lunas yang disimpan di sini.
public class BillingManager {

    private List<Transaksi> riwayatLunas;

    public BillingManager() {
        this.riwayatLunas = new ArrayList<>();
    }

    // buat tagihan + langsung hitung biayanya
    public Transaksi buatTagihan(String idOrder, Pelanggan pelanggan, Layanan layanan,
                                 double berat, String tglMasuk) {
        Transaksi trx = new Transaksi(idOrder, pelanggan, layanan, berat, tglMasuk);

        trx.hitungBiaya();
        trx.terapkanDiskon();

        System.out.println("[BILLING] Tagihan " + idOrder + " dibuat untuk "
                + pelanggan.getNama() + ".");
        System.out.printf("          Subtotal Rp%,.2f - Diskon Rp%,.2f = Total Rp%,.2f%n",
                trx.getSubtotal(), trx.getDiskon(), trx.getTotalBayar());
        return trx;
    }

    // proses bayar -> jadi lunas + masuk riwayat
    public void prosesPembayaran(Transaksi trx) {
        // jangan bayar dua kali
        if (trx.isLunas()) {
            System.out.println("[BILLING] Transaksi " + trx.getIdOrder()
                    + " sudah lunas sebelumnya. Tidak diproses ulang.");
            return;
        }

        // tolak kalau biaya belum dihitung atau totalnya masih 0
        if (!trx.isSudahDihitung() || trx.getTotalBayar() <= 0) {
            System.out.println("[BILLING] Gagal: biaya belum dihitung atau total Rp0 "
                    + "(cek berat cucian). Gunakan buatTagihan() terlebih dahulu.");
            return;
        }

        trx.prosesPembayaran();
        riwayatLunas.add(trx);

        System.out.println("[BILLING] Pembayaran " + trx.getIdOrder()
                + " LUNAS dan tersimpan di riwayat.");
    }

    // cari 1 transaksi lewat nomor invoice
    public Optional<Transaksi> cariByInvoice(String idOrder) {
        return riwayatLunas.stream()
                .filter(trx -> trx.getIdOrder().equalsIgnoreCase(idOrder))
                .findFirst();
    }

    // cari transaksi lewat nama / no telp (sebagian, abaikan besar-kecil huruf)
    public List<Transaksi> cariByPelanggan(String kataKunci) {
        String kunci = kataKunci.toLowerCase();
        List<Transaksi> hasil = new ArrayList<>();
        for (Transaksi trx : riwayatLunas) {
            Pelanggan p = trx.getPelanggan();
            boolean cocokNama = p.getNama().toLowerCase().contains(kunci);
            boolean cocokTelp = p.getNoTelp().toLowerCase().contains(kunci);
            if (cocokNama || cocokTelp) {
                hasil.add(trx);
            }
        }
        return hasil;
    }

    // tampilkan semua riwayat + totalnya
    public void tampilkanSemuaRiwayat() {
        System.out.println("\n===== RIWAYAT TRANSAKSI LUNAS =====");
        if (riwayatLunas.isEmpty()) {
            System.out.println("Belum ada transaksi lunas.");
        } else {
            for (Transaksi trx : riwayatLunas) {
                System.out.printf("%-8s | %-12s | %-14s | Rp%,12.2f | %s%n",
                        trx.getIdOrder(),
                        trx.getTglMasuk(),
                        trx.getPelanggan().getNama(),
                        trx.getTotalBayar(),
                        trx.getStatusBayar());
            }
            System.out.printf("Total %d transaksi | Total pendapatan: Rp%,.2f%n",
                    riwayatLunas.size(), hitungTotalPendapatan());
        }
        System.out.println("===================================");
    }

    // jumlahkan pemasukan dari semua transaksi lunas (dipakai modul Reporting)
    public double hitungTotalPendapatan() {
        double total = 0;
        for (Transaksi trx : riwayatLunas) {
            total += trx.getTotalBayar();
        }
        return total;
    }

    public int getJumlahRiwayat() {
        return riwayatLunas.size();
    }
}

```

## 3. Penjelasan detail per bagian

### 3.1 Bagian `package` dan `import`

```java
package com.laundry.smartlaundry.module.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;
```

- **Untuk apa:** Menyatakan file ini tinggal di "folder/paket" `module.payment`, lalu meminjam beberapa class bawaan Java dan satu class dari modul lain.
- **Cara kerja:**
  - `ArrayList` dan `List` dipakai untuk menyimpan kumpulan transaksi (daftar yang bisa nambah/kurang isinya).
  - `Optional` dipakai sebagai "kotak yang isinya mungkin ada, mungkin kosong" — berguna untuk pencarian yang hasilnya bisa tidak ketemu (lihat `cariByInvoice`).
  - `Layanan` di-`import` dari modul `servicecatalog` (punya rekan tim, modul Shellyn). Class ini menyimpan nama paket & harga per kg.
- **Nanti dipakai untuk apa / dipanggil siapa:** `import` ini "menyalakan" bahan-bahan yang dipakai method di bawahnya. `Layanan` khususnya dipakai di parameter `buatTagihan`.
- **Data masuk → keluar:** Tidak ada (ini deklarasi, bukan logika).

### 3.2 Field `riwayatLunas`

```java
private List<Transaksi> riwayatLunas;
```

- **Untuk apa:** Inilah "buku catatan toko" — sebuah daftar (list) yang menyimpan semua objek `Transaksi` yang **sudah lunas**. Transaksi yang belum dibayar TIDAK masuk ke sini.
- **Cara kerja:** Bertipe `List<Transaksi>`, artinya bisa berisi banyak `Transaksi` dan bisa bertambah sewaktu-waktu. Diberi `private` supaya hanya bisa diutak-atik lewat method di dalam class ini (enkapsulasi — data dilindungi dari sembarang akses luar).
- **Nanti dipakai untuk apa / dipanggil siapa:** Hampir semua method memakai field ini: `prosesPembayaran` menambah isinya, `cariByInvoice` dan `cariByPelanggan` membacanya untuk mencari, `tampilkanSemuaRiwayat` mencetaknya, `hitungTotalPendapatan` menjumlahkannya, dan `getJumlahRiwayat` menghitung ukurannya.
- **Data masuk → keluar:** Bukan method, jadi tidak punya parameter/return. Ia hanya wadah data internal.

### 3.3 Constructor `BillingManager()`

```java
public BillingManager() {
    this.riwayatLunas = new ArrayList<>();
}
```

- **Untuk apa:** Menyiapkan objek `BillingManager` yang masih "kosong dan segar" saat pertama dibuat.
- **Cara kerja:** Saat seseorang menulis `new BillingManager()`, baris `this.riwayatLunas = new ArrayList<>();` membuat daftar kosong. Ini penting: kalau daftar tidak diinisialisasi, ia bernilai `null` dan program akan error (`NullPointerException`) begitu kita coba menambah data.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil sekali di awal program (biasanya di class `Main`/demo modul Payment) untuk membuat satu objek pengelola billing yang akan dipakai sepanjang sesi.
- **Data masuk → keluar:** Tidak ada parameter. Hasilnya: sebuah objek `BillingManager` siap pakai dengan riwayat kosong.

### 3.4 Method `buatTagihan(...)`

```java
public Transaksi buatTagihan(String idOrder, Pelanggan pelanggan, Layanan layanan,
                             double berat, String tglMasuk) {
    Transaksi trx = new Transaksi(idOrder, pelanggan, layanan, berat, tglMasuk);

    trx.hitungBiaya();
    trx.terapkanDiskon();

    System.out.println("[BILLING] Tagihan " + idOrder + " dibuat untuk "
            + pelanggan.getNama() + ".");
    System.out.printf("          Subtotal Rp%,.2f - Diskon Rp%,.2f = Total Rp%,.2f%n",
            trx.getSubtotal(), trx.getDiskon(), trx.getTotalBayar());
    return trx;
}
```

- **Untuk apa:** Membuat sebuah transaksi baru sekaligus langsung menghitung biayanya (subtotal + diskon), supaya pemanggil tidak perlu ingat urutan langkah hitungnya.
- **Cara kerja (langkah demi langkah):**
  1. `new Transaksi(...)` membuat objek transaksi baru dari data yang dikirim (nomor order, pelanggan, layanan, berat, tanggal masuk).
  2. `trx.hitungBiaya();` menyuruh transaksi menghitung subtotal = `berat × harga per kg`.
  3. `trx.terapkanDiskon();` memotong diskon **jika** pelanggan member (diskon member = 5%, konstanta `DISKON_MEMBER = 0.05` di class `Transaksi`), lalu menetapkan total bayar. Kalau bukan member, diskon = 0.
  4. Dua baris `System.out.println` / `printf` mencetak ringkasan ke layar: nama pelanggan, lalu format "Subtotal − Diskon = Total" dengan format rupiah dua angka di belakang koma (`%,.2f`).
  5. `return trx;` mengembalikan objek transaksi yang sudah berisi hasil hitungan, supaya pemanggil bisa memakainya lagi (misalnya untuk dibayar).
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil ketika kasir memasukkan order baru. Hasilnya (`trx`) biasanya langsung diteruskan ke `prosesPembayaran(trx)` untuk dibayar, atau ke `trx.cetakStruk()` untuk dicetak.
- **Data masuk → keluar:**
  - Masuk: `idOrder` (String, no invoice), `pelanggan` (objek `Pelanggan`), `layanan` (objek `Layanan`), `berat` (double, kg), `tglMasuk` (String tanggal).
  - Keluar: sebuah objek `Transaksi` yang subtotal, diskon, dan total bayarnya sudah terisi.

### 3.5 Method `prosesPembayaran(Transaksi trx)`

```java
public void prosesPembayaran(Transaksi trx) {
    if (trx.isLunas()) {
        System.out.println("[BILLING] Transaksi " + trx.getIdOrder()
                + " sudah lunas sebelumnya. Tidak diproses ulang.");
        return;
    }

    if (!trx.isSudahDihitung() || trx.getTotalBayar() <= 0) {
        System.out.println("[BILLING] Gagal: biaya belum dihitung atau total Rp0 "
                + "(cek berat cucian). Gunakan buatTagihan() terlebih dahulu.");
        return;
    }

    trx.prosesPembayaran();
    riwayatLunas.add(trx);

    System.out.println("[BILLING] Pembayaran " + trx.getIdOrder()
            + " LUNAS dan tersimpan di riwayat.");
}
```

- **Untuk apa:** Memproses pembayaran sebuah transaksi dengan aman: menandainya lunas dan menyimpannya ke riwayat — tetapi hanya kalau transaksinya memang sah untuk dibayar.
- **Cara kerja (langkah demi langkah):**
  1. **Penjaga pertama (jangan bayar dua kali):** `if (trx.isLunas())` — kalau transaksi sudah lunas, cetak peringatan dan `return` (berhenti) supaya tidak masuk riwayat dua kali.
  2. **Penjaga kedua (validasi hitungan):** `if (!trx.isSudahDihitung() || trx.getTotalBayar() <= 0)` — kalau biaya belum pernah dihitung **atau** total bayarnya 0 atau kurang (misalnya berat cucian 0), tolak pembayaran, cetak pesan gagal, lalu `return`. Ini mencegah menyimpan transaksi "kosong".
  3. Kalau lolos dua penjaga itu: `trx.prosesPembayaran();` menandai transaksi jadi lunas (`lunas = true` di dalam `Transaksi`).
  4. `riwayatLunas.add(trx);` menambahkan transaksi tersebut ke daftar riwayat.
  5. Cetak konfirmasi bahwa pembayaran LUNAS dan sudah tersimpan.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil setelah `buatTagihan`. Setelah method ini sukses, transaksi tersebut akan ikut terhitung di `hitungTotalPendapatan` dan muncul di `tampilkanSemuaRiwayat` serta hasil pencarian.
- **Data masuk → keluar:**
  - Masuk: `trx` (objek `Transaksi` yang biasanya dibuat dari `buatTagihan`).
  - Keluar: `void` (tidak mengembalikan nilai). Efeknya: status transaksi berubah lunas dan isi `riwayatLunas` bertambah (kalau lolos validasi).

### 3.6 Method `cariByInvoice(String idOrder)`

```java
public Optional<Transaksi> cariByInvoice(String idOrder) {
    return riwayatLunas.stream()
            .filter(trx -> trx.getIdOrder().equalsIgnoreCase(idOrder))
            .findFirst();
}
```

- **Untuk apa:** Mencari **satu** transaksi berdasarkan nomor invoice/order yang persis.
- **Cara kerja:**
  - `riwayatLunas.stream()` mengubah daftar jadi "aliran data" yang bisa disaring.
  - `.filter(trx -> trx.getIdOrder().equalsIgnoreCase(idOrder))` menyimpan hanya transaksi yang nomor ordernya sama dengan `idOrder`. Pakai `equalsIgnoreCase` artinya huruf besar/kecil diabaikan (mis. `"INV01"` dianggap sama dengan `"inv01"`).
  - `.findFirst()` mengambil hasil pertama yang cocok, dibungkus dalam `Optional`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipakai saat butuh menampilkan/mencetak struk satu transaksi tertentu. Karena hasilnya `Optional`, pemanggil harus mengecek dulu apakah ada isinya (mis. `if (hasil.isPresent())`) sebelum dipakai — ini cara aman menghindari error kalau invoice tidak ketemu.
- **Data masuk → keluar:**
  - Masuk: `idOrder` (String nomor invoice yang dicari).
  - Keluar: `Optional<Transaksi>` — berisi transaksi kalau ketemu, atau kosong (`Optional.empty()`) kalau tidak ada.

### 3.7 Method `cariByPelanggan(String kataKunci)`

```java
public List<Transaksi> cariByPelanggan(String kataKunci) {
    String kunci = kataKunci.toLowerCase();
    List<Transaksi> hasil = new ArrayList<>();
    for (Transaksi trx : riwayatLunas) {
        Pelanggan p = trx.getPelanggan();
        boolean cocokNama = p.getNama().toLowerCase().contains(kunci);
        boolean cocokTelp = p.getNoTelp().toLowerCase().contains(kunci);
        if (cocokNama || cocokTelp) {
            hasil.add(trx);
        }
    }
    return hasil;
}
```

- **Untuk apa:** Mencari **banyak** transaksi berdasarkan nama atau nomor telepon pelanggan, dengan pencocokan **sebagian** (tidak harus persis).
- **Cara kerja (langkah demi langkah):**
  1. `kataKunci.toLowerCase()` mengubah kata kunci ke huruf kecil supaya pencarian tidak peduli besar/kecil huruf.
  2. Membuat `hasil` (list kosong) untuk menampung transaksi yang cocok.
  3. Melakukan `for` (loop) ke setiap transaksi di `riwayatLunas`.
  4. Untuk tiap transaksi, ambil `Pelanggan`-nya, lalu cek dua hal:
     - `cocokNama` = nama pelanggan (huruf kecil) mengandung kata kunci.
     - `cocokTelp` = nomor telepon (huruf kecil) mengandung kata kunci.
     - `.contains(...)` artinya "mengandung sebagian" — jadi mengetik "an" bisa cocok dengan "Andi" maupun "Wawan".
  5. Kalau salah satu cocok (`cocokNama || cocokTelp`), transaksi dimasukkan ke `hasil`.
  6. Kembalikan `hasil`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipakai saat petugas mau melihat semua transaksi milik seorang pelanggan tanpa hafal nomor invoice-nya. Hasilnya berupa daftar yang bisa di-loop untuk ditampilkan.
- **Data masuk → keluar:**
  - Masuk: `kataKunci` (String, potongan nama atau no telp).
  - Keluar: `List<Transaksi>` berisi semua transaksi yang cocok. Kalau tak ada yang cocok, daftarnya kosong (bukan `null`).

### 3.8 Method `tampilkanSemuaRiwayat()`

```java
public void tampilkanSemuaRiwayat() {
    System.out.println("\n===== RIWAYAT TRANSAKSI LUNAS =====");
    if (riwayatLunas.isEmpty()) {
        System.out.println("Belum ada transaksi lunas.");
    } else {
        for (Transaksi trx : riwayatLunas) {
            System.out.printf("%-8s | %-12s | %-14s | Rp%,12.2f | %s%n",
                    trx.getIdOrder(),
                    trx.getTglMasuk(),
                    trx.getPelanggan().getNama(),
                    trx.getTotalBayar(),
                    trx.getStatusBayar());
        }
        System.out.printf("Total %d transaksi | Total pendapatan: Rp%,.2f%n",
                riwayatLunas.size(), hitungTotalPendapatan());
    }
    System.out.println("===================================");
}
```

- **Untuk apa:** Menampilkan seluruh riwayat transaksi lunas ke layar dalam bentuk tabel rapi, plus ringkasan jumlah transaksi dan total pendapatan di bawahnya.
- **Cara kerja (langkah demi langkah):**
  1. Cetak judul header `===== RIWAYAT TRANSAKSI LUNAS =====`.
  2. `if (riwayatLunas.isEmpty())` — kalau belum ada transaksi, cetak "Belum ada transaksi lunas." dan selesai.
  3. Kalau ada isinya, `for` ke setiap transaksi dan cetak satu baris per transaksi memakai `System.out.printf` dengan format kolom:
     - `%-8s` = nomor order, rata kiri lebar 8.
     - `%-12s` = tanggal masuk, rata kiri lebar 12.
     - `%-14s` = nama pelanggan, rata kiri lebar 14.
     - `Rp%,12.2f` = total bayar dengan pemisah ribuan dan 2 desimal, rata kanan lebar 12.
     - `%s` = status bayar ("LUNAS"/"BELUM LUNAS").
  4. Setelah loop, cetak ringkasan: jumlah transaksi (`riwayatLunas.size()`) dan total pendapatan (memanggil `hitungTotalPendapatan()`).
  5. Cetak garis penutup.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil dari menu/demo modul Payment saat petugas memilih "lihat semua riwayat". Murni untuk menampilkan informasi, tidak mengubah data apa pun.
- **Data masuk → keluar:** Tidak ada parameter; `void`. Efeknya hanya mencetak ke layar (`System.out`).

### 3.9 Method `hitungTotalPendapatan()`

```java
public double hitungTotalPendapatan() {
    double total = 0;
    for (Transaksi trx : riwayatLunas) {
        total += trx.getTotalBayar();
    }
    return total;
}
```

- **Untuk apa:** Menjumlahkan total bayar dari semua transaksi lunas untuk mengetahui total pendapatan.
- **Cara kerja:** Mulai dari `total = 0`, lalu loop ke setiap transaksi dan tambahkan `trx.getTotalBayar()` ke `total`. Setelah semua dijumlah, kembalikan `total`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipakai di dalam `tampilkanSemuaRiwayat` untuk baris ringkasan, dan menurut komentar di kodenya juga **dipakai modul Reporting** (modul lain bisa memanggil ini untuk laporan pemasukan).
- **Data masuk → keluar:**
  - Masuk: tidak ada parameter (membaca dari field `riwayatLunas`).
  - Keluar: `double` berisi jumlah total pendapatan. Ingat, tipe uang di modul standalone ini memang `double` (di layer web pakai `BigDecimal`).

### 3.10 Method `getJumlahRiwayat()`

```java
public int getJumlahRiwayat() {
    return riwayatLunas.size();
}
```

- **Untuk apa:** Memberitahu berapa banyak transaksi lunas yang sudah tersimpan.
- **Cara kerja:** Mengembalikan `riwayatLunas.size()`, yaitu jumlah elemen di dalam list.
- **Nanti dipakai untuk apa / dipanggil siapa:** Berguna untuk pengecekan cepat, misalnya di kode demo/test untuk memastikan "setelah satu pembayaran, jumlah riwayat jadi 1". Tidak mengubah data.
- **Data masuk → keluar:**
  - Masuk: tidak ada.
  - Keluar: `int` jumlah transaksi lunas.

## 4. Contoh alur nyata

Misalkan Andi (seorang **member**) menyetor cucian 3 kg dengan layanan seharga Rp8.000/kg, tanggal masuk "2026-06-25", nomor order "INV01".

1. **Buat tagihan:**
   ```java
   BillingManager billing = new BillingManager();
   Transaksi trx = billing.buatTagihan("INV01", andi, layananReguler, 3, "2026-06-25");
   ```
   Di dalam `buatTagihan`:
   - `hitungBiaya()` → subtotal = 3 kg × Rp8.000 = **Rp24.000**.
   - `terapkanDiskon()` → karena Andi member, diskon = 24.000 × 0,05 (5%) = **Rp1.200**.
   - total bayar = 24.000 − 1.200 = **Rp22.800**.
   - Layar mencetak: `Subtotal Rp24.000,00 - Diskon Rp1.200,00 = Total Rp22.800,00`.

2. **Proses pembayaran:**
   ```java
   billing.prosesPembayaran(trx);
   ```
   - Penjaga 1: belum lunas → lolos.
   - Penjaga 2: sudah dihitung dan total (22.800) > 0 → lolos.
   - Transaksi ditandai LUNAS dan ditambahkan ke `riwayatLunas`.
   - Layar mencetak: `[BILLING] Pembayaran INV01 LUNAS dan tersimpan di riwayat.`

3. **Cek & cari:**
   - `billing.getJumlahRiwayat()` → `1`.
   - `billing.cariByInvoice("inv01")` → `Optional` berisi transaksi INV01 (huruf besar/kecil diabaikan).
   - `billing.cariByPelanggan("an")` → daftar berisi transaksi Andi (karena "an" ada di "Andi").
   - `billing.hitungTotalPendapatan()` → `22800.0`.
   - `billing.tampilkanSemuaRiwayat()` → mencetak tabel berisi baris INV01 dan ringkasan "Total 1 transaksi | Total pendapatan: Rp22.800,00".

Sebagai pembanding, kalau pelanggan **bukan member** (mis. Budi) dengan cucian yang sama: diskon = 0, jadi total bayar = subtotal = **Rp24.000**.

## 5. Hubungan dengan file lain

- **`BillingManager` memakai `Transaksi`** (`module.payment.Transaksi`): membuatnya lewat `new Transaksi(...)` dan memanggil method-methodnya: `hitungBiaya()`, `terapkanDiskon()`, `prosesPembayaran()`, serta getter seperti `isLunas()`, `isSudahDihitung()`, `getTotalBayar()`, `getIdOrder()`, `getTglMasuk()`, `getStatusBayar()`, `getPelanggan()`. Logika hitung biaya & konstanta diskon member 5% (`DISKON_MEMBER = 0.05`) ada di `Transaksi`, bukan di sini.
- **`BillingManager` (lewat `Transaksi`) memakai `Pelanggan`** (`module.payment.Pelanggan`): di `cariByPelanggan` ia memanggil `getNama()` dan `getNoTelp()`, dan status member dicek di dalam `Transaksi` via `cekStatusMember()`.
- **`BillingManager` memakai `Layanan`** (`module.servicecatalog.Layanan`, modul rekan tim/Shellyn): diterima sebagai parameter `buatTagihan` lalu diteruskan ke `Transaksi`, yang membaca `getHargaPerKg()` untuk menghitung subtotal.
- **Siapa yang memakai `BillingManager`?** Class `Main`/demo modul Payment (titik masuk program) membuat satu objek `BillingManager` dan memanggil `buatTagihan` → `prosesPembayaran`, lalu method tampilan/pencarian sesuai pilihan menu.
- **Modul Reporting** memakai `hitungTotalPendapatan()` untuk mengambil angka total pemasukan (sesuai komentar di kode).
- **Catatan layer:** File ini bagian dari **modul standalone Java murni** (uang `double`). Ini berbeda dari layer web Spring yang memakai `BigDecimal` untuk uang; alur web punya rangkaian sendiri (mis. controller → service) yang tidak terlibat langsung dengan class ini.
