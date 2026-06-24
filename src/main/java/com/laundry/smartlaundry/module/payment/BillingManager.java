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
