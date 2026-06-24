# payment/index.html — Tampilan halaman Pembayaran (Thymeleaf)
> Lokasi asli: `src/main/resources/templates/payment/index.html` · Bagian: Raihan (Payment & Billing) · Layer: Web — View (Thymeleaf)

## 1. Untuk apa file ini?

File ini adalah **halaman tampilan (View)** untuk fitur **Pembayaran**. Bentuknya HTML, tapi bukan HTML biasa — ini **template Thymeleaf**, artinya ada bagian-bagian yang nanti diisi oleh server (Java/Spring) sebelum halaman dikirim ke browser.

Bayangkan begini: kalau kamu buka aplikasi Smart Laundry lalu klik menu **Pembayaran** (alamat URL-nya `/payments`), inilah halaman yang muncul. Isinya:
- Ringkasan singkat di atas (total pendapatan yang sudah lunas, jumlah transaksi lunas, jumlah transaksi belum lunas).
- Kotak pencarian untuk mencari transaksi berdasarkan nomor invoice atau nama pelanggan.
- Tabel daftar transaksi, lengkap dengan tombol **Bayar** untuk transaksi yang belum lunas.

Kenapa file ini perlu ada? Karena di pola **MVC (Model-View-Controller)**, tugasnya dibagi-bagi: `PaymentController` mengatur logika dan menyiapkan data, `PaymentService` mengolah data dari database, dan **file ini (View)** bertugas khusus **menampilkan** data itu ke layar dengan rapi. Jadi file ini fokus ke tampilan saja — tidak menghitung apa pun sendiri, dia hanya "memamerkan" angka yang sudah dihitung di sisi server.

Catatan penting untuk pemula: file ini **tidak menghitung diskon atau total** sama sekali. Semua angka (subtotal, diskon, total bayar) sudah dihitung lebih dulu di layer web saat transaksi dibuat, lalu disimpan ke database. Halaman ini cuma menampilkan kolom `totalBayar` yang sudah jadi.

## 2. Kode lengkap

```html
<!doctype html>
<html lang="id" xmlns:th="http://www.thymeleaf.org">
<head th:replace="~{layout/base :: head('Pembayaran - Smart Laundry')}"></head>
<body class="app-shell bg-gray-50 text-gray-900" data-app-shell>
    <div class="flex h-screen overflow-hidden">
        <div th:replace="~{layout/sidebar :: sidebar}"></div>
        <div class="relative flex flex-1 flex-col overflow-y-auto overflow-x-hidden">
            <div data-sidebar-overlay class="app-sidebar-overlay fixed inset-0 z-[9] hidden bg-gray-900/50 lg:hidden"></div>
            <div th:replace="~{layout/navbar :: navbar}"></div>
            <main>
                <div class="mx-auto max-w-[1536px] p-4 md:p-6">
                    <!-- notif sukses / gagal -->
                    <div th:if="${successMessage}" id="__swal_success" th:data-msg="${successMessage}" style="display:none"></div>
                    <div th:if="${errorMessage}" id="__swal_error" th:data-msg="${errorMessage}" style="display:none"></div>

                    <div class="mb-6">
                        <h1 class="text-2xl font-bold tracking-tight text-slate-950">Pembayaran</h1>
                        <p class="mt-1 text-sm text-slate-500">Proses pembayaran transaksi dan lihat riwayat transaksi lunas.</p>
                    </div>

                    <!-- ringkasan -->
                    <div class="mb-6 grid grid-cols-1 gap-4 sm:grid-cols-3">
                        <div class="rounded-2xl border border-emerald-200 bg-emerald-50 p-5">
                            <p class="text-sm font-semibold text-emerald-700">Total Pendapatan (Lunas)</p>
                            <p class="mt-2 text-2xl font-bold text-emerald-900" th:text="${'Rp ' + #numbers.formatDecimal(totalPendapatan, 0, 'COMMA', 0, 'POINT')}">Rp 0</p>
                        </div>
                        <div class="rounded-2xl border border-slate-200 bg-white p-5">
                            <p class="text-sm font-semibold text-slate-500">Transaksi Lunas</p>
                            <p class="mt-2 text-2xl font-bold text-slate-900" th:text="${jumlahLunas}">0</p>
                        </div>
                        <div class="rounded-2xl border border-slate-200 bg-white p-5">
                            <p class="text-sm font-semibold text-slate-500">Belum Lunas</p>
                            <p class="mt-2 text-2xl font-bold text-slate-900" th:text="${jumlahBelumLunas}">0</p>
                        </div>
                    </div>

                    <!-- cari invoice / nama -->
                    <div class="mb-4">
                        <form th:action="@{/payments}" method="get" class="flex w-full max-w-sm items-center gap-2">
                            <input type="text" name="search" th:value="${search}" placeholder="Cari invoice atau nama pelanggan..." class="w-full rounded-lg border border-slate-200 px-4 py-2.5 text-sm focus:border-cyan-500 focus:outline-none focus:ring-1 focus:ring-cyan-500 shadow-sm">
                            <button type="submit" class="rounded-lg bg-slate-900 px-5 py-2.5 text-sm font-semibold text-white hover:bg-slate-800 shadow-sm transition-colors">Cari</button>
                            <a th:if="${search != null and !search.isEmpty()}" th:href="@{/payments}" class="rounded-lg border border-slate-200 bg-white px-3 py-2.5 text-sm font-medium text-slate-700 hover:bg-slate-50 shadow-sm transition-colors" title="Reset">
                                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="18" y1="6" x2="6" y2="18"></line><line x1="6" y1="6" x2="18" y2="18"></line></svg>
                            </a>
                        </form>
                    </div>

                    <!-- daftar transaksi -->
                    <section>
                        <div class="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                            <div class="overflow-x-auto">
                                <table class="w-full text-left text-sm text-slate-600">
                                    <thead class="border-b border-slate-200 bg-slate-50 text-xs uppercase text-slate-500">
                                        <tr>
                                            <th class="px-4 py-3 font-semibold">Invoice</th>
                                            <th class="px-4 py-3 font-semibold">Pelanggan</th>
                                            <th class="px-4 py-3 font-semibold">Layanan</th>
                                            <th class="px-4 py-3 font-semibold">Pembayaran</th>
                                            <th class="px-4 py-3 font-semibold text-right">Total</th>
                                            <th class="px-4 py-3 font-semibold text-center">Aksi</th>
                                        </tr>
                                    </thead>
                                    <tbody class="divide-y divide-slate-200">
                                        <tr th:each="trx : ${transaksiList}" class="hover:bg-slate-50">
                                            <td class="whitespace-nowrap px-4 py-3 font-medium text-slate-900" th:text="${trx.invoiceNumber}">INV-000</td>
                                            <td class="px-4 py-3">
                                                <div class="font-medium text-slate-900" th:text="${trx.pelanggan.nama}">Nama</div>
                                                <div class="text-xs text-slate-500" th:text="${trx.pelanggan.noTelp}">08123</div>
                                            </td>
                                            <td class="px-4 py-3">
                                                <div th:text="${trx.layanan.namaPaket}">Paket</div>
                                                <div class="text-xs text-slate-500" th:text="${trx.berat + ' kg'}">1 kg</div>
                                            </td>
                                            <td class="px-4 py-3">
                                                <span th:if="${trx.paymentStatus.name() == 'BELUM_LUNAS'}" class="inline-flex rounded-full bg-red-100 px-2.5 py-0.5 text-xs font-medium text-red-800">Belum Lunas</span>
                                                <span th:if="${trx.paymentStatus.name() == 'LUNAS'}" class="inline-flex rounded-full bg-emerald-100 px-2.5 py-0.5 text-xs font-medium text-emerald-800">Lunas</span>
                                            </td>
                                            <td class="whitespace-nowrap px-4 py-3 text-right font-medium text-slate-900" th:text="${'Rp ' + #numbers.formatDecimal(trx.totalBayar, 0, 'COMMA', 0, 'POINT')}">Rp 0</td>
                                            <td class="px-4 py-3 text-center">
                                                <!-- belum lunas: tombol bayar; lunas: centang -->
                                                <form th:if="${trx.paymentStatus.name() == 'BELUM_LUNAS'}" th:action="@{/payments/{id}/pay(id=${trx.id})}" method="post" class="inline-block" onsubmit="return confirm('Proses pembayaran transaksi ini menjadi LUNAS?');">
                                                    <button type="submit" class="rounded-full bg-emerald-600 px-4 py-1.5 text-xs font-semibold text-white hover:bg-emerald-500 shadow-sm transition-colors">Bayar</button>
                                                </form>
                                                <span th:if="${trx.paymentStatus.name() == 'LUNAS'}" class="text-xs font-medium text-emerald-600">&#10003; Lunas</span>
                                            </td>
                                        </tr>
                                        <tr th:if="${#lists.isEmpty(transaksiList)}">
                                            <td colspan="6" class="px-4 py-8 text-center text-slate-500">Belum ada transaksi.</td>
                                        </tr>
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </section>

                </div>
            </main>
        </div>
    </div>

    <script>
        document.addEventListener('DOMContentLoaded', function() {
            var elSuccess = document.getElementById('__swal_success');
            var elError = document.getElementById('__swal_error');
            if (elSuccess) {
                Swal.fire({ icon: 'success', title: 'Berhasil!', text: elSuccess.getAttribute('data-msg'), confirmButtonColor: '#0e7490', timer: 2500, timerProgressBar: true });
            }
            if (elError) {
                Swal.fire({ icon: 'error', title: 'Gagal!', text: elError.getAttribute('data-msg'), confirmButtonColor: '#0e7490' });
            }
        });
    </script>
</body>
</html>

```

## 3. Penjelasan detail per bagian

### 3.1 Deklarasi dokumen & namespace Thymeleaf
```html
<!doctype html>
<html lang="id" xmlns:th="http://www.thymeleaf.org">
```
- **Untuk apa:** menandai bahwa ini dokumen HTML berbahasa Indonesia (`lang="id"`), dan mendaftarkan "kamus" Thymeleaf lewat `xmlns:th=...`.
- **Cara kerja:** `xmlns:th="http://www.thymeleaf.org"` membuat semua atribut yang berawalan `th:` (seperti `th:text`, `th:if`, `th:each`) dikenali sebagai perintah Thymeleaf, bukan atribut HTML biasa.
- **Nanti dipakai untuk apa:** tanpa deklarasi ini, atribut `th:` tidak akan diproses server dan halaman hanya menampilkan teks placeholder mentah.
- **Data masuk → keluar:** tidak ada data; ini hanya pengaturan dasar dokumen.

### 3.2 Bagian `<head>` (sisip dari layout)
```html
<head th:replace="~{layout/base :: head('Pembayaran - Smart Laundry')}"></head>
```
- **Untuk apa:** memasang bagian `<head>` halaman (judul tab, CSS, dll.) tanpa menulis ulang — diambil dari template bersama.
- **Cara kerja:** `th:replace="~{layout/base :: head(...)}"` artinya "ganti tag ini dengan fragment bernama `head` dari file `layout/base`". Teks `'Pembayaran - Smart Laundry'` dikirim sebagai parameter judul halaman.
- **Nanti dipakai untuk apa:** supaya semua halaman aplikasi konsisten (style sama, library sama). Library SweetAlert (`Swal`) yang dipakai di bagian script kemungkinan juga ikut dimuat dari layout bersama ini.
- **Data masuk → keluar:** masuk = judul halaman (string); keluar = blok `<head>` lengkap.

### 3.3 Kerangka tampilan: sidebar, overlay, navbar
```html
<div th:replace="~{layout/sidebar :: sidebar}"></div>
...
<div data-sidebar-overlay class="... hidden ... lg:hidden"></div>
<div th:replace="~{layout/navbar :: navbar}"></div>
```
- **Untuk apa:** memasang menu samping (sidebar), lapisan gelap untuk mode mobile (overlay), dan bilah atas (navbar).
- **Cara kerja:** sama seperti `<head>`, dua `th:replace` ini menyisipkan fragment dari `layout/sidebar` dan `layout/navbar`. Overlay adalah `<div>` biasa yang awalnya `hidden`, muncul saat sidebar dibuka di layar kecil.
- **Nanti dipakai untuk apa:** memberi navigasi antar halaman (Dashboard, Transaksi, Pembayaran, dst.) agar tampilan seragam di semua halaman.
- **Data masuk → keluar:** tidak ada data dinamis; murni bagian tata letak.

### 3.4 Penanda notifikasi sukses / gagal
```html
<div th:if="${successMessage}" id="__swal_success" th:data-msg="${successMessage}" style="display:none"></div>
<div th:if="${errorMessage}" id="__swal_error" th:data-msg="${errorMessage}" style="display:none"></div>
```
- **Untuk apa:** menyimpan pesan hasil aksi (berhasil/gagal bayar) secara tersembunyi, untuk nanti ditampilkan sebagai popup.
- **Cara kerja:**
  - `th:if="${successMessage}"` → `<div>` ini hanya dibuat kalau variabel `successMessage` ada isinya. Kalau kosong, elemennya tidak ikut dirender sama sekali.
  - `th:data-msg="${successMessage}"` → isi pesan ditaruh di atribut `data-msg`.
  - `style="display:none"` → elemennya tidak terlihat di halaman; hanya jadi "wadah data" untuk JavaScript.
  - Sama persis untuk versi `errorMessage`.
- **Nanti dipakai untuk apa / dipanggil siapa:** dibaca oleh `<script>` di bawah (bagian 3.11) untuk memunculkan popup SweetAlert. Variabel `successMessage`/`errorMessage` dikirim oleh `PaymentController.pay(...)` lewat `RedirectAttributes` (flash attribute) setelah tombol Bayar ditekan.
- **Data masuk → keluar:** masuk = string pesan dari controller; keluar = atribut `data-msg` yang siap dibaca JS.

### 3.5 Judul halaman
```html
<h1 ...>Pembayaran</h1>
<p ...>Proses pembayaran transaksi dan lihat riwayat transaksi lunas.</p>
```
- **Untuk apa:** memberi tahu pengguna sedang berada di halaman apa.
- **Cara kerja:** teks statis biasa, tanpa data dinamis.
- **Nanti dipakai untuk apa:** identitas visual halaman saja.
- **Data masuk → keluar:** tidak ada.

### 3.6 Kartu ringkasan (3 kotak)
```html
<p ... th:text="${'Rp ' + #numbers.formatDecimal(totalPendapatan, 0, 'COMMA', 0, 'POINT')}">Rp 0</p>
...
<p ... th:text="${jumlahLunas}">0</p>
...
<p ... th:text="${jumlahBelumLunas}">0</p>
```
- **Untuk apa:** menampilkan tiga angka ringkasan: total pendapatan dari transaksi lunas, jumlah transaksi lunas, dan jumlah transaksi belum lunas.
- **Cara kerja:**
  - `th:text="..."` mengganti isi teks elemen dengan hasil ekspresi. Teks `Rp 0` / `0` di dalam tag hanya placeholder yang terlihat saat mendesain, akan tertimpa saat dijalankan.
  - `#numbers.formatDecimal(totalPendapatan, 0, 'COMMA', 0, 'POINT')` adalah utilitas Thymeleaf untuk memformat angka. Arti parameternya: minimal 0 digit bagian bulat, pemisah ribuan `COMMA` (yang dengan locale Indonesia tampil sebagai titik), 0 angka di belakang koma, dan `POINT` sebagai pemisah desimal. Hasil akhirnya seperti `Rp 1.250.000`. Lalu digabung dengan teks `'Rp '` di depannya.
  - `jumlahLunas` dan `jumlahBelumLunas` langsung ditampilkan apa adanya (bilangan bulat).
- **Nanti dipakai untuk apa / dipanggil siapa:** tiga variabel ini disiapkan oleh `PaymentController.index(...)` yang memanggil `paymentService.hitungTotalPendapatan()`, `paymentService.jumlahLunas()`, dan `paymentService.jumlahBelumLunas()`.
- **Data masuk → keluar:**
  - masuk = `totalPendapatan` (BigDecimal), `jumlahLunas` (long), `jumlahBelumLunas` (long);
  - keluar = teks angka terformat di layar.

### 3.7 Form pencarian (cari invoice / nama)
```html
<form th:action="@{/payments}" method="get" ...>
    <input type="text" name="search" th:value="${search}" placeholder="Cari invoice atau nama pelanggan..." ...>
    <button type="submit" ...>Cari</button>
    <a th:if="${search != null and !search.isEmpty()}" th:href="@{/payments}" ... title="Reset"> ... </a>
</form>
```
- **Untuk apa:** memungkinkan pengguna mencari transaksi tertentu berdasarkan nomor invoice atau nama pelanggan.
- **Cara kerja:**
  - `th:action="@{/payments}"` + `method="get"` → saat tombol Cari ditekan, browser membuka URL `/payments` dengan menambahkan parameter, misalnya `/payments?search=Andi`.
  - `name="search"` → kata kunci dikirim sebagai parameter bernama `search`.
  - `th:value="${search}"` → kotak input diisi ulang dengan kata kunci yang baru saja dicari, jadi tidak hilang setelah halaman dimuat ulang.
  - Tombol Reset (tag `<a>` berisi ikon silang) `th:if="${search != null and !search.isEmpty()}"` hanya muncul kalau sedang ada kata kunci aktif; mengkliknya membuka `/payments` polos (tanpa parameter) sehingga kembali menampilkan semua transaksi.
- **Nanti dipakai untuk apa / dipanggil siapa:** parameter `search` diterima `PaymentController.index(@RequestParam(required=false) String search, ...)`, lalu diteruskan ke `paymentService.cariTransaksi(search)`. Kalau kosong, service menampilkan semua transaksi; kalau ada isi, mencari lewat invoice atau nama pelanggan.
- **Data masuk → keluar:** masuk (saat render) = `search` (string, bisa null); keluar (saat submit) = request GET ke `/payments?search=...`.

### 3.8 Header tabel transaksi
```html
<thead ...>
    <tr>
        <th ...>Invoice</th>
        <th ...>Pelanggan</th>
        <th ...>Layanan</th>
        <th ...>Pembayaran</th>
        <th ... text-right>Total</th>
        <th ... text-center>Aksi</th>
    </tr>
</thead>
```
- **Untuk apa:** mendefinisikan judul tiap kolom tabel.
- **Cara kerja:** murni statis, hanya label kolom: Invoice, Pelanggan, Layanan, Pembayaran (status), Total, dan Aksi.
- **Nanti dipakai untuk apa:** memberi konteks untuk baris-baris data di bawahnya.
- **Data masuk → keluar:** tidak ada.

### 3.9 Baris data transaksi (perulangan)
```html
<tr th:each="trx : ${transaksiList}" class="hover:bg-slate-50">
    <td ... th:text="${trx.invoiceNumber}">INV-000</td>
    <td ...>
        <div ... th:text="${trx.pelanggan.nama}">Nama</div>
        <div ... th:text="${trx.pelanggan.noTelp}">08123</div>
    </td>
    <td ...>
        <div th:text="${trx.layanan.namaPaket}">Paket</div>
        <div ... th:text="${trx.berat + ' kg'}">1 kg</div>
    </td>
    <td ...>
        <span th:if="${trx.paymentStatus.name() == 'BELUM_LUNAS'}" ...>Belum Lunas</span>
        <span th:if="${trx.paymentStatus.name() == 'LUNAS'}" ...>Lunas</span>
    </td>
    <td ... th:text="${'Rp ' + #numbers.formatDecimal(trx.totalBayar, 0, 'COMMA', 0, 'POINT')}">Rp 0</td>
    ...
</tr>
```
- **Untuk apa:** menampilkan setiap transaksi sebagai satu baris tabel.
- **Cara kerja:**
  - `th:each="trx : ${transaksiList}"` → Thymeleaf mengulang baris `<tr>` ini untuk **setiap** transaksi di dalam daftar `transaksiList`. Variabel `trx` mewakili satu transaksi di tiap putaran.
  - `${trx.invoiceNumber}` menampilkan nomor invoice (memanggil getter `getInvoiceNumber()` dari objek `Transaksi`).
  - `${trx.pelanggan.nama}` dan `${trx.pelanggan.noTelp}` → menelusuri ke objek `Pelanggan` yang tertaut, ambil nama dan nomor telepon.
  - `${trx.layanan.namaPaket}` → nama paket layanan; `${trx.berat + ' kg'}` → berat cucian ditambah teks " kg" (mis. `3 kg`).
  - Kolom Pembayaran: dua `<span>` dengan `th:if` saling bergantian. `trx.paymentStatus.name()` mengubah enum jadi string nama-nya (`"BELUM_LUNAS"` atau `"LUNAS"`). Hanya satu badge yang muncul: merah "Belum Lunas" atau hijau "Lunas".
  - Kolom Total memformat `trx.totalBayar` (nilai akhir yang sudah dihitung saat transaksi dibuat) dengan format yang sama seperti ringkasan.
- **Nanti dipakai untuk apa / dipanggil siapa:** `transaksiList` dikirim oleh `PaymentController.index(...)` dari hasil `paymentService.cariTransaksi(search)`. Daftar ini sudah urut dari yang terbaru (`OrderByCreatedAtDesc`).
- **Data masuk → keluar:** masuk = `transaksiList` (List dari objek `Transaksi`); keluar = baris-baris tabel berisi data tiap transaksi.

### 3.10 Kolom Aksi: tombol Bayar / tanda Lunas
```html
<td ...>
    <form th:if="${trx.paymentStatus.name() == 'BELUM_LUNAS'}" th:action="@{/payments/{id}/pay(id=${trx.id})}" method="post" ... onsubmit="return confirm('Proses pembayaran transaksi ini menjadi LUNAS?');">
        <button type="submit" ...>Bayar</button>
    </form>
    <span th:if="${trx.paymentStatus.name() == 'LUNAS'}" ...>&#10003; Lunas</span>
</td>
```
- **Untuk apa:** menyediakan tombol untuk memproses pembayaran kalau transaksi belum lunas; kalau sudah lunas, hanya menampilkan tanda centang.
- **Cara kerja:**
  - Tombol **Bayar** dibungkus `<form>` yang hanya muncul saat status `BELUM_LUNAS`.
  - `th:action="@{/payments/{id}/pay(id=${trx.id})}"` membentuk alamat dinamis, mis. untuk transaksi ber-id 7 menjadi `/payments/7/pay`. Bagian `{id}` adalah placeholder, lalu `(id=${trx.id})` mengisinya dengan id transaksi tersebut.
  - `method="post"` → ini aksi yang mengubah data (mengubah status jadi lunas), maka pakai POST, bukan GET.
  - `onsubmit="return confirm(...)"` → sebelum dikirim, browser memunculkan kotak konfirmasi "Proses pembayaran transaksi ini menjadi LUNAS?". Kalau pengguna menekan Batal, form tidak jadi dikirim.
  - Kalau status sudah `LUNAS`, yang tampil hanya teks centang `&#10003; Lunas` (karakter ✓), tanpa tombol apa pun — supaya tidak bisa dibayar dua kali.
- **Nanti dipakai untuk apa / dipanggil siapa:** saat ditekan, request POST menuju `PaymentController.pay(@PathVariable Long id, ...)`. Controller memanggil `paymentService.prosesPembayaran(id)`, yang mengubah `paymentStatus` jadi `LUNAS` dan mengisi `paidAt`. Lalu controller mengarahkan kembali (redirect) ke `/payments` sambil membawa pesan sukses/gagal.
- **Data masuk → keluar:** masuk = `trx.id` (Long) dan `trx.paymentStatus` (enum); keluar = request POST `/payments/{id}/pay` (jika diklik) atau sekadar teks "✓ Lunas".

### 3.11 Baris "Belum ada transaksi" (kondisi kosong)
```html
<tr th:if="${#lists.isEmpty(transaksiList)}">
    <td colspan="6" ...>Belum ada transaksi.</td>
</tr>
```
- **Untuk apa:** menampilkan pesan ramah saat daftar transaksi kosong (mis. pencarian tidak menemukan apa pun, atau memang belum ada data).
- **Cara kerja:** `#lists.isEmpty(transaksiList)` mengecek apakah daftar kosong. Kalau ya, baris ini muncul dengan satu sel `colspan="6"` (melebar penuh menutupi 6 kolom).
- **Nanti dipakai untuk apa:** memberi umpan balik agar tabel tidak terlihat "rusak" / kosong tanpa keterangan.
- **Data masuk → keluar:** masuk = `transaksiList`; keluar = satu baris pesan jika daftar kosong.

### 3.12 Script popup notifikasi (SweetAlert)
```html
<script>
    document.addEventListener('DOMContentLoaded', function() {
        var elSuccess = document.getElementById('__swal_success');
        var elError = document.getElementById('__swal_error');
        if (elSuccess) {
            Swal.fire({ icon: 'success', title: 'Berhasil!', text: elSuccess.getAttribute('data-msg'), confirmButtonColor: '#0e7490', timer: 2500, timerProgressBar: true });
        }
        if (elError) {
            Swal.fire({ icon: 'error', title: 'Gagal!', text: elError.getAttribute('data-msg'), confirmButtonColor: '#0e7490' });
        }
    });
</script>
```
- **Untuk apa:** memunculkan popup cantik (SweetAlert) berisi pesan berhasil/gagal setelah aksi bayar.
- **Cara kerja:**
  - `DOMContentLoaded` → kode dijalankan setelah halaman selesai dimuat.
  - Mencari elemen tersembunyi `#__swal_success` dan `#__swal_error` (yang dibuat di bagian 3.4).
  - Kalau elemen sukses ada, `Swal.fire(...)` menampilkan popup hijau "Berhasil!" dengan teks dari `data-msg`, lalu menutup otomatis setelah 2,5 detik (`timer: 2500`).
  - Kalau elemen error ada, menampilkan popup merah "Gagal!" yang harus ditutup manual.
- **Nanti dipakai untuk apa / dipanggil siapa:** menjadi penutup alur tombol Bayar — setelah `PaymentController.pay(...)` redirect dengan flash message, popup inilah yang memberi tahu pengguna hasilnya. `Swal` berasal dari library SweetAlert yang dimuat lewat layout bersama (`layout/base`).
- **Data masuk → keluar:** masuk = teks dari atribut `data-msg`; keluar = popup di layar.

## 4. Contoh alur nyata

Misalkan Andi adalah pelanggan **member**, mencuci **3 kg** dengan layanan tarif **Rp8.000/kg**:

1. Saat transaksi Andi dibuat (di modul/halaman Transaksi, bukan di file ini), sistem menghitung:
   - subtotal = 3 kg × Rp8.000 = **Rp24.000**
   - karena Andi member, diskon = 5% × 24.000 = **Rp1.200** (konstanta `DISKON_MEMBER = 0.05`)
   - total bayar = 24.000 − 1.200 = **Rp22.800**
   Nilai `totalBayar` (Rp22.800) ini disimpan ke database. **Halaman ini tidak menghitung ulang apa-apa**, hanya menampilkan angka jadinya.

2. Andi membuka halaman `/payments`. `PaymentController.index(...)` menyiapkan `transaksiList`, lalu file ini menampilkan satu baris:
   - Invoice: `INV-...`
   - Pelanggan: Andi · nomor telepon
   - Layanan: nama paket · `3 kg`
   - Pembayaran: badge merah **Belum Lunas**
   - Total: **Rp 22.800**
   - Aksi: tombol hijau **Bayar**

3. Petugas menekan **Bayar** → muncul konfirmasi "Proses pembayaran transaksi ini menjadi LUNAS?" → klik OK.
4. Form POST ke `/payments/7/pay` (misal id-nya 7) → `PaymentController.pay(7, ...)` memanggil `paymentService.prosesPembayaran(7)` → status berubah jadi `LUNAS`, `paidAt` terisi waktu sekarang.
5. Halaman diarahkan kembali ke `/payments` dengan pesan sukses. Popup hijau "Berhasil! Pembayaran berhasil! Transaksi sekarang LUNAS." muncul selama 2,5 detik.
6. Baris Andi sekarang menampilkan badge hijau **Lunas** dan tanda **✓ Lunas** (tombol Bayar hilang). Kartu ringkasan "Total Pendapatan (Lunas)" bertambah Rp22.800 dan "Transaksi Lunas" bertambah 1.

## 5. Hubungan dengan file lain

- **Dipanggil oleh:** `PaymentController` (`src/main/java/com/laundry/smartlaundry/app/controllers/payment/PaymentController.java`). Method `index(...)` mengembalikan nama view `"payment/index"` yang menunjuk tepat ke file ini, sekaligus mengisi variabel `transaksiList`, `search`, `totalPendapatan`, `jumlahLunas`, dan `jumlahBelumLunas`.
- **Sumber data / logika:** `PaymentService` (`src/main/java/com/laundry/smartlaundry/app/services/payment/PaymentService.java`) menyediakan `cariTransaksi`, `hitungTotalPendapatan`, `jumlahLunas`, `jumlahBelumLunas`, dan `prosesPembayaran`. Komentar di service ini menyebut dirinya "mirip BillingManager di modul standalone".
- **Model yang ditampilkan:** `Transaksi` (`src/main/java/com/laundry/smartlaundry/app/models/Transaksi.java`). Field yang dipakai di file ini: `invoiceNumber`, `pelanggan` (lalu `nama`, `noTelp`), `layanan` (lalu `namaPaket`), `berat`, `totalBayar`, `paymentStatus`, dan `id`.
- **Enum status:** `PaymentStatus` (`src/main/java/com/laundry/smartlaundry/app/enums/PaymentStatus.java`) dengan dua nilai `BELUM_LUNAS` dan `LUNAS`, yang dibandingkan lewat `paymentStatus.name()` di template.
- **Layout bersama:** `layout/base` (fragment `head`), `layout/sidebar` (fragment `sidebar`), dan `layout/navbar` (fragment `navbar`) disisipkan lewat `th:replace`. Library SweetAlert (`Swal`) yang dipanggil di script kemungkinan dimuat dari `layout/base`.

Catatan tipe uang: di **layer web** ini, nilai uang seperti `totalBayar` dan `totalPendapatan` bertipe **BigDecimal** (lihat field di `Transaksi.java`). Berbeda dengan **modul standalone** Payment & Billing yang memakai tipe **double**. Logika diskon member tetap sama secara konsep, yaitu **5%** (`DISKON_MEMBER = 0.05`), dan dihitung di tempat pembuatan transaksi — bukan di file tampilan ini.
