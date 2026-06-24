# Modul Payment & Billing — Raihan (Kelompok 2)

Catatan ringkas modul bagian Raihan untuk Tugas Besar PBO *Smart Laundry*.

## Apa yang dikerjakan

Sesuai pembagian tugas, modul ini punya **2 fitur**:

1. **Billing Logic** — menghitung total biaya otomatis dari:
   - **berat** cucian (kg),
   - **jenis layanan** (harga per kg), dan
   - **status member** (member dapat diskon).
2. **Transaction History** — menyimpan & mencari transaksi yang sudah **LUNAS**.

## Lokasi kode

```
src/main/java/com/laundry/smartlaundry/module/payment/
├── Pelanggan.java       # data pelanggan + cekStatusMember()
├── Transaksi.java       # BILLING LOGIC: hitungBiaya(), terapkanDiskon(), cetakStruk()
├── BillingManager.java  # orkestrator + TRANSACTION HISTORY (simpan & cari)
├── Main.java            # demo kedua fitur (jalankan ini)
└── package-info.java    # dokumentasi paket
```

Modul ini **Java murni** (tanpa Spring/JPA/Lombok), meniru gaya modul
`module/servicecatalog` milik Shellyn. Uang memakai tipe `double` agar mudah
dipahami pemula. Class `Layanan` **dipakai ulang** dari modul `servicecatalog`
(sesuai relasi agregasi di class diagram: Transaksi "memilih" Layanan).

## Aturan bisnis (Billing Logic)

| Langkah | Rumus |
|---------|-------|
| Subtotal | `subtotal = berat × hargaPerKg` |
| Diskon member | `diskon = subtotal × 0.10` (10%), bukan member = `0` |
| Total bayar | `totalBayar = subtotal − diskon` |
| Poin member (bonus) | 1 poin per Rp10.000 yang dibayar |

Diskon diatur lewat konstanta `Transaksi.DISKON_MEMBER` — ubah di satu tempat
saja kalau persentasenya berganti.

**Validasi (biar data tetap benar):**
- Berat cucian harus `> 0` kg (kalau 0/negatif, transaksi tidak bisa dibayar).
- `prosesPembayaran()` menolak transaksi yang biayanya belum dihitung atau
  totalnya Rp0, supaya riwayat tidak terisi transaksi "kosong".
- Transaksi yang sudah LUNAS tidak bisa dibayar dua kali.

**Contoh** (dari `Main`):
- Andi (member), 3 kg Cuci Setrika @ Rp8.000 → subtotal Rp24.000 − diskon Rp2.400 = **Rp21.600**
- Budi (bukan member), 2 kg Express @ Rp12.000 → subtotal Rp24.000 − diskon Rp0 = **Rp24.000**

## Transaction History

- Hanya transaksi **LUNAS** yang masuk ke daftar `riwayatLunas` (lewat `prosesPembayaran`).
- Pencarian:
  - `cariByInvoice("INV-002")` → cari persis berdasarkan nomor invoice.
  - `cariByPelanggan("Andi")` → cari sebagian berdasarkan nama / no. telepon.
  - `tampilkanSemuaRiwayat()` → daftar semua + total pendapatan.
  - `hitungTotalPendapatan()` → total pemasukan (dipakai modul Reporting/Herdian).

## Cara menjalankan demo

Jalankan class `Main` di paket `module.payment` (butuh JDK 21).
Outputnya membuktikan kedua fitur: perhitungan diskon otomatis lalu
penyimpanan & pencarian riwayat transaksi lunas.

## Konsep PBO yang ditunjukkan

- **Class & Object**: `Pelanggan`, `Layanan`, `Transaksi`.
- **Encapsulation**: atribut `private` + getter/setter.
- **Constructor**: membentuk objek dengan data awal.
- **Reuse / Aggregation**: `Transaksi` menyatukan `Pelanggan` + `Layanan`.
- **Separation of concerns**: data (`Transaksi`) vs. pengelola (`BillingManager`).
