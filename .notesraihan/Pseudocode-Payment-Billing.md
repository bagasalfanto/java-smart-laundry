# Pseudocode — Modul Payment & Billing (Raihan)

Algoritma logika modul **Payment & Billing** dalam bentuk pseudocode, ditulis
agar mudah dibaca pemula PBO. Pseudocode ini **sama persis** dengan kode Java di
`module/payment/`. Cocok dipakai di laporan Tugas Besar.

**Notasi yang dipakai:**
- `<-` artinya "diisi dengan" (assignment).
- `FUNGSI` mengembalikan nilai; `PROSEDUR` tidak mengembalikan nilai.
- `JIKA ... MAKA ... SELAIN ITU ... AKHIR JIKA` = percabangan.
- `UNTUK SETIAP x DALAM daftar` = perulangan.
- `TAMPILKAN` = cetak ke layar.

---

## 1. Class Pelanggan (data + status member)

```
FUNGSI cekStatusMember() -> boolean
    KEMBALIKAN member        // true jika pelanggan adalah member

PROSEDUR tambahPoin(tambahan)
    JIKA tambahan > 0 MAKA
        poin <- poin + tambahan
    AKHIR JIKA
```

---

## 2. Class Transaksi (BILLING LOGIC)

**Konstanta:** `DISKON_MEMBER = 0.10`  (member dapat potongan 10%)

```
KONSTRUKTOR Transaksi(idOrder, pelanggan, layanan, berat, tglMasuk)
    simpan idOrder, pelanggan, layanan, tglMasuk

    // Validasi: berat harus lebih dari 0 kg
    JIKA berat <= 0 MAKA
        TAMPILKAN "Peringatan: berat harus > 0 kg. Diatur menjadi 0."
        this.berat <- 0
    SELAIN ITU
        this.berat <- berat
    AKHIR JIKA

    lunas <- false              // transaksi baru = BELUM LUNAS
    sudahDihitung <- false


// Langkah 1 Billing Logic
FUNGSI hitungBiaya() -> angka
    subtotal <- berat * layanan.hargaPerKg
    KEMBALIKAN subtotal


// Langkah 2 Billing Logic
FUNGSI terapkanDiskon() -> angka
    JIKA pelanggan.cekStatusMember() MAKA
        diskon <- subtotal * DISKON_MEMBER      // member: potong 10%
    SELAIN ITU
        diskon <- 0                             // bukan member: tanpa potongan
    AKHIR JIKA
    totalBayar <- subtotal - diskon
    sudahDihitung <- true                       // tandai biaya sudah dihitung
    KEMBALIKAN totalBayar


PROSEDUR prosesPembayaran()
    lunas <- true               // ubah status menjadi LUNAS


PROSEDUR cetakStruk()
    JIKA pelanggan member MAKA persenDiskon <- 10 SELAIN ITU persenDiskon <- 0
    TAMPILKAN nomor invoice, tanggal, nama pelanggan, layanan, berat
    TAMPILKAN "Subtotal     : " + subtotal
    TAMPILKAN "Diskon (" + persenDiskon + "%) : " + diskon
    TAMPILKAN "TOTAL BAYAR  : " + totalBayar
    TAMPILKAN "Status       : " + (LUNAS / BELUM LUNAS)
```

---

## 3. Class BillingManager (orkestrator + TRANSACTION HISTORY)

**Penyimpanan:** `riwayatLunas` = daftar transaksi yang sudah LUNAS.

### Fitur 1 — Billing Logic

```
FUNGSI buatTagihan(idOrder, pelanggan, layanan, berat, tglMasuk) -> Transaksi
    trx <- Transaksi baru(idOrder, pelanggan, layanan, berat, tglMasuk)
    trx.hitungBiaya()           // langkah 1: subtotal
    trx.terapkanDiskon()        // langkah 2: diskon -> total bayar
    TAMPILKAN rincian (subtotal, diskon, total)
    KEMBALIKAN trx


PROSEDUR prosesPembayaran(trx)
    // Penjaga 1: jangan bayar dua kali
    JIKA trx.lunas MAKA
        TAMPILKAN "Sudah lunas, tidak diproses ulang."
        BERHENTI
    AKHIR JIKA

    // Penjaga 2: biaya harus sudah dihitung dan total tidak Rp0
    JIKA (BUKAN trx.sudahDihitung) ATAU (trx.totalBayar <= 0) MAKA
        TAMPILKAN "Gagal: biaya belum dihitung atau total Rp0."
        BERHENTI
    AKHIR JIKA

    trx.prosesPembayaran()          // status -> LUNAS
    tambahkan trx ke riwayatLunas   // SIMPAN TRANSAKSI LUNAS

    // Bonus: member dapat 1 poin tiap Rp10.000
    JIKA trx.pelanggan.cekStatusMember() MAKA
        poin <- bagian bulat dari (trx.totalBayar / 10000)
        trx.pelanggan.tambahPoin(poin)
    AKHIR JIKA

    TAMPILKAN "Pembayaran LUNAS dan tersimpan di riwayat."
```

### Fitur 2 — Transaction History (pencarian transaksi lunas)

```
FUNGSI cariByInvoice(idOrder) -> Transaksi atau KOSONG
    UNTUK SETIAP trx DALAM riwayatLunas
        JIKA trx.idOrder sama dengan idOrder MAKA
            KEMBALIKAN trx
        AKHIR JIKA
    AKHIR UNTUK
    KEMBALIKAN KOSONG


FUNGSI cariByPelanggan(kataKunci) -> daftar Transaksi
    hasil <- daftar kosong
    UNTUK SETIAP trx DALAM riwayatLunas
        JIKA nama ATAU noTelp pelanggan mengandung kataKunci MAKA
            tambahkan trx ke hasil
        AKHIR JIKA
    AKHIR UNTUK
    KEMBALIKAN hasil


PROSEDUR tampilkanSemuaRiwayat()
    JIKA riwayatLunas kosong MAKA
        TAMPILKAN "Belum ada transaksi lunas."
    SELAIN ITU
        UNTUK SETIAP trx DALAM riwayatLunas
            TAMPILKAN idOrder, tgl, nama, totalBayar, status
        AKHIR UNTUK
        TAMPILKAN "Total pendapatan: " + hitungTotalPendapatan()
    AKHIR JIKA


FUNGSI hitungTotalPendapatan() -> angka
    total <- 0
    UNTUK SETIAP trx DALAM riwayatLunas
        total <- total + trx.totalBayar
    AKHIR UNTUK
    KEMBALIKAN total
```

---

## 4. Alur utama (Main / demo)

```
MULAI
    billing <- BillingManager baru

    // siapkan data
    layanan <- Layanan(... harga per kg ...)
    andi    <- Pelanggan(member = true)
    budi    <- Pelanggan(member = false)

    // FITUR 1: Billing Logic
    trxAndi <- billing.buatTagihan("INV-001", andi, cuciSetrika, 3.0, tgl)
    trxBudi <- billing.buatTagihan("INV-002", budi, express,     2.0, tgl)

    // Proses pembayaran + cetak struk
    billing.prosesPembayaran(trxAndi)
    billing.prosesPembayaran(trxBudi)
    trxAndi.cetakStruk()

    // FITUR 2: Transaction History
    billing.tampilkanSemuaRiwayat()
    billing.cariByInvoice("INV-002")
    billing.cariByPelanggan("Andi")
SELESAI
```

**Contoh hasil hitung:**
- Andi (member), 3 kg @ Rp8.000 → subtotal 24.000 − diskon 2.400 = **21.600** (+2 poin)
- Budi (bukan member), 2 kg @ Rp12.000 → subtotal 24.000 − diskon 0 = **24.000**
- Total pendapatan riwayat = **45.600**

> Lihat juga: [`Modul-Payment-Billing-Raihan.md`](Modul-Payment-Billing-Raihan.md) untuk ringkasan modul & cara menjalankan.
