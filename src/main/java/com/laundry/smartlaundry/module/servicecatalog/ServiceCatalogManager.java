package com.laundry.smartlaundry.module.servicecatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Class ServiceCatalogManager menangani logika bisnis utama untuk modul Service & Catalog.
 * Termasuk di dalamnya fitur Laundry Package CRUD (untuk Admin) dan Price Estimator (untuk Staff).
 */
public class ServiceCatalogManager {
    private List<Layanan> katalogLayanan;

    public ServiceCatalogManager() {
        this.katalogLayanan = new ArrayList<>();
    }

    // ==========================================
    // FITUR 1: LAUNDRY PACKAGE CRUD (ROLE: ADMIN)
    // ==========================================

    /**
     * CREATE: Menambahkan paket layanan baru ke dalam katalog.
     * Operasi ini dikhususkan untuk Admin untuk menjaga konsistensi harga.
     * 
     * @param layanan Objek layanan baru
     */
    public void tambahLayanan(Layanan layanan) {
        if (cariLayananById(layanan.getIdLayanan()).isPresent()) {
            System.out.println("[ADMIN] Gagal: Layanan dengan ID " + layanan.getIdLayanan() + " sudah ada.");
            return;
        }
        katalogLayanan.add(layanan);
        System.out.println("[ADMIN] Sukses menambahkan: " + layanan.getNamaPaket());
    }

    /**
     * READ: Menampilkan semua paket layanan yang tersedia di katalog.
     */
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

    /**
     * UPDATE: Memperbarui data layanan secara lengkap (nama, harga, estimasi waktu).
     * 
     * @param idLayanan     ID layanan yang ingin diubah
     * @param namaPaketBaru Nama paket yang baru
     * @param hargaBaru     Harga per kg yang baru
     * @param estimasiWaktuBaru Estimasi waktu yang baru
     */
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

    /**
     * DELETE: Menghapus paket layanan dari katalog.
     * 
     * @param idLayanan ID layanan yang akan dihapus
     */
    public void hapusLayanan(String idLayanan) {
        Optional<Layanan> layananOpt = cariLayananById(idLayanan);
        if (layananOpt.isPresent()) {
            katalogLayanan.remove(layananOpt.get());
            System.out.println("[ADMIN] Sukses menghapus layanan dengan ID " + idLayanan);
        } else {
            System.out.println("[ADMIN] Gagal: Layanan dengan ID " + idLayanan + " tidak ditemukan.");
        }
    }

    // ==========================================
    // FITUR 2: PRICE ESTIMATOR (ROLE: STAFF)
    // ==========================================

    /**
     * Simulasi perhitungan total harga berdasarkan paket dan berat (dalam kg).
     * Fitur ini digunakan oleh Staff dan hanya memiliki akses baca (read-access) ke data layanan.
     * 
     * @param idLayanan ID layanan yang dipilih pelanggan
     * @param beratKg   Berat cucian pelanggan (dalam kilogram)
     * @return Estimasi total harga. Mengembalikan -1 jika layanan tidak ditemukan.
     */
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

    // ==========================================
    // HELPER METHODS
    // ==========================================

    public Optional<Layanan> getLayananById(String idLayanan) {
        return cariLayananById(idLayanan);
    }

    /**
     * Mencari layanan berdasarkan ID-nya di dalam list.
     * 
     * @param idLayanan ID layanan yang dicari
     * @return Optional yang berisi objek Layanan jika ditemukan
     */
    private Optional<Layanan> cariLayananById(String idLayanan) {
        return katalogLayanan.stream()
                .filter(l -> l.getIdLayanan().equals(idLayanan))
                .findFirst();
    }
}
