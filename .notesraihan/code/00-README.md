# Dokumentasi Kode Raihan — Payment & Billing

Folder ini berisi salinan kode + penjelasan detail untuk semua file kode bagian Raihan.
Setiap file dokumentasi punya pola yang sama:

1. **Untuk apa file ini** — fungsi file di modul/aplikasi.
2. **Kode lengkap** — salinan kode asli apa adanya.
3. **Penjelasan detail per bagian** — field, constructor, method, blok HTML penting.
4. **Contoh alur nyata** — contoh data masuk sampai hasilnya.
5. **Hubungan dengan file lain** — file ini dipakai siapa dan memakai apa.

> Catatan: blok kode di semua file dokumentasi sudah dicek dan **sama persis** dengan file sumber saat dokumentasi dibuat.

---

## Daftar file

| Urutan | Dokumentasi | File asli | Isi utama |
|--------|-------------|-----------|-----------|
| 01 | [`01-Pelanggan.md`](01-Pelanggan.md) | `module/payment/Pelanggan.java` | Data pelanggan + status member |
| 02 | [`02-Transaksi.md`](02-Transaksi.md) | `module/payment/Transaksi.java` | Billing Logic: subtotal, diskon 5%, total bayar, struk |
| 03 | [`03-BillingManager.md`](03-BillingManager.md) | `module/payment/BillingManager.java` | Buat tagihan, proses pembayaran, riwayat transaksi lunas |
| 04 | [`04-Main.md`](04-Main.md) | `module/payment/Main.java` | Demo fitur Billing Logic + Transaction History |
| 05 | [`05-package-info.md`](05-package-info.md) | `module/payment/package-info.java` | Dokumentasi paket modul payment |
| 06 | [`06-PaymentService.md`](06-PaymentService.md) | `app/services/payment/PaymentService.java` | Logika pembayaran versi web |
| 07 | [`07-PaymentController.md`](07-PaymentController.md) | `app/controllers/payment/PaymentController.java` | Route `/payments` dan tombol Bayar |
| 08 | [`08-payment-index-html.md`](08-payment-index-html.md) | `templates/payment/index.html` | Tampilan halaman Pembayaran |

---

## Urutan baca yang disarankan

1. Mulai dari `01-Pelanggan.md` supaya paham data pelanggan dan member.
2. Lanjut `02-Transaksi.md` karena di situ inti rumus billing.
3. Baca `03-BillingManager.md` untuk paham transaksi lunas disimpan dan dicari.
4. Baca `04-Main.md` untuk lihat alur demo dari awal sampai akhir.
5. Kalau mau paham versi web, lanjut `06-PaymentService.md`, `07-PaymentController.md`, lalu `08-payment-index-html.md`.

---

## Ringkasan logic utama

### 1. Billing Logic

Rumus yang dipakai:

```text
subtotal   = berat x hargaPerKg
diskon     = subtotal x 0.05   (kalau member)
totalBayar = subtotal - diskon
```

Contoh:

```text
Andi member, 3 kg, harga Rp8.000/kg
subtotal   = 3 x 8.000 = 24.000
diskon     = 24.000 x 5% = 1.200
totalBayar = 24.000 - 1.200 = 22.800
```

### 2. Transaction History

Yang masuk riwayat hanya transaksi yang sudah **LUNAS**.
Setelah pembayaran berhasil:

```text
status transaksi -> LUNAS
transaksi disimpan ke riwayat
riwayat bisa dicari lewat invoice / nama / no telp
pendapatan dihitung dari totalBayar transaksi lunas
```

### 3. Versi web

Alur web `/payments`:

```text
Browser buka /payments
  -> PaymentController.index()
  -> PaymentService cari data + hitung ringkasan
  -> payment/index.html menampilkan tabel dan tombol Bayar

Klik tombol Bayar
  -> POST /payments/{id}/pay
  -> PaymentController.pay()
  -> PaymentService.prosesPembayaran(id)
  -> status jadi LUNAS + paidAt diisi
  -> kembali ke /payments
```
