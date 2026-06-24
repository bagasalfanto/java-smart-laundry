package com.laundry.smartlaundry.module.payment;

import java.util.List;
import java.util.Optional;

import com.laundry.smartlaundry.module.servicecatalog.Layanan;

/**
 * Class Main untuk mendemonstrasikan modul Payment &amp; Billing (bagian: Raihan).
 *
 * <p>Demo ini membuktikan dua fitur:</p>
 * <ol>
 *   <li><b>Billing Logic</b> &rarr; total biaya dihitung otomatis dari berat,
 *       jenis layanan, dan status member (member dapat diskon).</li>
 *   <li><b>Transaction History</b> &rarr; transaksi lunas disimpan lalu dicari
 *       kembali berdasarkan invoice maupun nama pelanggan.</li>
 * </ol>
 */
public class Main {
    public static void main(String[] args) {
        System.out.println("=== SISTEM MANAJEMEN OPERASIONAL LAUNDRY DIGITAL ===");
        System.out.println("Modul: Payment & Billing (Bagian: Raihan)\n");

        BillingManager billing = new BillingManager();

        // --- Data pendukung: paket layanan (dipakai ulang dari modul Shellyn) ---
        Layanan cuciSetrika = new Layanan("LYN-02", "Cuci Setrika", 8000.0, 3);
        Layanan paketExpress = new Layanan("LYN-03", "Express 1 Hari", 12000.0, 1);

        // --- Data pendukung: pelanggan (satu member, satu bukan member) ---
        Pelanggan andi = new Pelanggan("MBR-01", "Andi Wijaya", "081234567890", true);  // member
        Pelanggan budi = new Pelanggan("MBR-02", "Budi Santoso", "082199998888", false); // bukan member

        // ==========================================
        // FITUR 1: BILLING LOGIC
        // ==========================================
        System.out.println(">>> FITUR 1: BILLING LOGIC (perhitungan biaya otomatis)\n");

        // Andi (MEMBER) laundry 3 kg Cuci Setrika -> harusnya dapat diskon 10%.
        System.out.println("[Skenario] Andi (member) menyetrika 3 kg pakaian.");
        Transaksi trxAndi = billing.buatTagihan("INV-001", andi, cuciSetrika, 3.0, "2026-06-24");

        // Budi (BUKAN member) laundry 2 kg Express -> tidak dapat diskon.
        System.out.println("\n[Skenario] Budi (bukan member) ambil paket Express 2 kg.");
        Transaksi trxBudi = billing.buatTagihan("INV-002", budi, paketExpress, 2.0, "2026-06-24");

        // ==========================================
        // PROSES PEMBAYARAN + CETAK STRUK
        // ==========================================
        System.out.println("\n>>> PROSES PEMBAYARAN\n");
        billing.prosesPembayaran(trxAndi);
        billing.prosesPembayaran(trxBudi);

        System.out.println("\n[Skenario] Andi minta dicetakkan struk:");
        trxAndi.cetakStruk();

        // ==========================================
        // FITUR 2: TRANSACTION HISTORY
        // ==========================================
        System.out.println("\n>>> FITUR 2: TRANSACTION HISTORY (penyimpanan & pencarian)\n");

        // Tampilkan seluruh riwayat transaksi lunas.
        billing.tampilkanSemuaRiwayat();

        // Pencarian 1: berdasarkan nomor invoice.
        System.out.println("\n[Skenario] Cari transaksi dengan invoice 'INV-002':");
        Optional<Transaksi> hasilInvoice = billing.cariByInvoice("INV-002");
        if (hasilInvoice.isPresent()) {
            Transaksi t = hasilInvoice.get();
            System.out.println("Ditemukan: " + t.getPelanggan().getNama()
                    + " - Rp" + String.format("%,.2f", t.getTotalBayar()));
        } else {
            System.out.println("Invoice tidak ditemukan.");
        }

        // Pencarian 2: berdasarkan nama pelanggan.
        System.out.println("\n[Skenario] Cari riwayat transaksi atas nama 'Andi':");
        List<Transaksi> hasilNama = billing.cariByPelanggan("Andi");
        if (hasilNama.isEmpty()) {
            System.out.println("Tidak ada transaksi atas nama tersebut.");
        } else {
            for (Transaksi t : hasilNama) {
                System.out.println("Ditemukan: " + t.getIdOrder()
                        + " - Rp" + String.format("%,.2f", t.getTotalBayar()));
            }
        }

        System.out.println("\n=== DEMONSTRASI MODUL SELESAI ===");
    }
}
