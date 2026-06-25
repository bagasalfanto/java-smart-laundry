package com.laundry.smartlaundry.module.servicecatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ServiceCatalogManager {
    private List<Layanan> katalogLayanan;

    public ServiceCatalogManager() {
        this.katalogLayanan = new ArrayList<>();
    }

    public void tambahLayanan(Layanan layanan) {
        if (cariLayananById(layanan.getIdLayanan()).isPresent()) {
            System.out.println("[ADMIN] Gagal: Layanan dengan ID " + layanan.getIdLayanan() + " sudah ada.");
            return;
        }
        katalogLayanan.add(layanan);
        System.out.println("[ADMIN] Sukses menambahkan: " + layanan.getNamaPaket());
    }

    public void tampilkanSemuaLayanan() {
        System.out.println("\n=== KATALOG PAKET LAUNDRY ===");
        if (katalogLayanan.isEmpty()) {
            System.out.println("Katalog layanan masih kosong.");
        } else {
            for (Layanan lay : katalogLayanan) {
                System.out.println(lay.getDetailLayanan());
            }
        }
        System.out.println("=============================");
    }

    public void updateLayanan(String idLayanan, String namaPaketBaru, double hargaBaru, int estimasiWaktuBaru) {
        Optional<Layanan> layananOpt = cariLayananById(idLayanan);
        if (layananOpt.isPresent()) {
            Layanan lay = layananOpt.get();
            lay.setNamaPaket(namaPaketBaru);
            lay.updateHarga(hargaBaru);
            lay.setEstimasiWaktu(estimasiWaktuBaru);
            System.out.println("[ADMIN] Sukses memperbarui data layanan ID: " + idLayanan);
        } else {
            System.out.println("[ADMIN] Gagal: Layanan dengan ID " + idLayanan + " tidak ditemukan.");
        }
    }

    public List<Layanan> getKatalogLayanan() {
        return katalogLayanan;
    }


    public void hapusLayanan(String idLayanan) {
        Optional<Layanan> layananOpt = cariLayananById(idLayanan);
        if (layananOpt.isPresent()) {
            katalogLayanan.remove(layananOpt.get());
            System.out.println("[ADMIN] Sukses menghapus layanan dengan ID " + idLayanan);
        } else {
            System.out.println("[ADMIN] Gagal: Layanan dengan ID " + idLayanan + " tidak ditemukan.");
        }
    }


    public double estimasiTotalHarga(String idLayanan, double beratKg) {
        Optional<Layanan> layananOpt = cariLayananById(idLayanan);
        if (layananOpt.isPresent()) {
            Layanan layanan = layananOpt.get();
            double estimasiHarga = layanan.getHargaPerKg() * beratKg;
            
            System.out.println("\n[STAFF] --- ESTIMASI HARGA ---");
            System.out.println("Paket Terpilih : " + layanan.getNamaPaket());
            System.out.println("Harga per Kg   : Rp" + String.format("%,.2f", layanan.getHargaPerKg()));
            System.out.println("Berat Cucian   : " + beratKg + " Kg");
            System.out.println("Estimasi Waktu : " + layanan.getEstimasiWaktu() + " hari");
            System.out.println("TOTAL ESTIMASI : Rp" + String.format("%,.2f", estimasiHarga));
            System.out.println("------------------------------");
            
            return estimasiHarga;
        } else {
            System.out.println("\n[STAFF] Gagal: Layanan dengan ID " + idLayanan + " tidak ditemukan untuk estimasi.");
            return -1;
        }
    }

    public Optional<Layanan> getLayananById(String idLayanan) {
        return cariLayananById(idLayanan);
    }


    private Optional<Layanan> cariLayananById(String idLayanan) {
        return katalogLayanan.stream()
                .filter(l -> l.getIdLayanan().equals(idLayanan))
                .findFirst();
    }
}
