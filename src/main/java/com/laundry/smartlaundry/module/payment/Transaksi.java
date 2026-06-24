package com.laundry.smartlaundry.module.payment;

// Layanan dipinjam dari modul Shellyn (servicecatalog).
import com.laundry.smartlaundry.module.servicecatalog.Layanan;

// Satu transaksi laundry. Di sini biaya dihitung (billing logic).
public class Transaksi {

    public static final double DISKON_MEMBER = 0.05; // diskon member 5%, ganti di sini kalau berubah

    private String idOrder;
    private Pelanggan pelanggan;
    private Layanan layanan;
    private double berat;          // kg
    private String tglMasuk;

    private double subtotal;
    private double diskon;
    private double totalBayar;
    private boolean lunas;
    private boolean sudahDihitung; // penanda biaya udah dihitung apa belum

    public Transaksi(String idOrder, Pelanggan pelanggan, Layanan layanan, double berat, String tglMasuk) {
        this.idOrder = idOrder;
        this.pelanggan = pelanggan;
        this.layanan = layanan;
        this.tglMasuk = tglMasuk;

        // berat harus > 0, kalau nggak transaksinya nggak bisa dibayar
        if (berat <= 0) {
            System.out.println("[Peringatan] Berat cucian harus lebih dari 0 kg. Berat diatur menjadi 0.");
            this.berat = 0;
        } else {
            this.berat = berat;
        }

        this.lunas = false; // transaksi baru = belum lunas
    }

    // langkah 1: subtotal = berat x harga per kg
    public double hitungBiaya() {
        this.subtotal = this.berat * layanan.getHargaPerKg();
        return this.subtotal;
    }

    // langkah 2: potong diskon kalau member, lalu dapat total bayar
    public double terapkanDiskon() {
        if (pelanggan.cekStatusMember()) {
            this.diskon = this.subtotal * DISKON_MEMBER;
        } else {
            this.diskon = 0;
        }
        this.totalBayar = this.subtotal - this.diskon;
        this.sudahDihitung = true;
        return this.totalBayar;
    }

    public void prosesPembayaran() {
        this.lunas = true;
    }

    // cetak struk ke layar
    public void cetakStruk() {
        String statusMember = pelanggan.cekStatusMember() ? " (Member)" : "";
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

    // getter
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

    public boolean isSudahDihitung() {
        return sudahDihitung;
    }

    // "LUNAS" / "BELUM LUNAS" buat ditampilkan
    public String getStatusBayar() {
        return lunas ? "LUNAS" : "BELUM LUNAS";
    }
}
