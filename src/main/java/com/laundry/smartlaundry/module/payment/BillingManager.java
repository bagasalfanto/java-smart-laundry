package com.laundry.smartlaundry.module.payment;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;

/**
 * Class BillingManager adalah "otak" dari modul Payment &amp; Billing (bagian Raihan).
 *
 * <p>Class ini menyatukan dua fitur utama:</p>
 * <ol>
 *   <li><b>Billing Logic</b> &rarr; membuat tagihan dan memproses pembayaran,
 *       termasuk perhitungan biaya otomatis berdasarkan berat, jenis layanan,
 *       dan status member.</li>
 *   <li><b>Transaction History</b> &rarr; menyimpan semua transaksi yang sudah
 *       LUNAS, lalu menyediakan fitur pencarian (cari berdasarkan invoice atau
 *       nama/nomor telepon pelanggan).</li>
 * </ol>
 */
public class BillingManager {

    /**
     * Daftar penyimpanan transaksi yang SUDAH LUNAS.
     * Hanya transaksi lunas yang masuk ke sini, sehingga list ini sekaligus
     * berperan sebagai "Riwayat Transaksi" (Transaction History).
     */
    private List<Transaksi> riwayatLunas;

    public BillingManager() {
        this.riwayatLunas = new ArrayList<>();
    }

    // ==========================================
    // FITUR 1: BILLING LOGIC
    // ==========================================

    /**
     * Membuat tagihan baru sekaligus menghitung biayanya secara otomatis.
     *
     * <p>Urutan kerjanya: buat objek Transaksi &rarr; hitung subtotal
     * &rarr; terapkan diskon member (jika ada) &rarr; tampilkan rincian.</p>
     *
     * @param idOrder   nomor invoice unik
     * @param pelanggan pelanggan yang melakukan transaksi
     * @param layanan   paket layanan yang dipilih
     * @param berat     berat cucian dalam kg
     * @param tglMasuk  tanggal cucian masuk
     * @return objek Transaksi yang biayanya sudah dihitung (status: BELUM LUNAS)
     */
    public Transaksi buatTagihan(String idOrder, Pelanggan pelanggan, Layanan layanan,
                                 double berat, String tglMasuk) {
        Transaksi trx = new Transaksi(idOrder, pelanggan, layanan, berat, tglMasuk);

        // Inilah inti Billing Logic: dua langkah perhitungan.
        trx.hitungBiaya();      // langkah 1: subtotal = berat x harga/kg
        trx.terapkanDiskon();   // langkah 2: kurangi diskon member -> total bayar

        System.out.println("[BILLING] Tagihan " + idOrder + " dibuat untuk "
                + pelanggan.getNama() + ".");
        System.out.printf("          Subtotal Rp%,.2f - Diskon Rp%,.2f = Total Rp%,.2f%n",
                trx.getSubtotal(), trx.getDiskon(), trx.getTotalBayar());
        return trx;
    }

    /**
     * Memproses pembayaran sebuah transaksi.
     *
     * <p>Setelah dibayar, status transaksi menjadi LUNAS dan transaksi
     * langsung DISIMPAN ke dalam riwayat (Transaction History). Member juga
     * mendapat tambahan poin loyalitas sebagai bonus.</p>
     *
     * @param trx transaksi yang ingin dibayar
     */
    public void prosesPembayaran(Transaksi trx) {
        if (trx.isLunas()) {
            System.out.println("[BILLING] Transaksi " + trx.getIdOrder()
                    + " sudah lunas sebelumnya. Tidak diproses ulang.");
            return;
        }

        // Pastikan biaya sudah dihitung dan masuk akal sebelum menerima pembayaran.
        // Mencegah transaksi tersimpan dengan total Rp0 (mis. lupa hitung biaya,
        // atau berat cucian 0 kg). Gunakan buatTagihan() yang sudah menghitung otomatis.
        if (!trx.isSudahDihitung() || trx.getTotalBayar() <= 0) {
            System.out.println("[BILLING] Gagal: biaya belum dihitung atau total Rp0 "
                    + "(cek berat cucian). Gunakan buatTagihan() terlebih dahulu.");
            return;
        }

        trx.prosesPembayaran();        // ubah status menjadi LUNAS
        riwayatLunas.add(trx);         // SIMPAN TRANSAKSI LUNAS ke riwayat

        // Bonus: member mendapat 1 poin untuk setiap Rp10.000 yang dibayar.
        if (trx.getPelanggan().cekStatusMember()) {
            int poinDidapat = (int) (trx.getTotalBayar() / 10000);
            trx.getPelanggan().tambahPoin(poinDidapat);
        }

        System.out.println("[BILLING] Pembayaran " + trx.getIdOrder()
                + " LUNAS dan tersimpan di riwayat.");
    }

    // ==========================================
    // FITUR 2: TRANSACTION HISTORY (pencarian transaksi lunas)
    // ==========================================

    /**
     * Mencari satu transaksi lunas berdasarkan nomor invoice (pencarian persis).
     *
     * @param idOrder nomor invoice yang dicari
     * @return Optional berisi Transaksi jika ditemukan, atau kosong jika tidak ada
     */
    public Optional<Transaksi> cariByInvoice(String idOrder) {
        return riwayatLunas.stream()
                .filter(trx -> trx.getIdOrder().equalsIgnoreCase(idOrder))
                .findFirst();
    }

    /**
     * Mencari transaksi lunas berdasarkan nama ATAU nomor telepon pelanggan.
     * Pencarian bersifat sebagian (mengandung kata kunci) dan tidak peduli
     * huruf besar/kecil, sehingga lebih mudah digunakan kasir.
     *
     * @param kataKunci potongan nama atau nomor telepon
     * @return daftar transaksi yang cocok (bisa kosong jika tidak ada)
     */
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

    /**
     * Menampilkan seluruh riwayat transaksi lunas dalam bentuk daftar rapi.
     */
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

    /**
     * Menjumlahkan total pendapatan dari seluruh transaksi lunas.
     * Berguna untuk laporan harian (dipakai modul Reporting milik Herdian).
     *
     * @return total seluruh pembayaran yang sudah lunas
     */
    public double hitungTotalPendapatan() {
        double total = 0;
        for (Transaksi trx : riwayatLunas) {
            total += trx.getTotalBayar();
        }
        return total;
    }

    /**
     * @return jumlah transaksi yang tersimpan di riwayat
     */
    public int getJumlahRiwayat() {
        return riwayatLunas.size();
    }
}
