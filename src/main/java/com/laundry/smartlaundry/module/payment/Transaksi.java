package com.laundry.smartlaundry.module.payment;

// Kita pakai ulang (reuse) class Layanan milik modul Service & Catalog (Shellyn),
// karena di class diagram, Transaksi memang "memilih" sebuah Layanan.
// Ini contoh kerja sama antar-modul: tidak perlu menulis ulang class Layanan.
import com.laundry.smartlaundry.module.servicecatalog.Layanan;

/**
 * Class Transaksi adalah inti dari modul Payment &amp; Billing.
 *
 * <p>Satu objek Transaksi menyatukan tiga data utama:</p>
 * <ul>
 *   <li><b>Pelanggan</b> &rarr; untuk tahu apakah dia member (dapat diskon).</li>
 *   <li><b>Layanan</b> &rarr; untuk tahu harga per kilogram.</li>
 *   <li><b>Berat</b> cucian (kg) &rarr; untuk dikalikan dengan harga.</li>
 * </ul>
 *
 * <p><b>BILLING LOGIC</b> dijalankan lewat dua method:
 * {@link #hitungBiaya()} lalu {@link #terapkanDiskon()}.</p>
 */
public class Transaksi {

    /**
     * Aturan bisnis: setiap member mendapat diskon 10%.
     * Dibuat sebagai konstanta agar mudah diubah di satu tempat saja.
     * Contoh: ganti ke 0.15 kalau diskon dinaikkan menjadi 15%.
     */
    public static final double DISKON_MEMBER = 0.10; // 10%

    // --- Atribut ---
    private String idOrder;       // nomor invoice, contoh: "INV-001"
    private Pelanggan pelanggan;  // siapa yang melakukan laundry
    private Layanan layanan;      // paket layanan yang dipilih
    private double berat;         // berat cucian dalam kilogram (kg)
    private String tglMasuk;      // tanggal masuk, contoh: "2026-06-24"

    private double subtotal;      // hasil: berat x harga per kg
    private double diskon;        // potongan harga (0 kalau bukan member)
    private double totalBayar;    // hasil akhir: subtotal - diskon
    private boolean lunas;        // status pembayaran: true = sudah lunas
    private boolean sudahDihitung; // true jika hitungBiaya() + terapkanDiskon() sudah dijalankan

    /**
     * Constructor untuk membuat transaksi baru (status awal: BELUM LUNAS).
     *
     * @param idOrder   nomor invoice unik
     * @param pelanggan objek pelanggan yang melakukan transaksi
     * @param layanan   paket layanan yang dipilih
     * @param berat     berat cucian dalam kg (harus lebih dari 0)
     * @param tglMasuk  tanggal cucian masuk
     */
    public Transaksi(String idOrder, Pelanggan pelanggan, Layanan layanan, double berat, String tglMasuk) {
        this.idOrder = idOrder;
        this.pelanggan = pelanggan;
        this.layanan = layanan;
        this.tglMasuk = tglMasuk;

        // Validasi sederhana: berat cucian HARUS lebih dari 0 kg.
        // Kalau 0 atau negatif, berat diatur ke 0 dan transaksi tidak akan bisa
        // dibayar (dicegah oleh BillingManager.prosesPembayaran).
        if (berat <= 0) {
            System.out.println("[Peringatan] Berat cucian harus lebih dari 0 kg. Berat diatur menjadi 0.");
            this.berat = 0;
        } else {
            this.berat = berat;
        }

        this.lunas = false; // transaksi baru selalu BELUM LUNAS
    }

    // ==========================================
    // BILLING LOGIC (perhitungan biaya otomatis)
    // ==========================================

    /**
     * Langkah 1 Billing Logic: menghitung subtotal.
     * Rumus: subtotal = berat (kg) x harga per kg dari layanan.
     *
     * @return subtotal sebelum diskon
     */
    public double hitungBiaya() {
        this.subtotal = this.berat * layanan.getHargaPerKg();
        return this.subtotal;
    }

    /**
     * Langkah 2 Billing Logic: menerapkan diskon member lalu menghitung total bayar.
     *
     * <p>Kalau pelanggan adalah member, dia mendapat potongan
     * sebesar {@link #DISKON_MEMBER}. Kalau bukan member, diskon = 0.</p>
     *
     * <p>Catatan: panggil {@link #hitungBiaya()} terlebih dahulu supaya
     * subtotal sudah terisi.</p>
     *
     * @return total bayar setelah diskon
     */
    public double terapkanDiskon() {
        if (pelanggan.cekStatusMember()) {
            this.diskon = this.subtotal * DISKON_MEMBER; // member: potong 10%
        } else {
            this.diskon = 0; // bukan member: tidak ada potongan
        }
        this.totalBayar = this.subtotal - this.diskon;
        this.sudahDihitung = true; // tandai bahwa biaya sudah selesai dihitung
        return this.totalBayar;
    }

    /**
     * Menandai transaksi ini sudah dibayar (status menjadi LUNAS).
     * Dipanggil oleh BillingManager saat proses pembayaran berhasil.
     */
    public void prosesPembayaran() {
        this.lunas = true;
    }

    /**
     * Mencetak struk pembayaran yang rapi ke layar.
     * Menampilkan rincian biaya dari awal sampai total akhir.
     */
    public void cetakStruk() {
        String statusMember = pelanggan.cekStatusMember() ? " (Member)" : "";
        // Persentase pada struk mengikuti kenyataan: member 10%, bukan member 0%.
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

    // ==========================================
    // GETTER (cara membaca data transaksi)
    // ==========================================

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

    /**
     * @return true jika biaya sudah dihitung (hitungBiaya + terapkanDiskon sudah dijalankan)
     */
    public boolean isSudahDihitung() {
        return sudahDihitung;
    }

    /**
     * @return status pembayaran dalam bentuk teks: "LUNAS" atau "BELUM LUNAS"
     */
    public String getStatusBayar() {
        return lunas ? "LUNAS" : "BELUM LUNAS";
    }
}
