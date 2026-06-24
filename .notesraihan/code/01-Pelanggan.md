# Pelanggan.java — Data pelanggan + penentu status member
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/module/payment/Pelanggan.java` · Bagian: Raihan (Payment & Billing) · Layer: Modul standalone (Java murni)

## 1. Untuk apa file ini?

File ini berisi sebuah **class** bernama `Pelanggan`. Bayangkan class itu seperti "cetakan" atau "template" untuk membuat objek di dunia nyata. Di sini, cetakannya adalah **satu orang pelanggan laundry**.

Setiap kali ada orang datang ke laundry, kita bisa membuat satu objek `Pelanggan` untuk menyimpan datanya: siapa namanya, nomor teleponnya berapa, ID member-nya apa, dan yang paling penting — **apakah dia member atau bukan**.

Kenapa status member ini penting? Karena di modul Payment & Billing, **member mendapat diskon 5%** saat menghitung total pembayaran. Jadi class `Pelanggan` ini tugas utamanya adalah "membawa informasi" yang nanti dipakai oleh class lain (`Transaksi`) untuk memutuskan: orang ini dapat diskon atau tidak.

Class ini termasuk **modul standalone** — artinya ditulis dengan Java murni (tanpa framework web seperti Spring). Ini cocok untuk belajar konsep dasar PBO (Pemrograman Berorientasi Objek), karena kita bisa fokus ke ide class, field, constructor, dan method tanpa terganggu hal-hal rumit lainnya.

Singkatnya, `Pelanggan` adalah **wadah data (data holder)** untuk satu pelanggan, plus punya satu method kecil untuk mengecek status member.

## 2. Kode lengkap

```java
package com.laundry.smartlaundry.module.payment;

// Data pelanggan. Yang penting di sini status member-nya,
// soalnya member dapat diskon pas hitung total.
public class Pelanggan {

    private String idMember;   // contoh: "MBR-01"
    private String nama;
    private String noTelp;     // dipakai buat nyari pelanggan
    private boolean member;    // true = member (dapat diskon)

    public Pelanggan(String idMember, String nama, String noTelp, boolean member) {
        this.idMember = idMember;
        this.nama = nama;
        this.noTelp = noTelp;
        this.member = member;
    }

    // true kalau member -> dipakai buat nentuin diskon
    public boolean cekStatusMember() {
        return member;
    }

    // getter & setter
    public String getIdMember() {
        return idMember;
    }

    public void setIdMember(String idMember) {
        this.idMember = idMember;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public void setNoTelp(String noTelp) {
        this.noTelp = noTelp;
    }

    public boolean isMember() {
        return member;
    }

    public void setMember(boolean member) {
        this.member = member;
    }

}

```

## 3. Penjelasan detail per bagian

### Baris `package ...`

```java
package com.laundry.smartlaundry.module.payment;
```

- **Untuk apa:** Menandai bahwa file ini "tinggal" di dalam paket (package) `payment`. Package itu seperti folder untuk merapikan kode. Class `Pelanggan` adalah penghuni folder Payment & Billing milik Raihan.
- **Cara kerja:** Java memakai nama package ini untuk mengenali class secara lengkap, yaitu `com.laundry.smartlaundry.module.payment.Pelanggan`. Class lain di package yang sama (misal `Transaksi`) bisa langsung memakainya tanpa perlu `import`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Tidak "dipanggil", tapi menentukan lokasi class. `Transaksi` yang ada di package yang sama bisa langsung memakai `Pelanggan`.
- **Data masuk → keluar:** Tidak ada (ini bukan method, hanya deklarasi lokasi).

---

### Field `idMember`

```java
private String idMember;   // contoh: "MBR-01"
```

- **Untuk apa:** Menyimpan kode/ID member pelanggan, misalnya `"MBR-01"`. Ini semacam nomor kartu member.
- **Cara kerja:** Bertipe `String` (teks). Kata kunci `private` berarti field ini hanya boleh diakses dari dalam class `Pelanggan` sendiri. Kalau class lain mau membacanya, mereka harus lewat method `getIdMember()`. Ini namanya **enkapsulasi** — data disembunyikan dan hanya diakses lewat "pintu resmi".
- **Nanti dipakai untuk apa / dipanggil siapa:** Disetel saat objek dibuat (lewat constructor), lalu bisa dibaca via `getIdMember()` atau diubah via `setIdMember(...)`.
- **Data masuk → keluar:** Nilai masuk dari constructor/setter, keluar lewat getter.

---

### Field `nama`

```java
private String nama;
```

- **Untuk apa:** Menyimpan nama pelanggan, misalnya `"Andi"`.
- **Cara kerja:** Bertipe `String`, juga `private`. Nilai ini nanti dicetak di struk pembayaran.
- **Nanti dipakai untuk apa / dipanggil siapa:** `Transaksi.cetakStruk()` memanggil `pelanggan.getNama()` untuk menulis baris `Pelanggan : ...` pada struk.
- **Data masuk → keluar:** Masuk dari constructor/setter, keluar lewat `getNama()`.

---

### Field `noTelp`

```java
private String noTelp;     // dipakai buat nyari pelanggan
```

- **Untuk apa:** Menyimpan nomor telepon pelanggan. Sesuai komentar di kode, nomor telepon dipakai sebagai "kunci" untuk **mencari** pelanggan tertentu.
- **Cara kerja:** Bertipe `String` (bukan angka), supaya angka 0 di depan tidak hilang dan tetap bisa menyimpan format seperti `"0812..."`. Juga `private`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Bisa dipakai logika pencarian pelanggan (misalnya saat kasir mencari pelanggan berdasarkan nomor HP), diakses lewat `getNoTelp()`.
- **Data masuk → keluar:** Masuk dari constructor/setter, keluar lewat `getNoTelp()`.

---

### Field `member`

```java
private boolean member;    // true = member (dapat diskon)
```

- **Untuk apa:** Menyimpan status apakah pelanggan ini member atau bukan. Ini field paling penting di class ini.
- **Cara kerja:** Bertipe `boolean`, jadi nilainya hanya `true` atau `false`. Kalau `true` = member (dapat diskon), kalau `false` = pelanggan biasa (tidak dapat diskon).
- **Nanti dipakai untuk apa / dipanggil siapa:** Inilah dasar penentu diskon. Method `cekStatusMember()` mengembalikan nilai ini, dan `Transaksi.terapkanDiskon()` memakainya untuk memutuskan apakah diskon 5% diberikan.
- **Data masuk → keluar:** Masuk dari constructor/setter, keluar lewat `cekStatusMember()` atau `isMember()`.

---

### Constructor `Pelanggan(...)`

```java
public Pelanggan(String idMember, String nama, String noTelp, boolean member) {
    this.idMember = idMember;
    this.nama = nama;
    this.noTelp = noTelp;
    this.member = member;
}
```

- **Untuk apa:** Constructor adalah method khusus yang dijalankan saat kita **membuat objek baru** dengan `new Pelanggan(...)`. Tugasnya mengisi semua field sekaligus saat objek lahir, supaya pelanggan langsung lengkap datanya.
- **Cara kerja:**
  1. Menerima 4 parameter: `idMember`, `nama`, `noTelp`, dan `member`.
  2. Baris `this.idMember = idMember;` artinya: ambil nilai parameter `idMember` lalu simpan ke field `idMember` milik objek ini. Kata kunci `this` dipakai untuk membedakan **field objek** (`this.idMember`) dari **parameter** (`idMember`) yang kebetulan namanya sama.
  3. Begitu juga untuk `nama`, `noTelp`, dan `member`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil oleh kode mana pun yang mau membuat pelanggan, misalnya `new Pelanggan("MBR-01", "Andi", "0812...", true)`. Objek hasilnya lalu dimasukkan ke `Transaksi` sebagai pelanggan yang bertransaksi.
- **Data masuk → keluar:** Masuk: 4 nilai (id, nama, no telp, status member). Keluar: sebuah objek `Pelanggan` yang sudah terisi datanya (constructor tidak mengembalikan nilai biasa, tetapi menghasilkan objek baru).

---

### Method `cekStatusMember()`

```java
// true kalau member -> dipakai buat nentuin diskon
public boolean cekStatusMember() {
    return member;
}
```

- **Untuk apa:** Memberi tahu pihak luar apakah pelanggan ini member atau bukan, dalam bentuk `true`/`false`.
- **Cara kerja:** Sangat sederhana — langsung mengembalikan nilai field `member`. Tidak ada perhitungan tambahan. Jika `member` bernilai `true`, method ini mengembalikan `true`; jika `false`, mengembalikan `false`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Ini "jembatan" utama ke logika diskon. Di `Transaksi`:
  - `terapkanDiskon()` memanggil `pelanggan.cekStatusMember()`. Kalau `true`, diskon dihitung sebesar `subtotal * DISKON_MEMBER` (5%); kalau `false`, diskon = 0.
  - `cetakStruk()` juga memanggilnya untuk menampilkan label `(Member)` dan persentase diskon di struk.
- **Data masuk → keluar:** Masuk: tidak ada parameter. Keluar: `boolean` (`true` = member, `false` = bukan member).

> Catatan: secara fungsi, `cekStatusMember()` dan `isMember()` mengembalikan hasil yang sama (nilai field `member`). Bedanya hanya gaya penamaan; `cekStatusMember()` dibuat dengan nama yang lebih "bercerita" untuk dipakai di logika diskon.

---

### Getter & Setter (`getIdMember`/`setIdMember`, `getNama`/`setNama`, `getNoTelp`/`setNoTelp`, `isMember`/`setMember`)

```java
public String getIdMember() { return idMember; }
public void setIdMember(String idMember) { this.idMember = idMember; }

public String getNama() { return nama; }
public void setNama(String nama) { this.nama = nama; }

public String getNoTelp() { return noTelp; }
public void setNoTelp(String noTelp) { this.noTelp = noTelp; }

public boolean isMember() { return member; }
public void setMember(boolean member) { this.member = member; }
```

- **Untuk apa:** Karena semua field bersifat `private` (tertutup), kita butuh "pintu resmi" untuk membaca dan mengubah nilainya dari luar class. **Getter** untuk membaca, **setter** untuk mengubah. Ini bagian dari konsep enkapsulasi di PBO.
- **Cara kerja:**
  - **Getter** (`getIdMember`, `getNama`, `getNoTelp`, `isMember`) hanya mengembalikan nilai field. Untuk field `boolean`, konvensi Java memakai awalan `is...` (makanya namanya `isMember`, bukan `getMember`).
  - **Setter** (`setIdMember`, `setNama`, `setNoTelp`, `setMember`) menerima satu nilai baru lalu menyimpannya ke field memakai `this.namaField = parameter;`. Setter bertipe `void`, jadi tidak mengembalikan apa pun.
- **Nanti dipakai untuk apa / dipanggil siapa:**
  - `getNama()` dipanggil `Transaksi.cetakStruk()` untuk menulis nama pelanggan di struk.
  - `getNoTelp()` berguna saat mencari pelanggan berdasarkan nomor HP.
  - Setter berguna kalau data pelanggan perlu diperbarui setelah objek dibuat, misalnya pelanggan baru mendaftar jadi member → `setMember(true)`.
- **Data masuk → keluar:**
  - Getter: masuk tidak ada parameter, keluar nilai field.
  - Setter: masuk satu nilai sesuai tipe, keluar tidak ada (`void`).

## 4. Contoh alur nyata

Misalkan datang pelanggan bernama **Andi** yang berstatus **member**. Mari ikuti alur datanya sampai jadi total bayar.

1. **Buat objek Pelanggan:**
   ```java
   Pelanggan andi = new Pelanggan("MBR-01", "Andi", "0812-3456-7890", true);
   ```
   Constructor mengisi field: `idMember = "MBR-01"`, `nama = "Andi"`, `noTelp = "0812-3456-7890"`, `member = true`.

2. **Andi laundry 3 kg, harga layanan Rp8.000/kg.** Objek `andi` dimasukkan ke sebuah `Transaksi`.

3. **Hitung subtotal** (di `Transaksi.hitungBiaya()`):
   `subtotal = 3 kg × Rp8.000 = Rp24.000`.

4. **Terapkan diskon** (di `Transaksi.terapkanDiskon()`):
   - Method ini memanggil `andi.cekStatusMember()` → mengembalikan `true`.
   - Karena member, diskon = `subtotal × DISKON_MEMBER` = `24.000 × 0,05` = **Rp1.200** (diskon member 5%).
   - `totalBayar = 24.000 − 1.200 = Rp22.800`.

5. **Hasil akhir:** Andi membayar **Rp22.800**. Jika Andi bukan member (`member = false`), maka `cekStatusMember()` mengembalikan `false`, diskon = 0, dan total bayar tetap Rp24.000.

Jadi peran `Pelanggan` di alur ini adalah **menjawab pertanyaan "apakah dapat diskon?"** lewat `cekStatusMember()`, dan **menyediakan nama** lewat `getNama()` untuk dicetak di struk.

## 5. Hubungan dengan file lain

- **`Transaksi.java`** (file yang paling erat hubungannya): `Transaksi` menyimpan satu objek `Pelanggan` (field `private Pelanggan pelanggan;`). `Transaksi` memanggil:
  - `pelanggan.cekStatusMember()` di method `terapkanDiskon()` untuk menentukan apakah diskon 5% (`DISKON_MEMBER = 0.05`) diberikan.
  - `pelanggan.cekStatusMember()` dan `pelanggan.getNama()` di method `cetakStruk()` untuk menampilkan label `(Member)` dan nama pelanggan di struk.
- **`BillingManager.java`**: bertugas mengelola kumpulan transaksi. Karena setiap `Transaksi` membawa objek `Pelanggan`, secara tidak langsung `BillingManager` juga ikut "memegang" data pelanggan lewat transaksi-transaksinya.
- **`Main.java`**: titik awal program modul standalone. Biasanya di sinilah objek `Pelanggan` dibuat dengan `new Pelanggan(...)`, lalu dipakai untuk membuat `Transaksi`.
- **`Layanan` (modul servicecatalog, milik Shellyn)**: tidak dipakai langsung oleh `Pelanggan`, tapi berdampingan di dalam `Transaksi`. `Transaksi` memakai `Layanan` (untuk harga per kg) dan `Pelanggan` (untuk status member) bersama-sama saat menghitung biaya.

Catatan teknis: di **modul standalone** ini, semua nilai uang memakai tipe `double` (lihat `Transaksi`). Pada **layer web**, perhitungan uang umumnya memakai `BigDecimal` agar lebih presisi. Class `Pelanggan` sendiri tidak menyimpan nilai uang, jadi perbedaan tipe ini tidak berdampak langsung padanya — tapi penting diingat saat melihat keseluruhan modul Payment & Billing.

OK C:/Users/raihan/Documents/GitHub/java-smart-laundry/.notesraihan/code/01-Pelanggan.md
