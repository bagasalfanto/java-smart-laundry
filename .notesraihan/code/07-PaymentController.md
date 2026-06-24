# PaymentController.java — Controller route /payments
> Lokasi asli: `src/main/java/com/laundry/smartlaundry/app/controllers/payment/PaymentController.java` · Bagian: Raihan (Payment & Billing) · Layer: Web — Controller (Spring MVC)

## 1. Untuk apa file ini?

File ini adalah sebuah **Controller** Spring MVC. Anggap saja Controller itu seperti "resepsionis" di aplikasi web: tugasnya menerima permintaan (request) dari browser, lalu memutuskan harus ngapain, dan akhirnya mengembalikan halaman (view) atau mengarahkan (redirect) ke halaman lain.

`PaymentController` khusus mengurus segala hal yang berhubungan dengan **URL `/payments`** (halaman Pembayaran). Di halaman ini, kasir/admin bisa:
- Melihat **daftar transaksi** beserta **ringkasan** (total pendapatan, jumlah yang sudah lunas, jumlah yang belum lunas).
- **Mencari** transaksi berdasarkan nomor invoice atau nama pelanggan.
- Menekan tombol **Bayar** untuk menandai sebuah transaksi menjadi LUNAS.

Kenapa file ini perlu ada? Karena di Spring MVC kita memisahkan tugas. Controller **tidak** menghitung apa-apa sendiri dan **tidak** mengakses database langsung. Dia hanya "menerima tamu" lalu mendelegasikan pekerjaan berat ke `PaymentService` (yang berisi logika sebenarnya). Dengan begitu kode jadi rapi: Controller fokus ke urusan web (URL, parameter, balikan halaman), Service fokus ke logika bisnis.

Catatan penting soal tipe uang: di **modul standalone** (versi tanpa web) uang biasanya pakai tipe `double`, tetapi di **layer web ini** uang dihitung pakai `BigDecimal` (lihat `PaymentService`). Itu pilihan yang lebih aman untuk angka uang. Controller sendiri tidak menyentuh angkanya langsung, dia cuma menyalurkan hasil dari Service ke halaman.

## 2. Kode lengkap

```java
package com.laundry.smartlaundry.app.controllers.payment;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.laundry.smartlaundry.app.services.payment.PaymentService;

// Halaman Pembayaran (/payments).
@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    // tampilkan daftar transaksi + ringkasan
    @GetMapping
    public String index(@RequestParam(required = false) String search, Model model) {
        model.addAttribute("transaksiList", paymentService.cariTransaksi(search));
        model.addAttribute("search", search);
        model.addAttribute("totalPendapatan", paymentService.hitungTotalPendapatan());
        model.addAttribute("jumlahLunas", paymentService.jumlahLunas());
        model.addAttribute("jumlahBelumLunas", paymentService.jumlahBelumLunas());
        return "payment/index";
    }

    // tombol Bayar -> proses, lalu balik ke daftar
    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.prosesPembayaran(id);
            redirectAttributes.addFlashAttribute("successMessage", "Pembayaran berhasil! Transaksi sekarang LUNAS.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Gagal memproses pembayaran: " + e.getMessage());
        }
        return "redirect:/payments";
    }
}

```

## 3. Penjelasan detail per bagian

### a) Anotasi kelas: `@Controller` dan `@RequestMapping("/payments")`

```java
@Controller
@RequestMapping("/payments")
public class PaymentController {
```

- **Untuk apa:** Menandai bahwa kelas ini adalah Controller web, dan semua URL di dalamnya berawalan `/payments`.
- **Cara kerja:**
  - `@Controller` memberi tahu Spring, "Tolong kelola kelas ini sebagai komponen Controller (bukan Service atau Repository)." Saat aplikasi mulai, Spring otomatis mendeteksi dan mendaftarkannya.
  - `@RequestMapping("/payments")` menetapkan **prefix URL**. Jadi method di dalamnya akan menempel ke `/payments`. Misalnya method `pay` yang dipetakan ke `/{id}/pay` sebenarnya merespons `/payments/{id}/pay`.
- **Nanti dipakai untuk apa / dipanggil siapa:** Bukan dipanggil oleh kode kita, melainkan oleh framework Spring secara otomatis ketika ada request masuk ke URL `/payments...`. Spring yang "memilih" method mana yang cocok.
- **Data masuk → keluar:** Tidak ada data langsung di level anotasi kelas; ini hanya konfigurasi pemetaan URL.

### b) Field/atribut: `private final PaymentService paymentService;`

```java
private final PaymentService paymentService;
```

- **Untuk apa:** Menyimpan "alat kerja" utama Controller, yaitu `PaymentService`. Service inilah yang punya semua logika pembayaran sebenarnya.
- **Cara kerja:**
  - Kata kunci `final` artinya field ini hanya boleh diisi sekali (di constructor) dan tidak bisa diganti setelahnya. Ini praktik aman supaya dependensinya tidak berubah-ubah.
  - `private` artinya hanya bisa diakses dari dalam kelas ini.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipakai di hampir semua method (`index` dan `pay`) untuk mendelegasikan tugas, misalnya `paymentService.cariTransaksi(...)` atau `paymentService.prosesPembayaran(...)`.
- **Data masuk → keluar:** Sebagai field, tidak menerima/mengembalikan apa pun. Nilainya diisi lewat constructor.

### c) Constructor: `PaymentController(PaymentService paymentService)`

```java
public PaymentController(PaymentService paymentService) {
    this.paymentService = paymentService;
}
```

- **Untuk apa:** Menerima objek `PaymentService` dari luar dan menyimpannya ke field. Ini disebut **constructor injection** (penyuntikan lewat constructor).
- **Cara kerja:**
  - Saat aplikasi start, Spring melihat Controller ini butuh sebuah `PaymentService`. Spring sudah punya objek `PaymentService` siap pakai (karena kelas itu ber-anotasi `@Service`). Spring lalu memanggil constructor ini dan "menyuntikkan" objek tersebut.
  - Baris `this.paymentService = paymentService;` menyalin parameter ke field kelas, supaya bisa dipakai method lain.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil sekali oleh Spring saat membuat objek Controller. Kita tidak pernah memanggilnya manual dengan `new`.
- **Data masuk → keluar:** Masuk: satu objek `PaymentService`. Keluar: tidak ada (constructor tidak mengembalikan nilai), tetapi efeknya field `paymentService` jadi terisi.

### d) Method `index(...)` — menampilkan daftar transaksi + ringkasan

```java
@GetMapping
public String index(@RequestParam(required = false) String search, Model model) {
    model.addAttribute("transaksiList", paymentService.cariTransaksi(search));
    model.addAttribute("search", search);
    model.addAttribute("totalPendapatan", paymentService.hitungTotalPendapatan());
    model.addAttribute("jumlahLunas", paymentService.jumlahLunas());
    model.addAttribute("jumlahBelumLunas", paymentService.jumlahBelumLunas());
    return "payment/index";
}
```

- **Untuk apa:** Menyiapkan dan menampilkan halaman utama Pembayaran: daftar transaksi plus angka-angka ringkasan di atasnya.
- **Cara kerja (langkah demi langkah):**
  1. `@GetMapping` (tanpa argumen) berarti method ini merespons request **GET** ke URL dasar Controller, yaitu `GET /payments`. GET dipakai untuk "membaca/menampilkan" data.
  2. `@RequestParam(required = false) String search` mengambil parameter URL bernama `search`. Karena `required = false`, parameter ini boleh kosong (`null`). Contoh: `/payments` (search null) atau `/payments?search=andi` (search = "andi").
  3. `Model model` adalah "kantong data" yang akan dikirim ke halaman HTML. Apa pun yang kita `addAttribute` bisa dibaca di template.
  4. `paymentService.cariTransaksi(search)` meminta Service mencarikan daftar transaksi. Jika `search` kosong → kembalikan semua transaksi (urut terbaru); jika ada isinya → cari berdasarkan invoice atau nama pelanggan. Hasilnya dimasukkan ke model dengan nama `transaksiList`.
  5. `model.addAttribute("search", search)` mengembalikan teks pencarian ke halaman, supaya kotak pencarian tetap menampilkan kata yang tadi diketik.
  6. `hitungTotalPendapatan()`, `jumlahLunas()`, dan `jumlahBelumLunas()` mengambil angka ringkasan dari Service, lalu dimasukkan ke model sebagai `totalPendapatan`, `jumlahLunas`, dan `jumlahBelumLunas`.
  7. `return "payment/index";` mengembalikan **nama view** (bukan teks biasa). Spring akan mencari template `payment/index` (file HTML/Thymeleaf) lalu menampilkannya beserta data dari model.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil otomatis oleh Spring setiap kali pengguna membuka halaman `/payments` di browser (atau menekan tombol cari yang mengirim `?search=...`). Hasilnya adalah halaman daftar transaksi yang terlihat oleh kasir/admin.
- **Data masuk → keluar:**
  - Masuk: `search` (boleh null) dan objek `Model` (disediakan Spring).
  - Keluar: `String` berisi nama view `"payment/index"`. Data sebenarnya dikirim lewat `model`.

### e) Method `pay(...)` — tombol Bayar, proses pembayaran lalu balik ke daftar

```java
@PostMapping("/{id}/pay")
public String pay(@PathVariable Long id, RedirectAttributes redirectAttributes) {
    try {
        paymentService.prosesPembayaran(id);
        redirectAttributes.addFlashAttribute("successMessage", "Pembayaran berhasil! Transaksi sekarang LUNAS.");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("errorMessage", "Gagal memproses pembayaran: " + e.getMessage());
    }
    return "redirect:/payments";
}
```

- **Untuk apa:** Memproses satu pembayaran ketika tombol **Bayar** ditekan, lalu mengarahkan kembali ke daftar dengan pesan sukses atau gagal.
- **Cara kerja (langkah demi langkah):**
  1. `@PostMapping("/{id}/pay")` berarti method ini merespons request **POST** ke `/payments/{id}/pay`. POST dipakai untuk aksi yang **mengubah** data (di sini: mengubah status jadi LUNAS). Bagian `{id}` adalah bagian URL yang berubah-ubah sesuai transaksi.
  2. `@PathVariable Long id` mengambil angka `id` dari URL. Misal URL `/payments/7/pay` → `id = 7`.
  3. `RedirectAttributes redirectAttributes` dipakai untuk menitipkan pesan yang akan **tetap ada** walaupun kita melakukan redirect ke halaman lain.
  4. Di dalam blok `try`, `paymentService.prosesPembayaran(id)` menyuruh Service menandai transaksi tersebut menjadi LUNAS. Kalau berhasil, sebuah `successMessage` dititipkan via `addFlashAttribute`.
  5. Kalau terjadi error (misalnya transaksi tidak ditemukan, sehingga Service melempar exception), blok `catch` menangkapnya dan menitipkan `errorMessage` berisi penjelasan, termasuk `e.getMessage()` (pesan dari exception).
  6. `return "redirect:/payments";` melakukan **redirect** balik ke halaman daftar `/payments`. Karena pesan dititipkan sebagai *flash attribute*, pesan itu masih bisa ditampilkan satu kali di halaman daftar setelah redirect.
- **Nanti dipakai untuk apa / dipanggil siapa:** Dipanggil otomatis oleh Spring ketika form/tombol Bayar di halaman daftar mengirim POST ke `/payments/{id}/pay`. Setelah selesai, pengguna kembali melihat daftar dengan status transaksi yang sudah diperbarui.
- **Data masuk → keluar:**
  - Masuk: `id` (Long, dari URL) dan `redirectAttributes`.
  - Keluar: `String` berisi instruksi redirect `"redirect:/payments"`. Efek sampingnya: transaksi jadi LUNAS (lewat Service) dan pesan sukses/gagal disiapkan.

## 4. Contoh alur nyata

Misalkan kasir membuka halaman Pembayaran dan ada transaksi milik **Andi** (member) untuk **3 kg @ Rp8.000**.

Perhitungan totalnya (dilakukan di tempat lain saat transaksi dibuat, bukan di Controller ini):
- Subtotal = 3 kg × Rp8.000 = **Rp24.000**
- Diskon member = **5%** (konstanta `DISKON_MEMBER = 0.05`) → 5% × 24.000 = **Rp1.200**
- Total = 24.000 − 1.200 = **Rp22.800**

Alur lewat Controller ini:

1. Kasir membuka `/payments` → Spring memanggil `index(search=null, model)`.
   - `cariTransaksi(null)` mengembalikan semua transaksi (termasuk milik Andi, total Rp22.800, status BELUM_LUNAS).
   - `hitungTotalPendapatan()`, `jumlahLunas()`, `jumlahBelumLunas()` mengisi angka ringkasan.
   - View `payment/index` tampil, menampilkan baris transaksi Andi dengan tombol Bayar.
2. Kasir mengetik "andi" di kotak cari → browser membuka `/payments?search=andi` → `index(search="andi", model)`.
   - `cariTransaksi("andi")` hanya mengembalikan transaksi yang invoice/namanya mengandung "andi".
3. Kasir menekan tombol **Bayar** pada transaksi Andi (misal id = 7) → browser POST ke `/payments/7/pay` → `pay(id=7, redirectAttributes)`.
   - `prosesPembayaran(7)` menandai transaksi jadi LUNAS dan mencatat waktu bayar.
   - `successMessage` = "Pembayaran berhasil! Transaksi sekarang LUNAS." dititipkan.
   - Redirect ke `/payments` → halaman daftar tampil lagi, kini transaksi Andi berstatus LUNAS, total pendapatan bertambah Rp22.800, dan jumlahLunas bertambah 1.

Jika kasir menekan Bayar pada id yang tidak ada (misal `/payments/999/pay`), Service melempar `IllegalArgumentException("Transaksi tidak ditemukan")`, blok `catch` menangkapnya, dan halaman menampilkan `errorMessage` = "Gagal memproses pembayaran: Transaksi tidak ditemukan".

## 5. Hubungan dengan file lain

- **Memakai / memanggil:**
  - `PaymentService` (`app.services.payment.PaymentService`) — Controller mendelegasikan SEMUA logika ke sini lewat method `cariTransaksi`, `hitungTotalPendapatan`, `jumlahLunas`, `jumlahBelumLunas`, dan `prosesPembayaran`.
  - View `payment/index` — template halaman (HTML/Thymeleaf) yang dirender memakai data dari `Model`. View inilah yang menampilkan ringkasan, form pencarian, tabel transaksi, dan tombol Bayar.
  - Komponen Spring: `Model` (membawa data ke view) dan `RedirectAttributes` (membawa pesan flash setelah redirect).

- **Dipakai / dipanggil oleh:**
  - **Spring MVC (DispatcherServlet)** — yang otomatis memanggil method `index`/`pay` sesuai URL dan HTTP method.
  - **Browser pengguna** — secara tidak langsung, melalui klik link/tombol di halaman `/payments`.

- **Rantai lebih lanjut (di balik Service):**
  - `PaymentService` memakai `TransaksiRepository` untuk mengambil/menyimpan data, serta entity `Transaksi` dan enum `PaymentStatus` (LUNAS / BELUM_LUNAS).
  - Ini analog dengan modul standalone: `PaymentService` di sini berperan mirip `BillingManager`, dan `Transaksi` memakai data Layanan & Pelanggan. Bedanya, layer web ini memakai `BigDecimal` untuk uang, sedangkan modul standalone memakai `double`.
```
