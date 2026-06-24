# Transaksi.java — Inti Billing Logic: hitung biaya, diskon, cetak struk
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/module/payment/Transaksi.java` · Bagian: Raihan (Payment & Billing) · Layer: Modul standalone (Java murni)

## 1. Untuk apa file ini?

`Transaksi` adalah **class** (cetakan objek) yang mewakili **satu order laundry**. Bayangkan satu nota/struk laundry: ada nomor order, siapa pelanggannya, paket layanan yang dipilih, berapa kilo cuciannya, dan tanggal masuk. Semua data itu disimpan di dalam satu objek `Transaksi`.

Yang membuat class ini penting: di sinilah **logika billing (perhitungan biaya) sebenarnya terjadi**. Class ini tahu cara:
- menghitung **subtotal** (berat × harga per kg),
- memotong **diskon** kalau pelanggan adalah member,
- menentukan **total bayar**,
- menandai transaksi sudah **lunas** atau belum,
- mencetak **struk** ke layar.

Class ini dipakai **setiap kali ada pelanggan datang dan membayar laundry**. Di aplikasi (lihat `BillingManager`), objek `Transaksi` dibuat saat tagihan dibikin, dihitung biayanya, lalu kalau sudah dibayar baru disimpan ke riwayat.

Karena ini bagian dari **modul standalone (Java murni)**, semua nilai uang di sini memakai tipe `double`. (Catatan: di layer web yang lain, uang biasanya pakai `BigDecimal` supaya lebih presisi — tapi di file ini murni `double`.)

Buat teman yang baru belajar PBO: anggap `Transaksi` itu seperti **formulir nota kosong**. Begitu kamu isi (lewat constructor), kamu bisa menyuruhnya "hitung biaya", "potong diskon", "tandai lunas", dan "cetak struk". Datanya tersimpan rapi di dalam objek itu sendiri.

## 2. Kode lengkap

```java
package com.laundry.smartlaundry.module.payment;

// Layanan dipinjam dari modul Shellyn (servicecatalog).
import com.laundry.smartlaundry.module.servicecatalog.Layanan;

// Satu transaksi laundry. Di sini biaya dihitung (billing logic).
public class Transaksi {

    public static final double DISKON_MEMBER = 0.05; // diskon member 5%, ganti di sini kalau berubah

    private String idOrder;
    private Pelanggan pelanggan;
    private Layanan layanan;
    private double berat;          // kg
    private String tglMasuk;

    private double subtotal;
    private double diskon;
    private double totalBayar;
    private boolean lunas;
    private boolean sudahDihitung; // penanda biaya udah dihitung apa belum

    public Transaksi(String idOrder, Pelanggan pelanggan, Layanan layanan, double berat, String tglMasuk) {
        this.idOrder = idOrder;
        this.pelanggan = pelanggan;
        this.layanan = layanan;
        this.tglMasuk = tglMasuk;

        // berat harus > 0, kalau nggak transaksinya nggak bisa dibayar
        if (berat <= 0) {
            System.out.println("[Peringatan] Berat cucian harus lebih dari 0 kg. Berat diatur menjadi 0.");
            this.berat = 0;
        } else {
            this.berat = berat;
        }

        this.lunas = false; // transaksi baru = belum lunas
    }

    // langkah 1: subtotal = berat x harga per kg
    public double hitungBiaya() {
        this.subtotal = this.berat * layanan.getHargaPerKg();
        return this.subtotal;
    }

    // langkah 2: potong diskon kalau member, lalu dapat total bayar
    public double terapkanDiskon() {
        if (pelanggan.cekStatusMember()) {
            this.diskon = this.subtotal * DISKON_MEMBER;
        } else {
            this.diskon = 0;
        }
        this.totalBayar = this.subtotal - this.diskon;
        this.sudahDihitung = true;
        return this.totalBayar;
    }

    public void prosesPembayaran() {
        this.lunas = true;
    }

    // cetak struk ke layar
    public void cetakStruk() {
        String statusMember = pelanggan.cekStatusMember() ? " (Member)" : "";
        int persenDiskon = pelanggan.cekStatusMember() ? (int) (DISKON_MEMBER * 100) : 0;

        System.out.println("========================================");
        System.out.println("        STRUK PEMBAYARAN LAUNDRY        ");
        System.out.println("========================================");
        System.out.println("No. Invoice : " + idOrder);
        System.out.println("Tanggal     : " + tglMasuk);
        System.out.println("Pelanggan   : " + pelanggan.getNama() + statusMember);
        System.out.println("Layanan     : " + layanan.getNamaPaket());
        System.out.printf("Berat       : %.2f Kg%n", berat);
        System.out.printf("Harga/Kg    : Rp%,.2f%n", layanan.getHargaPerKg());
        System.out.println("----------------------------------------");
        System.out.printf("Subtotal    : Rp%,.2f%n", subtotal);
        System.out.printf("Diskon (%d%%) : Rp%,.2f%n", persenDiskon, diskon);
        System.out.printf("TOTAL BAYAR : Rp%,.2f%n", totalBayar);
        System.out.println("Status      : " + getStatusBayar());
        System.out.println("========================================");
        System.out.println("  Terima kasih telah laundry di sini!  ");
        System.out.println("========================================");
    }

    // getter
    public String getIdOrder() {
        return idOrder;
    }

    public Pelanggan getPelanggan() {
        return pelanggan;
    }

    public Layanan getLayanan() {
        return layanan;
    }

    public double getBerat() {
        return berat;
    }

    public String getTglMasuk() {
        return tglMasuk;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiskon() {
        return diskon;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public boolean isLunas() {
        return lunas;
    }

    public boolean isSudahDihitung() {
        return sudahDihitung;
    }

    // "LUNAS" / "BELUM LUNAS" buat ditampilkan
    public String getStatusBayar() {
        return lunas ? "LUNAS" : "BELUM LUNAS";
    }
}

```

## 3. Penjelasan detail per bagian

### `package` dan `import`
```java
package com.laundry.smartlaundry.module.payment;
import com.laundry.smartlaundry.module.servicecatalog.Layanan;
```
- **Untuk apa:** Baris `package` menentukan "alamat" class ini di dalam proyek (folder `module/payment`). Baris `import` "meminjam" class `Layanan` dari modul lain (`servicecatalog`, milik Shellyn) supaya bisa dipakai di sini.
- **Cara kerja:** Tanpa `import`, Java tidak tahu di mana `Layanan` berada karena beda package. Class `Pelanggan` tidak perlu di-`import` karena ada di package yang sama (`payment`).
- **Nanti dipakai untuk apa:** `Layanan` dipakai untuk mengambil harga per kg dan nama paket saat menghitung biaya dan mencetak struk.

---

### Konstanta `DISKON_MEMBER`
```java
public static final double DISKON_MEMBER = 0.05; // diskon member 5%
```
- **Untuk apa:** Menyimpan **besar diskon untuk member = 5%** (0.05). Karena `static final`, nilainya **sama untuk semua transaksi** dan **tidak bisa diubah** saat program jalan (konstanta).
- **Cara kerja:** `0.05` artinya 5% dalam bentuk pecahan. Kalau suatu saat diskon berubah, cukup ganti angka di satu baris ini saja, bukan di banyak tempat.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipakai di method `terapkanDiskon()` (untuk menghitung jumlah diskon) dan di `cetakStruk()` (untuk menampilkan "Diskon (5%)").
- **Data masuk → keluar:** Bukan method, jadi tidak menerima/mengembalikan apa pun — hanya nilai tetap `0.05`.

---

### Field (atribut) data transaksi
```java
private String idOrder;
private Pelanggan pelanggan;
private Layanan layanan;
private double berat;          // kg
private String tglMasuk;
```
- **Untuk apa:** Menyimpan **data inti** satu order: nomor invoice (`idOrder`), siapa pelanggannya (objek `Pelanggan`), paket layanannya (objek `Layanan`), berat cucian dalam kg (`berat`), dan tanggal masuk (`tglMasuk`).
- **Cara kerja:** Semua `private`, artinya hanya bisa diakses dari dalam class ini. Dari luar, datanya diambil lewat **getter** (lihat bawah). `pelanggan` dan `layanan` bukan tipe sederhana — ini contoh **agregasi** (objek menyimpan objek lain).
- **Nanti dipakai untuk apa:** Diisi sekali lewat constructor, lalu dipakai oleh method `hitungBiaya()`, `terapkanDiskon()`, dan `cetakStruk()`.
- **Data masuk → keluar:** Diisi oleh constructor; dibaca lewat getter masing-masing.

---

### Field hasil perhitungan & status
```java
private double subtotal;
private double diskon;
private double totalBayar;
private boolean lunas;
private boolean sudahDihitung; // penanda biaya udah dihitung apa belum
```
- **Untuk apa:** Menyimpan **hasil** billing: `subtotal` (sebelum diskon), `diskon` (jumlah potongan), `totalBayar` (yang dibayar), `lunas` (sudah dibayar atau belum), dan `sudahDihitung` (apakah biaya sudah pernah dihitung).
- **Cara kerja:** Field-field ini awalnya bernilai default (`double` = 0.0, `boolean` = false). Nilainya baru terisi setelah method perhitungan dijalankan.
- **Nanti dipakai untuk apa / dipanggil siapa:** `sudahDihitung` dan `totalBayar` dipakai oleh `BillingManager.prosesPembayaran()` untuk **menolak pembayaran** kalau biaya belum dihitung atau totalnya masih 0. `lunas` menentukan teks status di struk.
- **Data masuk → keluar:** Diisi otomatis oleh `hitungBiaya()`, `terapkanDiskon()`, dan `prosesPembayaran()`; dibaca lewat getter.

---

### Constructor `Transaksi(...)`
```java
public Transaksi(String idOrder, Pelanggan pelanggan, Layanan layanan, double berat, String tglMasuk) {
    this.idOrder = idOrder;
    this.pelanggan = pelanggan;
    this.layanan = layanan;
    this.tglMasuk = tglMasuk;

    if (berat <= 0) {
        System.out.println("[Peringatan] Berat cucian harus lebih dari 0 kg. Berat diatur menjadi 0.");
        this.berat = 0;
    } else {
        this.berat = berat;
    }

    this.lunas = false; // transaksi baru = belum lunas
}
```
- **Untuk apa:** Membuat objek `Transaksi` baru dan mengisi data awalnya.
- **Cara kerja, langkah demi langkah:**
  1. Menyalin `idOrder`, `pelanggan`, `layanan`, dan `tglMasuk` ke field objek (`this.xxx = xxx`).
  2. **Validasi berat:** kalau `berat <= 0`, program mencetak peringatan dan **memaksa berat menjadi 0**. Ini penting supaya transaksi dengan berat tidak masuk akal nantinya menghasilkan total 0 dan ditolak saat pembayaran. Kalau berat valid (> 0), nilainya dipakai apa adanya.
  3. Mengeset `lunas = false` karena transaksi yang baru dibuat pasti **belum dibayar**.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil oleh `BillingManager.buatTagihan(...)` saat membuat tagihan baru, atau langsung lewat `new Transaksi(...)` di kelas `Main` (demo).
- **Data masuk → keluar:** Masuk = 5 parameter (idOrder, pelanggan, layanan, berat, tglMasuk). Keluar = objek `Transaksi` baru yang siap dihitung biayanya. (Constructor tidak mengembalikan nilai biasa.)

---

### Method `hitungBiaya()` — langkah 1
```java
public double hitungBiaya() {
    this.subtotal = this.berat * layanan.getHargaPerKg();
    return this.subtotal;
}
```
- **Untuk apa:** Menghitung **subtotal**, yaitu biaya sebelum diskon.
- **Cara kerja:** Mengambil harga per kg dari objek `layanan` (lewat `layanan.getHargaPerKg()`), lalu mengalikannya dengan `berat`. Hasilnya disimpan ke field `subtotal` dan langsung dikembalikan.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil **pertama** sebelum `terapkanDiskon()`. Di aplikasi, dipanggil otomatis oleh `BillingManager.buatTagihan(...)`.
- **Data masuk → keluar:** Masuk = tidak ada parameter (memakai field `berat` dan objek `layanan`). Keluar = `double` subtotal.

---

### Method `terapkanDiskon()` — langkah 2
```java
public double terapkanDiskon() {
    if (pelanggan.cekStatusMember()) {
        this.diskon = this.subtotal * DISKON_MEMBER;
    } else {
        this.diskon = 0;
    }
    this.totalBayar = this.subtotal - this.diskon;
    this.sudahDihitung = true;
    return this.totalBayar;
}
```
- **Untuk apa:** Menghitung **diskon** (kalau pelanggan member) lalu menentukan **total bayar**.
- **Cara kerja, langkah demi langkah:**
  1. Bertanya ke objek `pelanggan`: apakah dia member? (`pelanggan.cekStatusMember()`).
  2. Kalau **member**: `diskon = subtotal × 0.05` (potongan **5%**). Kalau **bukan member**: `diskon = 0`.
  3. `totalBayar = subtotal − diskon`.
  4. Mengeset `sudahDihitung = true` sebagai penanda bahwa biaya sudah selesai dihitung (penanda ini dicek `BillingManager` sebelum menerima pembayaran).
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil **setelah** `hitungBiaya()` (karena butuh nilai `subtotal` lebih dulu). Dipanggil otomatis oleh `BillingManager.buatTagihan(...)`.
- **Data masuk → keluar:** Masuk = tidak ada parameter (memakai field `subtotal` dan status member). Keluar = `double` total bayar.

---

### Method `prosesPembayaran()`
```java
public void prosesPembayaran() {
    this.lunas = true;
}
```
- **Untuk apa:** Menandai transaksi ini sudah **LUNAS**.
- **Cara kerja:** Mengubah field `lunas` dari `false` menjadi `true`. Sangat sederhana — class ini sengaja tidak mengurusi cara bayar; ia hanya menyimpan status.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil oleh `BillingManager.prosesPembayaran(trx)` **setelah** lolos pengecekan (belum lunas, sudah dihitung, total > 0). Setelah ini transaksi dimasukkan ke riwayat lunas.
- **Data masuk → keluar:** Masuk = tidak ada parameter. Keluar = tidak ada (`void`); efeknya mengubah field `lunas`.

---

### Method `cetakStruk()`
```java
public void cetakStruk() {
    String statusMember = pelanggan.cekStatusMember() ? " (Member)" : "";
    int persenDiskon = pelanggan.cekStatusMember() ? (int) (DISKON_MEMBER * 100) : 0;
    ...
}
```
- **Untuk apa:** Mencetak **struk pembayaran** yang rapi ke layar (console).
- **Cara kerja, langkah demi langkah:**
  1. `statusMember` diisi `" (Member)"` kalau pelanggan member, atau kosong kalau bukan (memakai operator `? :` / ternary).
  2. `persenDiskon` diisi `5` kalau member (`DISKON_MEMBER * 100 = 0.05 * 100 = 5`, lalu di-cast ke `int`), atau `0` kalau bukan member.
  3. Mencetak header, nomor invoice, tanggal, nama pelanggan (+ label member), nama paket, berat, harga per kg, subtotal, diskon, total bayar, dan status.
  4. `System.out.printf` dengan format seperti `Rp%,.2f` artinya angka uang ditampilkan dengan pemisah ribuan dan 2 angka di belakang koma (contoh: `Rp24.000,00` tergantung locale).
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil saat ingin menampilkan bukti pembayaran ke pengguna, misalnya dari kelas `Main` (demo modul) setelah pembayaran diproses.
- **Data masuk → keluar:** Masuk = tidak ada parameter (membaca semua field objek). Keluar = tidak ada (`void`); hasilnya teks tercetak di layar.

---

### Getter (`getIdOrder`, `getPelanggan`, `getLayanan`, `getBerat`, `getTglMasuk`, `getSubtotal`, `getDiskon`, `getTotalBayar`, `isLunas`, `isSudahDihitung`)
```java
public String getIdOrder() { return idOrder; }
public Pelanggan getPelanggan() { return pelanggan; }
public Layanan getLayanan() { return layanan; }
public double getBerat() { return berat; }
public String getTglMasuk() { return tglMasuk; }
public double getSubtotal() { return subtotal; }
public double getDiskon() { return diskon; }
public double getTotalBayar() { return totalBayar; }
public boolean isLunas() { return lunas; }
public boolean isSudahDihitung() { return sudahDihitung; }
```
- **Untuk apa:** Memberikan **akses baca** ke field yang `private` dari luar class. Ini prinsip **encapsulation** di PBO: data disembunyikan, akses lewat method.
- **Cara kerja:** Tiap method hanya mengembalikan nilai satu field. (Untuk `boolean` namanya pakai awalan `is` — konvensi Java.)
- **Nanti dipakai untuk apa / dipanggil siapa:** `BillingManager` banyak memakai ini — contohnya `getTotalBayar()` dan `isSudahDihitung()` untuk validasi pembayaran, `getIdOrder()` untuk pencarian, `getPelanggan()` untuk mencari berdasarkan nama/telp, dan `getSubtotal()`/`getDiskon()`/`getTotalBayar()` untuk cetak ringkasan.
- **Data masuk → keluar:** Masuk = tidak ada parameter. Keluar = nilai field yang bersangkutan.

---

### Method `getStatusBayar()`
```java
public String getStatusBayar() {
    return lunas ? "LUNAS" : "BELUM LUNAS";
}
```
- **Untuk apa:** Mengubah status `boolean lunas` menjadi **teks yang ramah dibaca**.
- **Cara kerja:** Operator ternary `lunas ? "LUNAS" : "BELUM LUNAS"` — kalau `lunas == true` kembalikan `"LUNAS"`, selain itu `"BELUM LUNAS"`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipakai di `cetakStruk()` (baris status) dan di `BillingManager.tampilkanSemuaRiwayat()` (kolom status pada tabel riwayat).
- **Data masuk → keluar:** Masuk = tidak ada parameter. Keluar = `String` `"LUNAS"` atau `"BELUM LUNAS"`.

## 4. Contoh alur nyata

Misalkan **Andi adalah member**, mencuci **3 kg** dengan paket seharga **Rp8.000/kg**. Alurnya:

1. **Buat transaksi** (constructor): `new Transaksi("INV-001", andi, paketReguler, 3, "2026-06-25")`. Berat 3 > 0, jadi diterima. `lunas = false`.
2. **`hitungBiaya()`** → `subtotal = 3 × 8.000 = 24.000`.
3. **`terapkanDiskon()`** → Andi member, jadi `diskon = 24.000 × 0.05 = 1.200`. Lalu `totalBayar = 24.000 − 1.200 = 22.800`. `sudahDihitung = true`.
4. **`prosesPembayaran()`** → `lunas = true` (transaksi jadi LUNAS).
5. **`cetakStruk()`** menampilkan kira-kira:
   - Pelanggan : Andi (Member)
   - Subtotal : Rp24.000,00
   - Diskon (5%) : Rp1.200,00
   - TOTAL BAYAR : Rp22.800,00
   - Status : LUNAS

Bandingkan kalau **Budi bukan member**, 3 kg @ Rp8.000: `subtotal = 24.000`, `diskon = 0`, `totalBayar = 24.000`. Di struk tertulis "Diskon (0%) : Rp0,00".

Kalau berat dimasukkan **0 atau negatif**, constructor memaksa `berat = 0`, sehingga `subtotal` dan `totalBayar` ikut 0 — dan `BillingManager.prosesPembayaran()` akan **menolak** pembayaran karena total Rp0.

## 5. Hubungan dengan file lain

**File ini MEMAKAI / bergantung pada:**
- **`Pelanggan`** (package sama, `module/payment`): dipakai untuk mengetahui status member (`cekStatusMember()`) dan nama pelanggan (`getNama()`) saat cetak struk.
- **`Layanan`** (di-`import` dari modul `servicecatalog`, milik Shellyn): dipakai untuk mengambil `getHargaPerKg()` (untuk hitung subtotal) dan `getNamaPaket()` (untuk struk). Ini contoh **agregasi antar-modul**.

**File ini DIPAKAI / DIPANGGIL oleh:**
- **`BillingManager`** (package sama): pemakai utama. `buatTagihan(...)` membuat objek `Transaksi`, lalu memanggil `hitungBiaya()` dan `terapkanDiskon()`. `prosesPembayaran(trx)` memanggil `trx.prosesPembayaran()` dan menyimpan transaksi ke riwayat. Method pencarian dan laporan (`cariByInvoice`, `cariByPelanggan`, `tampilkanSemuaRiwayat`, `hitungTotalPendapatan`) memakai getter `Transaksi`.
- **`Main`** (kelas demo modul payment): membuat transaksi, memprosesnya lewat `BillingManager`, lalu memanggil `cetakStruk()` untuk menampilkan bukti.

**Catatan tipe data:** Di modul standalone ini, semua uang (`subtotal`, `diskon`, `totalBayar`, `hargaPerKg`) bertipe **`double`**. Di layer web Smart Laundry, perhitungan uang umumnya memakai **`BigDecimal`** agar lebih presisi — jadi logika billing-nya konsep sama, tapi tipe datanya berbeda.
