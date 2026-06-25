package com.laundry.smartlaundry.module.servicecatalog;

public class Layanan {
    private String idLayanan;
    private String namaPaket;
    private double hargaPerKg;
    private int estimasiWaktu;

    public Layanan(String idLayanan, String namaPaket, double hargaPerKg, int estimasiWaktu) {
        this.idLayanan = idLayanan;
        this.namaPaket = namaPaket;
        this.hargaPerKg = hargaPerKg;
        this.estimasiWaktu = estimasiWaktu;
    }

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

    public String getDetailLayanan() {
        return String.format("ID: %s | Paket: %-15s | Harga/Kg: Rp%,.2f | Estimasi: %d hari", 
                             idLayanan, namaPaket, hargaPerKg, estimasiWaktu);
    }


    public void updateHarga(double hargaBaru) {
        if (hargaBaru >= 0) {
            this.hargaPerKg = hargaBaru;
            System.out.println("Info: Harga untuk paket " + this.namaPaket + " berhasil diupdate menjadi Rp" + this.hargaPerKg);
        } else {
            System.out.println("Error: Harga tidak boleh negatif.");
        }
    }
}
