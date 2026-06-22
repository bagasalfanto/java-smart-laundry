package com.laundry.smartlaundry.module.servicecatalog;

/**
 * Class Layanan merepresentasikan paket layanan laundry yang ditawarkan.
 * Class ini merangkum detail layanan seperti nama paket, harga, dan estimasi waktu.
 * Nantinya class ini akan diagregasi oleh class Transaksi.
 */
public class Layanan {
    private String idLayanan;
    private String namaPaket;
    private double hargaPerKg;
    private int estimasiWaktu;

    /**
     * Constructor untuk membuat objek Layanan baru.
     *
     * @param idLayanan     ID unik layanan
     * @param namaPaket     Nama paket layanan (contoh: "Cuci Kering", "Cuci Setrika")
     * @param hargaPerKg    Harga layanan per kilogram
     * @param estimasiWaktu Estimasi waktu pengerjaan dalam hari
     */
    public Layanan(String idLayanan, String namaPaket, double hargaPerKg, int estimasiWaktu) {
        this.idLayanan = idLayanan;
        this.namaPaket = namaPaket;
        this.hargaPerKg = hargaPerKg;
        this.estimasiWaktu = estimasiWaktu;
    }

    // --- Getters & Setters ---

    public String getIdLayanan() {
        return idLayanan;
    }

    public void setIdLayanan(String idLayanan) {
        this.idLayanan = idLayanan;
    }

    public String getNamaPaket() {
        return namaPaket;
    }

    public void setNamaPaket(String namaPaket) {
        this.namaPaket = namaPaket;
    }

    public double getHargaPerKg() {
        return hargaPerKg;
    }

    public void setHargaPerKg(double hargaPerKg) {
        this.hargaPerKg = hargaPerKg;
    }

    public int getEstimasiWaktu() {
        return estimasiWaktu;
    }

    public void setEstimasiWaktu(int estimasiWaktu) {
        this.estimasiWaktu = estimasiWaktu;
    }

    // --- Special Methods ---

    /**
     * Mengembalikan detail lengkap dari layanan dalam bentuk String yang rapi.
     * @return String detail layanan
     */
    public String getDetailLayanan() {
        return String.format("ID: %s | Paket: %-15s | Harga/Kg: Rp%,.2f | Estimasi: %d hari", 
                             idLayanan, namaPaket, hargaPerKg, estimasiWaktu);
    }

    /**
     * Memperbarui harga layanan per kilogram.
     * Method ini dirancang khusus untuk mempermudah pembaruan harga oleh Admin.
     * 
     * @param hargaBaru Harga baru per kilogram
     */
    public void updateHarga(double hargaBaru) {
        if (hargaBaru >= 0) {
            this.hargaPerKg = hargaBaru;
            System.out.println("Info: Harga untuk paket " + this.namaPaket + " berhasil diupdate menjadi Rp" + this.hargaPerKg);
        } else {
            System.out.println("Error: Harga tidak boleh negatif.");
        }
    }
}
